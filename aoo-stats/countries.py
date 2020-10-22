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
    print download
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



# extracts the language code from the URL
# this logic is very sensitive to the exact naming conventions
def getLanguage(url):

    s = str(url)

    if s.endswith('.exe'):
        s = s[:-4]
    elif s.endswith('.dmg'):
        s = s[:-4]
    elif s.endswith('.tar.gz'):
        s = s[:-7]

    return s[s.rfind("_")+1:len(s)]



# dictionary of language code to country_dict (dictionary of country name to count)
master_dict = {}

def mergeCountries(lang, countries):
    country_dict = {}

    if lang in master_dict:
        country_dict = master_dict[lang]
    else:
        master_dict[lang] = country_dict

    for country_tuple in countries:

        country_name = country_tuple[0]
        country_count = country_tuple[1]

        if country_name in country_dict:
            country_dict[country_name] = country_dict[country_name] + country_count
        else:
            country_dict[country_name] = country_count





if len(sys.argv) == 0:
    print "syntax: python countries.py <urls.lst> <start-date> <end-date>"
    print "where <file.list> is a list of files URL's to gather stats on, and <start-date> and <end-date> are in YYYY-MM-DD format."

downloads = [line.strip() for line in open(sys.argv[1])]

for download in downloads :

    data = json.loads(getSourceForgeStats(download, sys.argv[2], sys.argv[3]))

    lang = getLanguage(download)
    #lang = "foo"

    countries = data["countries"]

    mergeCountries(lang, countries)

for lang in master_dict:
    print "===" + lang + "==="
    country_dict = master_dict[lang]
    for country in sorted(country_dict,key=lambda x: country_dict[x], reverse=True):
        out_str = country + "," + str(country_dict[country])
        print out_str.encode("utf-8")
    print
    print

