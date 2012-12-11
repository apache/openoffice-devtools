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
import xmlrpclib
from subprocess import Popen, PIPE


# string constants to get the info for the Apache OpenOffice project
bzsoap = "https://issues.apache.org/ooo/xmlrpc.cgi"
issue_pattern = "^\s*(?:re)?(?:fix)?\s*(?:for)?\s*(?:bug|issue|problem)?\s*#?i?([1-9][0-9][0-9][0-9]+)[#: ]"
bugref_url = "https://issues.apache.org/ooo/show_bug.cgi?id="
infoout_name = "izlist.htm"


class Revision(object):
	"""Constructor for a Revision object"""
	def __init__( self, revnum, author, revlog, dirs_changed):
		self.revnum = revnum
		self.author = author
		self.log    = revlog
		self.dirs_changed = dirs_changed
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

	svncmd = "svn log -v -r%d:%d %s" % (revmin, revmax, svnurl)
	svnout = Popen( svncmd, shell=True, stdout=PIPE).communicate()[0]
	return svnout


def parse_svn_log( svnout):
	"""Parse the output of the svn log command"""
	all_revs = []
	s = svnout
	while True:
		(s,rev) = parse_svn_rev( s)
		if not rev:
			break
		all_revs.append( rev)
	return all_revs


def parse_svn_rev( s):
	"""Parse a revision from the svn log output"""
	# parse the seperator line
	sep_re = re.compile( "-----+")
	sep_line = sep_re.match( s)
	if not sep_line:
		print "SVN revision log separator line not found: \"%s\"" % (s[0:80])
		return (s, None)
	s = s[ sep_line.end()+1:]
	if len(s) == 0:
		return (s, None)
	# parse the revision line
	# :r1418023 | jani | 2012-12-06 19:30:45 +0100 (Thu, 06 Dec 2012) | 2 lines
	rev_pattern = "r(\d+) \| (\w+) \| .* \| (\d+) lines?\n"
	rev_re = re.compile( rev_pattern, re.MULTILINE)
	m_rev = rev_re.match( s)
	if m_rev == None:
		print( "SVN revision header line not found: \"%s\" !!!" % (s[:200]))
		return (s, None)
	revnum = int(m_rev.group(1))
	author = m_rev.group(2)
	linecnt = int(m_rev.group(3))
	s = s[ m_rev.end():]

	# parse changed dirs
	cdirs = []
	cpath_re = re.compile( "Changed paths?:\n", re.MULTILINE)
	cpath_match = cpath_re.match( s)
	if cpath_match:
		s = s[ cpath_match.end():]
		dir_re = re.compile( "\s+([MAD])\s+/?(.*?)$", re.MULTILINE)
		while True:
			m_dir = dir_re.match( s)
			if m_dir == None:
				break
			cdirs.append( m_dir.group(2))
			s = s[ m_dir.end(2):]
		s = s[2:]

	# parse commit comment
	line_re = re.compile( ".*?$", re.MULTILINE)
	idx = 0
	while linecnt > 0:
		linecnt -= 1
		m_line = line_re.match(s,idx+1)
		if m_line == None:
			break
		idx = m_line.end()
	comment = s[:idx]
	s = s[idx+2:]
	return (s,Revision( revnum, author, comment, cdirs))


def revs2info( htmlname, all_revs, svnurl, revmin, revmax):
	"""Create a HTML file with infos about revision range and its referenced issues"""
	# emit html header to the info file
	htmlfile = open( htmlname, "wb")
	branchname = svnurl.split("/")[-1]
	header = "<html><title>Annotated Log for r%d..r%d</title>\n" % (revmin, revmax)
	header += "<body><h1>Revisions %d..%d from <a href=\"%s\">%s</a></h1>\n" % (revmin, revmax, svnurl, branchname)
	htmlfile.write( header)

	# split revisions with issue references from other revisions
	bugid_map = {}
	other_revs = []
	for rev in all_revs:
		if rev.issue:
			if not rev.issue in bugid_map:
				bugid_map[ rev.issue] = []
			bugid_map[ rev.issue].append( rev.revnum)
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
				revurl = revurl_base % (r)
				line += "<a href=\"%s\">c</a>" % (revurl)
			line += "</td>"
			line += "<td>%s</td>" % (bug_target)
			line += "<td>%s</td>" % (bug_status)
			line += "<td>"
			if color:
				line += "<font color=\"%s\">" % (color)
			line += "%s" % (bug_desc.encode('utf-8', 'xmlcharrefreplace'))
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
			line += "<td>%s</td>" % (summary.encode('utf-8'))
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
	revlist = parse_svn_log( svnout)
	revs2info( infoout_name, revlist, svnurl, revmin, revmax)


if __name__ == "__main__":
    main(sys.argv[0:])

