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
# sets of files on SourceForge, for a range of dates, in ISO format (YYYY-MM-DD)
# passed in as a command line argument.  The data, in CSV format is written to stdout


import urllib
import json
import sys
import datetime

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
            print url
            print "error " + str(attempts)

    return ""
    
    
if len(sys.argv) != 4:
    print "syntax:  python detail-by-day.py <urls.lst> <start-date> <end-date>"
    print "where <file.list> is a list of files URL's to gather stats on, and <start-date> and <end-date> are in YYYY-MM-DD format."
    exit(-1)


downloads = [line.strip() for line in open(sys.argv[1])]
start_date =  datetime.datetime.strptime(sys.argv[2], '%Y-%m-%d')
end_date = datetime.datetime.strptime(sys.argv[3], '%Y-%m-%d')

print '"date","count_total","count_340","count_341","count_400","count_401","windows","mac","linux","linux32","linux64","deb","rpm","ar","ast","eu","zh_TW","zh_CN","cs","da","nl","en_GB","en_US","fi","fr","gd","gl","de","hu","it","ja","km","ko","nb","pl","pt_BR","ru","sk","sl","es","sv","el","pt","ta","sr","tr","vi"'

today = start_date

while today <= end_date:

    linux32 = 0
    linux64 = 0
    windows = 0
    mac = 0
    linux = 0
    count_total = 0
    count_340 = 0
    count_341 = 0
    count_400 = 0
    count_401 = 0
    deb = 0
    rpm = 0

    ar = 0
    ast = 0 
    eu = 0
    zh_TW = 0
    zh_CN = 0
    cs = 0
    da = 0
    nl = 0
    el = 0
    en_GB = 0
    en_US = 0
    fi = 0
    fr = 0
    gd = 0
    gl = 0
    de = 0
    hu = 0
    it = 0
    ja = 0
    km = 0
    ko = 0
    nb = 0
    pl = 0
    pt = 0
    pt_BR = 0
    ru = 0
    sk = 0
    sl = 0
    es = 0
    sv = 0
    ta = 0
    sr = 0
    tr = 0
    vi = 0

    date_string = today.strftime("%Y-%m-%d")

    for download in downloads :
        
        try:
            data = json.loads(getSourceForgeStats(download,date_string,date_string))
            day_count = data["total"]
        except ValueError:
            data = ""
            day_count = 0

        count_total = count_total + day_count

#versions

        if download.find("3.4.0") != -1:
            count_340 = count_340 + day_count

        if download.find("3.4.1") != -1:
            count_341 = count_341 + day_count

        if download.find("4.0.0") != -1:
            count_400 = count_400 + day_count

        if download.find("4.0.1") != -1:
            count_401 = count_401 + day_count


#platforms

        if download.find("Win_x86") != -1:
            windows = windows + day_count

        if download.find("MacOS") != -1:
            mac = mac + day_count

        if download.find("Linux") != -1:
            linux = linux + day_count

#architecture

        if download.find("Linux_x86_") != -1:
            linux32 = linux32 + day_count

        if download.find("Linux_x86-64_") != -1:
            linux64 = linux64 + day_count

#packaging

        if download.find("install-deb_") != -1:
            deb = deb + day_count

        if download.find("install-rpm_") != -1:
            rpm = rpm + day_count

#languages

        if download.find("_ar.") != -1:
            ar = ar + day_count
        if download.find("_ast.") != -1:
            ast = ast+ day_count
        if download.find("_eu.") != -1:
            eu = eu + day_count
        if download.find("_zh-TW.") != -1:
            zh_TW = zh_TW + day_count
        if download.find("_zh-CN.") != -1:
            zh_CN = zh_CN + day_count
        if download.find("_cs.") != -1:
            cs = cs + day_count
        if download.find("_da.") != -1:
            da = da + day_count
        if download.find("_nl.") != -1:
            nl = nl + day_count
        if download.find("_el.") != -1:
            el = el + day_count
        if download.find("_en-GB.") != -1:
            en_GB = en_GB + day_count
        if download.find("_en-US.") != -1:
            en_US = en_US + day_count
        if download.find("_fi.") != -1:
            fi = fi + day_count
        if download.find("_fr.") != -1:
            fr = fr + day_count
        if download.find("_gd.") != -1:
            gd = gd + day_count
        if download.find("_gl.") != -1:
            gl = gl + day_count
        if download.find("_de.") != -1:
            de = de + day_count
        if download.find("_hu.") != -1:
            hu = hu + day_count
        if download.find("_it.") != -1:
            it = it + day_count
        if download.find("_ja.") != -1:
            ja = ja + day_count
        if download.find("_km.") != -1:
            km = km + day_count
        if download.find("_ko.") != -1:
            ko = ko + day_count
        if download.find("_nb.") != -1:
            nb = nb + day_count
        if download.find("_pl.") != -1:
            pl = pl + day_count
        if download.find("_pt.") != -1:
            pt = pt + day_count
        if download.find("_pt-BR.") != -1:
            pt_BR = pt_BR + day_count
        if download.find("_ru.") != -1:
            ru = ru + day_count
        if download.find("_sk.") != -1:
            sk = sk + day_count
        if download.find("_sl.") != -1:
            sl = sl + day_count
        if download.find("_es.") != -1:
            es = es + day_count
        if download.find("_sv.") != -1:
            sv = sv + day_count
        if download.find("_ta.") != -1:
            ta = ta + day_count
        if download.find("_sr.") != -1:
            sr = sr + day_count
        if download.find("_tr.") != -1:
            tr = tr + day_count
        if download.find("_vi.") != -1:
            vi = vi + day_count


    print date_string + "," + str(count_total) + "," + str(count_340) + "," + str(count_341) + "," + str(count_400) + "," + str(count_401) + "," + \
        str(windows) + "," + str(mac) + "," + str(linux) + "," + str(linux32) + "," + str(linux64) + "," + \
        str(deb) + "," + str(rpm) + "," +  \
        str(ar) + "," + str(ast) + "," + str(eu) + "," + str(zh_TW) + "," + \
        str(zh_CN) + "," + str(cs) + "," + str(da) + "," + \
        str(nl) + "," + str(en_GB) + "," + str(en_US) + "," + \
        str(fi) + "," + str(fr) + "," + str(gd) + "," + \
        str(gl) + "," + str(de) + "," + str(hu) + "," + \
        str(it) + "," + str(ja) + "," + str(km) + "," + \
        str(ko) + "," + str(nb) + "," + str(pl) + "," + str(pt_BR) + "," + \
        str(ru) + "," + str(sk) + "," + str(sl) + "," + \
        str(es) + "," + str(sv) + "," + str(el) + "," + \
        str(pt) + "," + str(ta) + "," + str(sr) + "," + str(tr) + "," + str(vi)


    today += datetime.timedelta(days=1)



