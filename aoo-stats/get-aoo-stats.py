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


# This script queries the SourceForce REST API for download statistics for
# sets of files on SourceForge, on a given date, in ISO format (YYYY-MM-DD)
# passed in as a command line argument.  The download count for that date
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
    
def printSyntax():
    print "syntax:  python get-aoo-stats.py <urls.lst> <iso-date> [<iso-date>]"
    print "where <file.list> is a list of files URL's to gather stats on,"
    print "and <iso-date> is a date of interest, in YYYY-MM-DD format."
    print "If two dates are given this expresses a range of dates."


if len(sys.argv) == 2:
    startDate = datetime.date.today().isoformat()
    endDate = startDate
elif len(sys.argv) == 3:
    startDate = sys.argv[2]
    endDate = startDate
elif len(sys.argv) == 4:
    startDate = sys.argv[2]
    endDate = sys.argv[3]
else:
    printSyntax()
    exit(-1)

#all of the URLs for AOO 3.4 install downloads
downloads = [line.strip() for line in open(sys.argv[1])]

count = 0

for download in downloads :

    try:

        data = json.loads(getSourceForgeStats(download,startDate,endDate))

    except ValueError:
        continue

    day_count = data["total"]

    print download + "," + str(day_count)

    count = count + day_count

print "date range: " +  startDate + " - " + endDate
print "stats_updated: " + data["stats_updated"]
print "count: " + str(count)



