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
# sets of files on SourceForge, for a range of dates, in ISO format (YYYY-MM-DD)
# passed in as a command line argument. The data, in CSV format is written to stdout.


import urllib
import json
import sys
import datetime

from urllib import urlencode

def getSourceForgeStats(download, startDate, endDate):

    url = download + "/stats/json?start_date=" + startDate + "&" "end_date=" + endDate

    #print >> sys.stderr, url

    attempts = 0

    while attempts < 3:
        try:
            conn = urllib.urlopen(url)
            data = conn.read()

            return data

        except:
            attempts += 1
            print url
            print >> sys.stderr, "error " + download + "(" + str(attempts) + ")"

    return ""


if len(sys.argv) != 4:
    print "syntax: python detail-by-day.py <urls.lst> <start-date> <end-date>"
    print "where <urls.lst> is a list of files URLs to gather stats on, and <start-date> and <end-date> are in YYYY-MM-DD format."
    exit(-1)


downloads = [line.strip() for line in open(sys.argv[1])]
start_date = datetime.datetime.strptime(sys.argv[2], '%Y-%m-%d')
end_date = datetime.datetime.strptime(sys.argv[3], '%Y-%m-%d')

# columns of interest
columns = [ "count_total",
    "count_340", "count_341", "count_400", "count_401", "count_410", "count_411", "count_412", "count_413", "count_414", "count_415", "count_416", "count_417", "count_418", "count_419", "count_4110",
    "windows", "mac", "linux", "linux32", "linux64", "deb", "rpm",
    "ar", "ast", "bg", "ca", "ca-XR", "ca-XV", "cs", "da", "de",
    "el", "en-GB", "en-US", "es", "eu", "fi", "fr", "gd", "gl",
    "he", "hi", "hu", "it", "ja", "km", "ko", "lt", "nb", "nl",
    "pl", "pt", "pt-BR", "ru", "sk", "sl", "sr", "sv", "ta", "th",
    "tr", "vi", "zh-CN", "zh-TW"]

# Column counters are updated if the download name contains a matching pattern.
# The dictionary below maps the column names to these search patterns.
# If there is no entry for a column then the pattern for language columns is assumed.
patternDict = {
    "count_total" : "",
    "count_340"  : "3.4.0",
    "count_341"  : "3.4.1",
    "count_400"  : "4.0.0",
    "count_401"  : "4.0.1",
    "count_410"  : "4.1.0",
    "count_411"  : "4.1.1",
    "count_412"  : "4.1.2",
    "count_413"  : "4.1.3",
    "count_414"  : "4.1.4",
    "count_415"  : "4.1.5",
    "count_416"  : "4.1.6",
    "count_417"  : "4.1.7",
    "count_418"  : "4.1.8",
    "count_419"  : "4.1.9",
    "count_4110" : "4.1.10",
    "windows"    : "Win_x86",
    "mac"        : "MacOS",
    "linux"      : "Linux",
    "linux32"    : "Linux_x86_",
    "linux64"    : "Linux_x86-64_",
    "deb"        : "install-deb_",
    "rpm"        : "install-rpm_"
}


print( '"date","' + '","'.join(columns) + '"')

today = start_date

while today <= end_date:

    counts = dict( [(c,0) for c in columns])

    date_string = today.strftime("%Y-%m-%d")
    print >> sys.stderr, date_string

    for download in downloads :

        try:
            data = json.loads(getSourceForgeStats(download,date_string,date_string))
            day_count = data["total"]
        except ValueError:
            continue

        # update the per column counts
        for c in columns:
            pattern = patternDict[c] if c in patternDict else ("_%s." % c)
            if download.find(pattern) != -1:
               	counts[c] += day_count

    print( date_string + ',' + ','.join( [str(counts[c]) for c in columns]))

    today += datetime.timedelta(days=1)

