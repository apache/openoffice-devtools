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


import urllib
import json
import os
import hashlib
import datetime
import time
import sys

from urllib import urlencode

def getSourceForgeStats(download, start_date, end_date):

    #print download

    url = download + "/stats/json?start_date=" + start_date + "&" "end_date=" + end_date
    
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

# dictionary of language code to country_dict (dictionary of country name to count)
country_dict = {}

def mergeCountries(countries):

    for country_tuple in countries:

        country_name = country_tuple[0]
        country_count = country_tuple[1]

        if country_name in country_dict:
            country_dict[country_name] = country_dict[country_name] + country_count
        else:
            country_dict[country_name] = country_count


if len(sys.argv) == 0:
    print "syntax: python downloads-by-country.py <urls.lst> <start-date> <end-date>"
    print "where <file.list> is a list of files URL's to gather stats on, and <start-date> and <end-date> are in YYYY-MM-DD format."

downloads = [line.strip() for line in open(sys.argv[1])]

for download in downloads :

    data = getSourceForgeStats(download, sys.argv[2], sys.argv[3])

    #print data

    obj = json.loads(data)

    countries = obj["countries"]

    mergeCountries(countries)


print "<html>"
print "<head>"
print "<title>OpenOffice Downloads by Country/Territory</title>"
print "<html>"
print "<body>"
print "<p>This table shows downloads per <a href='https://en.wikipedia.org/wiki/Country_code_top-level_domain'>country/territory</a> for the period from " + sys.argv[2] + " to " + sys.argv[3] + ".</p>"
print "<table border='1' cellpadding='10'>"

print "<tr>"
print "<th>Rank</th>"
print "<th>Country/Territory</th>"
print "<th>Downloads</th>"
print "</tr>"

rank = 1

for country in sorted(country_dict,key=lambda x: country_dict[x], reverse=True):
    print "<tr>"
    print "<td align='right'>" + "#" + str(rank) + "</td>"
    print "<td>" + country.encode("utf-8") + "</td>"
    print "<td align='right'>" + str(country_dict[country]) + "</td>"
    print "</tr>"
    rank +=1

print"</table>"
print "</body>"

