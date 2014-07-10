#!/bin/bash
#**************************************************************
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
# *************************************************************
#
# bash script to generate the Update Feeds requested by installed AOO/OOo instance via HTTP/HTTPS
# to check, if a new AOO version is available for the requesting AOO/OOo instance
#
# Needed meta data:
# - information about released AOO/OOo versions - found in file "ProductInfos.txt"
# - information about the languages for which an AOO release exists - found in file "LanguageCodeMap.txt"
# - XML template for a feed entry - found in file "feed-entry-template.Update"
# - XML prefix of the Update Feed - found in file "feed-prefix.Update"
# - translations for string "Only for Mac OS X versions 10.7 or newer" - found in file "AdditionalTextForMacOSX.txt"
#


# check arguements
if [ ${#@} -ne 2 ]; then
    echo Generate Update Feed for installed AOO/OOo instances
    echo Usage: $0 [version accessing the Product Update Feed] [new version]
    echo Output file: check.Update.[version accessing the Product Update Feed]-[new version]
    exit
fi


INSTALLEDVERSION=$1
RELEASE=$2
echo Generate Update Feed for installed $INSTALLEDVERSION OpenOffice instances to upgrade to $RELEASE
echo


# read product information about release AOO/OOo versions
echo -n Reading information about released AOO/OOo versions...
PRODUCTINFO="ProductInfos.txt"
if [ ! -e $PRODUCTINFO ];
then
    echo File $PRODUCTINFO is missing;
    exit;
fi

declare -A productname;
declare -A productbuildid;
declare -A productdownloadurl;
declare -A langlatestrelease;
while read productinfoline
do
    # skip empty lines and comments
    if [[ -n $productinfoline ]] && [[ ${productinfoline:0:1} != "#" ]]; then
        idx=0;
        declare version
        for data in $productinfoline;
        do
            if [ $idx -eq 0 ]; then
                version=$data
                # skip product versions released after the given $RELEASE
                if [[ $version > $RELEASE ]]; then
                    break
                fi
            elif [ $idx -eq 1 ]; then
                productname[$version]=$data
            elif [ $idx -eq 2 ]; then
                productbuildid[$version]=$data
            elif [ $idx -eq 3 ]; then
                productdownloadurl[$version]=$data
            else
                langlatestrelease[$data]=$version
            fi
            let idx++;
        done
    fi
done < $PRODUCTINFO
sortedlanglist=( $(echo ${!langlatestrelease[*]} | tr ' ' '\n' | sort | tr '\n' ' ') )
echo done


# read language code map
echo -n Reading information about languages...
LANGMAP="LanguageCodeMap.txt"
if [ ! -e $LANGMAP ];
then
    echo File $LANGMAP is missing;
    exit;
fi

declare -A langname
declare -A langdownloadurl
while read langcodemapline
do
    # skip empty lines and comments
    if [[ -n $langcodemapline ]] && [[ ${langcodemapline:0:1} != "#" ]]; then
        idx=0;
        declare langcode
        oldIFS=$IFS
        IFS=,
        for data in $langcodemapline; do
            if [ $idx -eq 0 ]; then
                langcode=$data
            elif [ $idx -eq 1 ]; then
                langname[$langcode]=$data
            elif [ $idx -eq 2 ]; then
                langdownloadurl[$langcode]=$data
            fi
            let idx++;
        done
        IFS=$oldIFS
    fi
done < $LANGMAP
echo done

# read additional text for Mac OS X
echo -n Reading translations for string [Only for Mac OS X versions 10.7 or newer]...
ADDTEXTMACOSX="AdditionalTextForMacOSX.txt"
if [ ! -e $ADDTEXTMACOSX ];
then
    echo File $ADDTEXTMACOSX is missing;
    exit;
fi

declare -A addtextformac
while read addtextformacline
do
    # skip empty lines and comments
    if [[ -n $addtextformacline ]] && [[ ${addtextformacline:0:1} != "#" ]]; then
        idx=0;
        declare langcode
        oldIFS=$IFS
        IFS=,
        for data in $addtextformacline; do
            if [ $idx -eq 0 ]; then
                langcode=$data
            elif [ $idx -eq 1 ]; then
                addtextformac[$langcode]=$data
            fi
            let idx++;
        done
        IFS=$oldIFS
    fi
done < $ADDTEXTMACOSX
echo done

# generate Update Feed
echo
echo start generation of Update Feed

FEED="check.Update."$INSTALLEDVERSION"-"$RELEASE

if [ ! -e output ];
then
    mkdir output
fi
echo -n Update Feed $FEED will be found in " "
cd output

declare platforms=(Windows MacOSX Linux Linux)
declare architectures
declare platformnames
if [[ $INSTALLEDVERSION > "4.0.1" ]]; then
    architectures=(x86 X86_64 x86 X86_64)
    platformnames=('Windows' 'Mac OS X 64bit' 'Linux 32bit' 'Linux 64bit')
else
    architectures=(x86 x86 x86 X86_64)
    platformnames=('Windows' 'Mac OS X' 'Linux 32bit' 'Linux 64bit')
fi

FEED="check.Update."$INSTALLEDVERSION"-"$RELEASE
if [ -e $FEED ]; then
    rm $FEED
fi
cat ../feed-prefix.Update | sed "s:%INSTALLED%:$INSTALLEDVERSION:g" | sed "s|%UPDATED%|$(date -u) UTC|g" > $FEED

TEMPLATE="../feed-entry-template.Update"
if [ ! -e $TEMPLATE ]; then
    echo File $TEMPLATE is missing;
    exit;
fi
WORKTEMPLATE="working-template"
WORKING="working"

# loop on the languages
for langidx in ${!sortedlanglist[*]}
do
    echo -n feed entries for language ${sortedlanglist[$langidx]} - ${langname[${sortedlanglist[$langidx]}]}

    if [[ ${langlatestrelease[${sortedlanglist[$langidx]}]} < $INSTALLEDVERSION ]]; then
        echo ..skipped as it its latest release was ${langlatestrelease[${sortedlanglist[$langidx]}]}
        continue
    fi

    cp $TEMPLATE $WORKTEMPLATE
    sed "s/%LANGUAGE%/${langname[${sortedlanglist[$langidx]}]}/g" -i $WORKTEMPLATE
    sed "s/%LANGUAGECODE%/${sortedlanglist[$langidx]}/g" -i $WORKTEMPLATE
    sed "s/%PRODUCTNAME%/${productname[$INSTALLEDVERSION]}/g" -i $WORKTEMPLATE

    echo -n .

    declare newversion=${langlatestrelease[${sortedlanglist[$langidx]}]}
    declare newbuildid=${productbuildid[$newversion]}
    declare downloadurl
    if [[ $newversion == $RELEASE ]] && [[ -n ${langdownloadurl[${sortedlanglist[$langidx]}]} ]]; then
        downloadurl=${langdownloadurl[${sortedlanglist[$langidx]}]}
    else
        downloadurl=${productdownloadurl[${langlatestrelease[${sortedlanglist[$langidx]}]}]}
    fi
    if [[ $INSTALLEDVERSION < "4.0.0" ]]; then
        newversion="- Apache OpenOffice "$newversion
    fi
    sed "s/%NEWVERSION%/$newversion/g" -i $WORKTEMPLATE
    sed "s/%NEWBUILDID%/$newbuildid/g" -i $WORKTEMPLATE
    sed "s|%DOWNLOADURL%|$downloadurl|g" -i $WORKTEMPLATE

    downloadcode=$(echo $INSTALLEDVERSION | tr '.' '_')
    if [[ $INSTALLEDVERSION == "3.4.0" ]] || [[ $INSTALLEDVERSION > "3.4.0" ]]; then
        downloadcode=AOO$downloadcode
    else
        downloadcode=OOo$downloadcode
    fi
    sed "s/%DOWNLOADCODE%/$downloadcode/g" -i $WORKTEMPLATE

    echo -n .

    declare addtext
    for i in {0..3}
    do
        cp $WORKTEMPLATE $WORKING
        sed "s/%PLATFORMNAME%/${platformnames[$i]}/g" -i $WORKING
        sed "s/%PLATFORM%/${platforms[$i]}/g" -i $WORKING
        sed "s/%ARCHITECTURECODE%/${architectures[$i]}/g" -i $WORKING
        addtext=""
        if [[ ${platforms[$i]} == "MacOSX" ]] && [[ $RELEASE > "4.0.1" ]] && [[ ${langlatestrelease[${sortedlanglist[$langidx]}]} == $RELEASE ]]; then
            addtext=${addtextformac[${sortedlanglist[$langidx]}]}
        fi
        sed "s/%ADDITIONALTEXT%/$addtext/g" -i $WORKING
        cat $WORKING >> $FEED
        rm $WORKING
        echo -n .
    done
    echo
    rm $WORKTEMPLATE
    echo >> $FEED
    echo >> $FEED
done
echo "</feed>" >> $FEED
# end of file
