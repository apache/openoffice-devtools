################################################################
#
#  Licensed to the Apache Software Foundation (ASF) under one
#  or more contributor license agreements.  See the NOTICE file
#  distributed with this work for additional information
#  regarding copyright ownership.  The ASF licenses this file
#  to you under the Apache License, Version 2.0 (the
#  "License"); you may not use this file except in compliance
#  with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing,
#  software distributed under the License is distributed on an
#  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
#  KIND, either express or implied.  See the License for the
#  specific language governing permissions and limitations
#  under the License.
#
################################################################


# This script queries the SourceForge REST API for download statistics for
# sets of files on SourceForge, on a given date, in ISO format (YYYY-MM-DD)
# passed in as a command line argument. The download count for that date
# is written to stdout.


import urllib
import json
import sys
import datetime
import time

from urllib import urlencode

def getSourceForgeStats(download, startDate, endDate):

    url = download + "/stats/json?start_date=" + startDate + "&" "end_date=" + endDate

    attempts = 0

    while attempts < 3:
        try:    
            conn = urllib.urlopen(url)
            data = conn.read()
            
            return data

        except:
            attempts += 1
            print "error " + str(attempts)

    return ""
    

if len(sys.argv) != 4:
    print "syntax: python platform.py <urls.lst> <start-date> <end-date>"
    print "where <urls.lst> is a list of files URLs to gather stats on, and <start-date> and <end-date> are in YYYY-MM-DD format."
    exit(-1)

downloads = [line.strip() for line in open(sys.argv[1])]

count = 0

windows = 0
mac = 0
linux = 0
android = 0
solaris = 0
bsd = 0
unknown = 0

for download in downloads :

    start_date = sys.argv[2]
    end_date = sys.argv[3]

    print download

    data = json.loads(getSourceForgeStats(download,start_date,end_date))

    oses = data["oses"]

    for os in oses:
        if os[0] == "Windows":
            windows += os[1]
        elif os[0] == "Macintosh":
            mac += os[1]
        elif os[0] == "Linux":
            linux += os[1]
        elif os[0] == "Android":
            android += os[1]
        elif os[0] == "Solaris":
            solaris += os[1]
        elif os[0] == "BSD":
            bsd += os[1]
        elif os[0] == "Unknown":
            unknown += os[1]

print

total = windows + mac + linux + android + solaris + bsd + unknown

print "Windows: " + str(windows) + " (" + "%0.2f" % (100.0*windows/total) + "%)"
print "Macintosh: " + str(mac) + " (" + "%0.2f" % (100.0*mac/total) + "%)"
print "Linux: " + str(linux) + " (" + "%0.2f" % (100.0*linux/total) + "%)"
print "Android: " + str(android) + " (" + "%0.2f" % (100.0*android/total) + "%)"
print "Solaris: " + str(solaris) + " (" + "%0.2f" % (100.0*solaris/total) + "%)"
print "BSD: " + str(bsd) + " (" + "%0.2f" % (100.0*bsd/total) + "%)"
print "Unknown: " + str(unknown) + " (" + "%0.2f" % (100.0*unknown/total) + "%)"

