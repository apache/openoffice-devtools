#! /usr/bin/env python
#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# Svn2Info - Create a HTML file with info about a revision range and connect it with a Bugzilla infos
#
# Usage:
#	python svnlog2info.py [svnurl|branchname] minrev maxrev [enduser|developer]
#
# Example:
#	python svnlog2info.py trunk 1405864 1418409 developer
#	python svnlog2info.py http://svn.apache.org/repos/asf/openoffice/trunk 1405864 1418409 enduser

import sys
import re
import codecs
import xmlrpclib
from subprocess import Popen, PIPE
from xml.dom.minidom import parseString
from xml.sax.saxutils import escape, quoteattr

# string constants specific to the Apache OpenOffice project
# adjust them to your project's needs
svn_default_root_url = "http://svn.apache.org/repos/asf/openoffice/"
svn_viewrev_url_base = "http://svn.apache.org/viewvc?view=revision&revision=%d"
bzsoap = "https://issues.apache.org/ooo/xmlrpc.cgi"
bugref_url = "https://issues.apache.org/ooo/show_bug.cgi?id="

issue_pattern = "^\s*(?:re)?(?:fix)?\s*(?:for)?\s*(?:bug|issue|problem)?\s*#?i?([1-9][0-9][0-9][0-9]+)[#: ]"
infoout_name = "izlist.htm"


class Revision(object):
	"""Constructor for a Revision object"""
	def __init__( self, revnum, author, revlog):
		self.revnum = revnum
		self.author = author
		self.log    = revlog
		self.issue  = get_issue( revlog)


def get_issue( revlog):
	"""Get the issue number referenced in a commit summary"""
	issue_re = re.compile( issue_pattern, re.IGNORECASE)
	issue_match = issue_re.search( revlog)
	if not issue_match:
		return None
	issue_id = int(issue_match.group(1))
	return issue_id


def get_svn_log( svnurl, revmin_name, revmax_name):
	"""Run the svn log command for the requested revision range"""
	svncmd = "svn log --xml -r%s:%s %s" % (revmin_name, revmax_name, svnurl)
	svnproc = Popen( svncmd, shell=True, stdout=PIPE, close_fds=True)
	svnout = svnproc.communicate()
	if svnproc.returncode != 0:
		raise Exception( "SVN LOG failure %d for \"%s\" with \"%s\"" % (svnproc.returncode,svncmd,svnout[1]))
	return svnout[0]


def parse_svn_log_xml( svnout):
	"""Parse the output of the xml-formatted svn log command"""
	all_revs = []

	dom = parseString( svnout)
	for log in dom.getElementsByTagName('logentry'):
		revnum = int(log.getAttribute("revision"))
		author = log.getElementsByTagName("author")[0].firstChild.nodeValue
		cmtnode = log.getElementsByTagName("msg")[0].firstChild
		if cmtnode:	
			comment = cmtnode.nodeValue
		else:
			comment = "UNCOMMENTED CHANGE"
		all_revs.append( Revision( revnum, author, comment))

	return all_revs


def get_bug_details( bugs_to_get):
	proxy = xmlrpclib.ServerProxy( bzsoap, verbose=False)
	# try to get all bug details at once
	try:
		soaprc = proxy.Bug.get( {"ids" : bugs_to_get})
		return soaprc
	except xmlrpclib.Fault as err:
		print( err)
		print( "Problem getting all issue details at once. Retrying each issue individually.")
	# getting the bug details individually
	soaprc = {"bugs":[], "faults":[]}
	for one_id in bugs_to_get:
		try:
			one_bug = proxy.Bug.get( {"ids":[one_id]})["bugs"][0]
		except xmlrpclib.Fault as err:
			one_bug = {'id':one_id, "summary":err.faultString[:32], 'priority':'P3', 'cf_bug_type':'UNKNOWN', 'resolution':'UNKNOWN', 'target_milestone':"UNKNOWN"}
			print( err)
			print( "ignoring #i%d#" % (one_id))
			soaprc["faults"].append( one_id)
		soaprc["bugs"].append( one_bug.copy())

	return soaprc


def revs2info( htmlname, detail_level, all_revs, svnurl, revmin_name, revmax_name):
	"""Create a HTML file with infos about revision range and its referenced issues"""
	# emit html header to the info file
	htmlfile = codecs.open( htmlname, "wb", encoding='utf-8')
	branchname = svnurl.split("/")[-1]
	header = "<html><head><meta charset=\"utf-8\"></head>\n"
	revmin_number = all_revs[+0].revnum
	revmax_number = all_revs[-1].revnum
	revmin_url = svn_viewrev_url_base % (revmin_number)
	revmax_url = svn_viewrev_url_base % (revmax_number)
	header += "<title>Annotated Log for %s..%s</title>\n" % (revmin_name, revmax_name)
	header += "<body><h1>Revisions <a href=\"%s\">%d</a>..<a href=\"%s\">%d</a> from <a href=\"%s\">%s</a></h1>\n" % (revmin_url, revmin_number, revmax_url, revmax_number, svnurl, branchname)
	htmlfile.write( header)

	# split revisions with issue references from other revisions
	bugid_map = {}
	other_revs = []
	for rev in all_revs:
		if rev.issue:
			if not rev.issue in bugid_map:
				bugid_map[ rev.issue] = []
			bugid_map[ rev.issue].append( rev)
		else:
			other_revs.append( rev.revnum)

	# emit info about issues referenced in revisions
	if len(bugid_map) and bzsoap:
		htmlfile.write( "<h2>Issues addressed:</h2>\n<table border=\"0\">\n")

		soaprc = get_bug_details( bugid_map.keys())
		type2prio = {"FEATURE":1, "ENHANCEMENT":2, "PATCH":3, "DEFECT":4, "TASK":5, "UNKNOWN":9}
		sorted_issues = sorted( soaprc["bugs"],
			key = lambda b: type2prio[b["cf_bug_type"]]*1e9 + int(b["priority"][1:])*1e8 + int(b["id"]))

		type2color = {
			"F1":"#0F0", "F2":"#0C0", "F3":"#080", "F4":"#040", "F5":"#020",
			"E1":"#0C8", "E2":"#0A6", "E3":"#084", "E4":"#063", "E5":"#042",
			"D1":"#F00", "D2":"#C00", "D3":"#800", "D4":"#600", "D5":"#300",
			"P1":"#00F", "P2":"#00C", "P3":"#008", "P4":"#006", "P5":"#003",
			"T1":"#0FF", "T2":"#0CC", "T3":"#088", "T4":"#066", "T5":"#063"};
		for bug in sorted_issues:
			idnum = int( bug[ "id"])
			if bugref_url:
				bug_url = bugref_url + str(idnum)
			bug_desc = bug[ "summary"]
			bug_type = bug[ "cf_bug_type"]
			bug_target = bug[ "target_milestone"]
			priority = bug[ "priority"]
			if ("status" in bug):
				bug_status = bug[ "status"]
				if bug_status == "RESOLVED":
					bug_status = bug[ "resolution"]
			else:
				bug_status = "UNKNOWN"

			colortype = bug_type[0]+priority[1]
			if colortype in type2color:
				color = type2color[ colortype]
			else:
				color = None

			idstr = ("#i%d#" if (detail_level >= 3) else "%d") % (idnum)
			line = "<tr>"
			if bug_url:
				line += "<td><a href=\"%s\">%s</a></td>" % (bug_url, idstr)
			else:
				line += "<td>%s</td>" % (idstr)
			if detail_level >= 5:
				line += "<td>%s</td>" % (priority)
			line += "<td>%s</td>" % (bug_type)
			if detail_level >= 9:
				line += "<td>"
				for r in bugid_map[ idnum]:
					revurl = svn_viewrev_url_base % (r.revnum)
					revtitle = r.log.splitlines()[0]
					line += "<a href=\"%s\" title=%s>c</a>" % (revurl, quoteattr(revtitle))
				line += "</td>"
			if detail_level >= 7:
				line += "<td>%s</td>" % (bug_target)
				line += "<td>%s</td>" % (bug_status)

			line += "<td>"
			if color:
				line += "<font color=\"%s\">" % (color)
			line += escape( bug_desc)
			if color:
				line += "</font>"
			line += "<td>"
			line += "</tr>\n"
			htmlfile.write( line)

		htmlfile.write( "</table>\n")

	# emit info about other revisions
	if len(other_revs) and (detail_level >= 6):
		htmlfile.write( "<h2>Commits without Issue References:</h2>\n<table border=\"0\">\n")
		for rev in all_revs:
			if rev.issue:
				continue
			line = "<tr>"

			if svn_viewrev_url_base:
				revurl = svn_viewrev_url_base % (rev.revnum)
				line += "<td><a href=\"%s\">r%d</a></td>" % (revurl, rev.revnum)
			else:
				line += "<td>r%d</td>" % (rev.revnum)

			summary = rev.log.splitlines()[0]
			line += "<td>%s</td>" % (escape(summary))
			line += "</tr>\n"
			htmlfile.write( line)

		htmlfile.write( "</table>\n")

	# emit html footer to the info file
	htmlfile.write( "</body></html>\n")

	# print summary of the HTML file created
	print "Processed %d revisions" % (len(all_revs))
	print "Found %d issues referenced" % (len(bugid_map))
	print "Wrote HTML file \"%s\"" % (htmlname)


def main(args):
	if (len(args) < 4) or (5 < len(args)):
		print "Usage: " + args[0] + " [svnurl|branchname] minrev maxrev [enduser|developer]"
		sys.exit(1)
	svnurl = args[1]
	revmin = args[2]
	revmax = args[3]

	if len(args) >= 5:
		audience = args[4]
	else:
		audience = "developer"

	audience2verbosity = {"enduser":1, "developer":9}
	if audience not in audience2verbosity:
		print "Audience \"%s\" not known! Only \"%s\" can be selected." % (audience,str(audience2verbosity.keys()))
		sys.exit(2)
	detail_level = audience2verbosity[ audience]

	full_url_re = re.compile( "https?://")
	if not full_url_re.match( svnurl):
		svnurl = svn_default_root_url + svnurl

	svnout = get_svn_log( svnurl, revmin, revmax)
	revlist = parse_svn_log_xml( svnout)
	revs2info( infoout_name, detail_level, revlist, svnurl, revmin, revmax)


if __name__ == "__main__":
    main(sys.argv[0:])

