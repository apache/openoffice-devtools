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

# Svn2Info - Create a HTML file with info about a revision range
#
# Example:
#	python svnlog2info.py trunk 1405864 1418409

import sys
import re
import codecs
import xmlrpclib
from subprocess import Popen, PIPE
from xml.dom.minidom import parseString
from xml.sax.saxutils import escape, quoteattr

# string constants to get the info for the Apache OpenOffice project
issue_pattern = "^\s*(?:re)?(?:fix)?\s*(?:for)?\s*(?:bug|issue|problem)?\s*#?i?([1-9][0-9][0-9][0-9]+)[#: ]"
bzsoap = "https://issues.apache.org/ooo/xmlrpc.cgi"
bugref_url = "https://issues.apache.org/ooo/show_bug.cgi?id="
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


def get_svn_log( svnurl, revmin, revmax):
	"""Run the svn log command for the requested revision range"""
	# check input arguments
	if int(revmin) > int(revmax):
		print "start revision %d must be less than end revision %d!" % (revmin, revmax)
		return None

	revmin_limit = 1162288
	revmax_limit = 3000000
	if revmin < revmin_limit:
		print "revision %d is out of range" % (revmin)
		return None
	if revmax_limit < revmax:
		print "revision %d is out of range" % (revmax)
		return None

	svncmd = "svn log --xml -r%d:%d %s" % (revmin, revmax, svnurl)
	svnout = Popen( svncmd, shell=True, stdout=PIPE).communicate()[0]
	return svnout


def parse_svn_log_xml( svnout):
	"""Parse the output of the xml-formatted svn log command"""
	all_revs = []

	dom = parseString( svnout)
	for log in dom.getElementsByTagName('logentry'):
		revnum = int(log.getAttribute("revision"))
		author = log.getElementsByTagName("author")[0].firstChild.nodeValue
		comment = log.getElementsByTagName("msg")[0].firstChild.nodeValue
		all_revs.append( Revision( revnum, author, comment))

	return all_revs


def revs2info( htmlname, all_revs, svnurl, revmin, revmax):
	"""Create a HTML file with infos about revision range and its referenced issues"""
	# emit html header to the info file
	htmlfile = codecs.open( htmlname, "wb", encoding='utf-8')
	branchname = svnurl.split("/")[-1]
	header = "<html><head><meta charset=\"utf-8\"></head>\n"
	header += "<title>Annotated Log for r%d..r%d</title>\n" % (revmin, revmax)
	header += "<body><h1>Revisions %d..%d from <a href=\"%s\">%s</a></h1>\n" % (revmin, revmax, svnurl, branchname)
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

	revurl_base = "http://svn.apache.org/viewvc?view=revision&revision=%d"

	# emit info about issues referenced in revisions
	if len(bugid_map):
		htmlfile.write( "<h2>Issues addressed:</h2>\n<table border=\"0\">\n")
		proxy = xmlrpclib.ServerProxy( bzsoap, verbose=False)
                soaprc = proxy.Bug.get( {"ids" : bugid_map.keys()})
		type2prio = {"FEATURE":1, "ENHANCEMENT":2, "PATCH":3, "DEFECT":4, "TASK":5}
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
			bug_url = bugref_url + str(idnum)
			bug_desc = bug[ "summary"]
			bug_type = bug[ "cf_bug_type"]
			bug_target = bug[ "target_milestone"]
			bug_status = bug[ "resolution"]
			priority = bug[ "priority"]

			colortype = bug_type[0]+priority[1]
			if colortype in type2color:
				color = type2color[ colortype]
			else:
				color = None

			line = "<tr>"
			line += "<td><a href=\"%s\">#i%d#</a></td>" % (bug_url, idnum)
			line += "<td>%s</td>" % (priority)
			line += "<td>%s</td>" % (bug_type)
			line += "<td>"
			for r in bugid_map[ idnum]:
				revurl = revurl_base % (r.revnum)
				revtitle = r.log.splitlines()[0]
				line += "<a href=\"%s\" title=%s>c</a>" % (revurl, quoteattr(revtitle))
			line += "</td>"
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
	if len(other_revs):
		htmlfile.write( "<h2>Commits without Issue References:</h2>\n<table border=\"0\">\n")
		for rev in all_revs:
			if rev.issue:
				continue
			line = "<tr>"
			revurl = revurl_base % (rev.revnum)
			line += "<td><a href=\"%s\">r%d</a></td>" % (revurl, rev.revnum)
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
	if len(args) != 4:
		print "Usage: " + args[0] + "branchname minrev maxrev"
		sys.exit(1)
	branchname = args[1]
	revmin = int(args[2])
	revmax = int(args[3])

	svnurl = "http://svn.apache.org/repos/asf/openoffice/%s" % (branchname)
	svnout = get_svn_log( svnurl, revmin, revmax)
	revlist = parse_svn_log_xml( svnout)
	revs2info( infoout_name, revlist, svnurl, revmin, revmax)


if __name__ == "__main__":
    main(sys.argv[0:])

