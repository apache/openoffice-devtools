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


# This script reads a text file containing AOO Bugzilla ID's and uses
# the Bugzilla REST API to download the XML data for each BZ issue,
# extract and decode and save any attachments.  The XML for each issue
# is cached, so repeated runs can avoid redundant hits to the server.

import urllib
import sys
import datetime
import time

import base64

import xml.etree.ElementTree as ET

from urllib import urlencode

#Smarter caching would look at the modify date of the issue and invalidate the cache if the issue had changed...
def getXMLFromCache(issue):

    fileName = "./cache/" + str(issue) + ".xml"

    try:
    
        file = open(fileName) 

        data = file.read()

        print "R"


    except IOError as e:
        data = ""


    return data


def writeDataToCache(issue,data):

    fileName = "./cache/" + str(issue) + ".xml"

    file = open(fileName, "w")
    file.write(data)
    file.close()

    print "W"


def getXML(issueID):

    url = "https://issues.apache.org/ooo/show_bug.cgi?ctype=xml&id=" + str(issueID)

    attempts = 0

# We get occasional time out errors, so retry up to 3 times
    while attempts < 3:
        try:    
            conn = urllib.urlopen(url)
            data = conn.read()

            return data

        except:
            attempts += 1
            print url
            print "error " + str(attempts)

    return ""
    
    
if len(sys.argv) != 2:
    print "syntax:  python bz-attach-extract.py <issues>"
    print "where <issues> is a text file containing a list of BZ ID's to extract their attachments, one per line"
    exit(-1)

issues = [line.strip() for line in open(sys.argv[1])]

for issue in issues:
    print issue

    cached = False

    data = getXMLFromCache(issue)

    if data=="":
        data = getXML(issue)
        writeDataToCache(issue,data)
    else:
        cached = True

    root = ET.fromstring(data)

    for attachment in root.iter('attachment'):
        mimetype =  attachment.find('type').text

        filename =  attachment.find('filename').text
        
        base64data = attachment.find('data').text

        decoded = base64.b64decode(base64data)
        
        try:

            file = open("./download/" + issue + "_" + filename, "wb")
            file.write(decoded)
            file.close()

        except IOError as e:
            print "I/O error({0}): {1}".format(e.errno, e.strerror)

# We don't want to overload the server or get our IP banned 
    if cached == False:
        time.sleep(15)



