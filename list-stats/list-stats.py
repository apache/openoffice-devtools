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

import sys
import mailbox

def compareItems((w1,c1), (w2,c2)):
    if c1 > c2:
        return - 1
    elif c1 == c2:
        return cmp(w1, w2)
    else:
        return 1

def main(): 
    fileName = sys.argv[1]
    
    box = mailbox.mbox(fileName) 

    authors = {}
    new_threads = {}

    for message in box: 
        author = message['From']

        try:
            authors[author] = authors[author] + 1
        except KeyError:
            authors[author] = 1

        parent = message['In-Reply-To']

        if parent ==  None:
            try:
                new_threads[author] = new_threads[author] + 1
            except KeyError:
                new_threads[author] = 1


        id = message['Message-ID']
        subject = message['Subject']
        #date = message['Date']
        #parent = message['In-Reply-To']
 
    items =authors.items()
    items.sort(compareItems)
    
    for item in items:
        print item[0] + "," + str(item[1])


    print



main()

