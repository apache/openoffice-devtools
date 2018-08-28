#!/bin/bash
#-------------------------------------------------------------------
#
#              UPDATE VERSION FOR OO
#
#   usage: updateVersion.sh <version> <oo_path>
#
#   params:
#           version - the version to update the files to
#           oo_path - the path to the OO files 
#
#-------------------------------------------------------------------
version=$1
oo_path=$2

logfile=/tmp/updateVersion_$(date +%Y-%m-%d).log
configfile=$(dirname "$0")/updateVersion.config

exec > >(tee -ia ${logfile}) 2>&1
echo "***************************************************"
echo "STARTED UPDATING VERSIONS $(date)"
echo "***************************************************"

# validate params
if [[ -z ${version} && -z ${oo_path} ]]
then
    echo "Missing both version and OO path. Exiting..."
    echo "***************************************************"
    echo "FINISHED UPDATING VERSIONS $(date)"
    echo "***************************************************"
    exit
fi
if [[ -z ${version} ]]
then
    echo "Missing version. Exiting..."
    echo "***************************************************"
    echo "FINISHED UPDATING VERSIONS $(date)"
    echo "***************************************************"
    exit
else
    if [[ ${version} =~ [^[:digit:].] ]]
    then
        echo "Invalid version format. It should be in the form x or x.x or x.x.x, where x is either a digit or a number. Exiting..."
        echo "***************************************************"
        echo "FINISHED UPDATING VERSIONS $(date)"
        echo "***************************************************"
        exit
    fi
fi

# validate paths
if [[ -z ${oo_path} ]]
then
    echo "Missing OO path, assuming current working directory. I will look here for files to modify..."
    oo_path=.
fi
if [[ ! -d "${oo_path}/main" ]]
then
    echo "${oo_path} does not contain a main subfolder. Exiting..."
    echo "***************************************************"
    echo "FINISHED UPDATING VERSIONS $(date)"
    echo "***************************************************"
    exit
fi

# validate config file
if [[ ! -r ${configfile} ]]
then
    echo "${configfile} either does not exist or is not readable. Exiting..."
    echo "***************************************************"
    echo "FINISHED UPDATING VERSIONS $(date)"
    echo "***************************************************"
    exit
fi

files=()
patterns=()
while read line
do
    if [[ ${line} == ---* ]]
    then
        filename=`echo ${line} | cut -d':' -f 2`
        files+=("${filename:1}")
        if [[ ! -z ${pattern} ]]
        then
            patterns+=("${pattern}")
            pattern=""
        fi
    else
        pattern=${pattern}" "${line}
    fi
done < ${configfile}
patterns+=("${pattern}")

# echo "***************************************************"
# echo "found ${#files[@]} files and ${#patterns[@]} pattern groups"
# for (( i=0; i<${#files[@]}; i++ ))
# do
#     echo "file: ${files[$i]}"
#     echo "patterns: ${patterns[$i]}"
# done

echo "***************************************************"
for (( i=0; i<${#files[@]}; i++ ))
do
    echo "*********************"
    if [[ ${files[$i]} == */* ]]
    then # files given as path, like odk/utils/makefile.pmk
        filepath=`find ${oo_path} -path "*${files[$i]}"`
    else # just the filename
        filepath=`find ${oo_path} -type f -name ${files[$i]}`
    fi    
    if [[ -z ${filepath} ]]
    then
        echo "File not found. Skipping..."
    else
        declare -i nbrFilesFound
        nbrFilesFound=`echo ${filepath} | grep -o "${files[$i]}" | wc -l`
        readarray -t filepaths <<< "${filepath}"
        for (( f=0; f<${nbrFilesFound}; f++ ))
        do
            filepath=${filepaths[$f]}
            echo "**********"
            echo "updating file: ${filepath}"
            for j in ${patterns[$i]}
            do
                # TYPE I substitution: 
                # -> the file is xml, we have name=<pattern> and value=<old_version>
                # -> old_version will be updated
                # -> ex. srcrelease.xml
                if [[ ${files[$i]} == *.xml ]]
                then
                    readarray -t lines < <(grep -w ${j} ${filepath} | grep -v -e '{' -e '(')
                    for (( s=0; s<${#lines[@]}; s++ ))
                    do
                        echo "line: ${lines[$s]}"
                        newline=`echo ${lines[$s]} | sed -E "s|(value=\")[0-9]+.?[0-9]*.?[0-9]*(\")|\1${version}\2|"`
                        echo "newline: ${newline}"
                        sed -E -i "s|(value=\")[0-9]+.?[0-9]*.?[0-9]*(\")|\1${version}\2|" ${filepath}
                        echo "**********"
                    done
                #
                # TYPE II substitution:
                # -> the pattern is in the form <pattern>=<new_value>
                # -> replace the old value of <pattern> with the <new_value>
                # -> ex. minor.mk
                elif [[ $j == *=* ]]
                then
                    patt2brepl=`echo $j | cut -d'=' -f 1`
                    patt2replWith=`echo $j | cut -d'=' -f 2`
                    echo "pattern to be replaced: ${patt2brepl}"
                    echo "pattern to replace with: ${patt2replWith}"
                    if [[ ${patt2replWith} == *\(* && ${patt2replWith} == *\)* ]]
                    then
                        replStr=`grep -w ${patt2brepl} ${filepath} | tr '(' '+'`
                        replStr=`echo ${replStr} | tr ')' '+'`
                        sed -E -i "s/(.*)${patt2brepl}.*/\1${replStr}/" ${filepath}
                        echo "temporarily changing file contents replStr ${replStr}"
                    fi
                    readarray -t lines < <(grep -w ${patt2brepl} ${filepath} | grep -v -e '{' -e '(')
                    for (( s=0; s<${#lines[@]}; s++ ))
                    do
                        echo "line: ${lines[$s]}"
                        newline=`echo ${lines[$s]} | sed -E "s|(${patt2brepl} ?=? ?)[a-zA-Z0-9]+.?[a-zA-Z0-9]*.?[a-zA-Z0-9]*[+]*|\1${patt2replWith}|"`
                        echo "newline: ${newline}"
                        sed -E -i "s|(${patt2brepl} ?=? ?)[a-zA-Z0-9]+.?[a-zA-Z0-9]*.?[a-zA-Z0-9]*[+]*|\1${patt2replWith}|" ${filepath}
                        echo "**********"
                    done
                else
                #
                # TYPE III substitution:
                # -> replace right-side pattern with new value
                # -> ex. openoffice.lst
                    readarray -t lines < <(grep -w ${j} ${filepath} | grep -v -e '{' -e '(')
                    for (( s=0; s<${#lines[@]}; s++ ))
                    do
                        echo "line: ${lines[$s]}"
                        line=`echo ${lines[$s]} | awk '{$1=$1};1'`
                        patt2brepl=`echo ${line} | cut -d'=' -f 2`
                        if [[ ${patt2brepl} == ${line} ]]
                        then
                            patt2brepl=`echo ${line} | cut -d' ' -f 2`
                        fi
                        
                        # search inside words for patterns like aoo412 or AOO4.1.2
                        insStrPatt=`echo ${patt2brepl} | egrep -w "[aA][oO][oO][0-9]+.?[0-9]*.?[0-9]*"`
                        if [[ ${patt2brepl} == ${insStrPatt} ]]
                        then
                            partAOO=`echo ${patt2brepl} | sed -E -n "s|.*([aA][oO][oO])[0-9]+.?[0-9]*.?[0-9]*[^a-zA-Z/].*|\1|p"`
                            partVer=`echo ${patt2brepl} | sed -E -n "s|.*([aA][oO][oO])([0-9]+.?[0-9]*.?[0-9]*[^a-zA-Z/]).*|\2|p"`
                            patt2brepl=${partVer}
                        fi
                        echo "patt2brepl: [${patt2brepl}]"
                        
                        v0=`echo ${version} | cut -d'.' -f 1`
                        if [[ ${v0} != ${version} ]]
                        then
                            v1=`echo ${version} | cut -d'.' -f 2`
                            v2=`echo ${version} | cut -d'.' -f 3`
                        fi
                        
                        p0=`echo ${patt2brepl} | cut -d'.' -f 1`
                        p1=`echo ${patt2brepl} | cut -d'.' -f 2`
                        p2=`echo ${patt2brepl} | cut -d'.' -f 3`
                        
                        if [[ ${p0} == ${patt2brepl} ]]  # right side is in the form x or xx (one or more numbers only)
                        then
                            declare -i nbr
                            nbr=${#patt2brepl}
                            if [[ ${nbr} -eq 1 ]]   # right side is just x
                            then
                                newline=`echo ${line} | sed -E "s|(${partAOO})[0-9]+.?[0-9]*.?[0-9]*[^a-zA-Z/]|\1${v0}|"`
                            else
                                if [[ ${nbr} -eq 2 ]]   # right side is xx
                                then
                                    if [[ ${p0}+1 -eq ${v0} ]]  # right side + 1 is version
                                    then
                                        newline=`echo ${line} | sed -E "s|(${partAOO})[0-9]+.?[0-9]*.?[0-9]*[^a-zA-Z/]|\1${v0}|"`
                                    else
                                        newline=`echo ${line} | sed -E "s|(${partAOO})[0-9]+.?[0-9]*.?[0-9]*[^a-zA-Z/]|\1${v0}${v1}|"`
                                    fi
                                else
                                    newline=`echo ${line} | sed -E "s|(${partAOO})[0-9]+.?[0-9]*.?[0-9]*[^a-zA-Z/]|\1${v0}${v1}${v2}|"`
                                fi
                            fi
                        elif [[ -z ${p2} ]]  # right side is in the form x.x
                            then
                                if [[ -n ${v1} ]]
                                then 
                                    newline=`echo ${line} | sed -E "s|(${partAOO})[0-9]+.?[0-9]*.?[0-9]*[^a-zA-Z/]|\1${v0}.${v1}|"`
                                else
                                    newline=`echo ${line} | sed -E "s|(${partAOO})[0-9]+.?[0-9]*.?[0-9]*[^a-zA-Z/]|\1${version}|"`
                                fi
                            else
                                newline=`echo ${line} | sed -E "s|(${partAOO})[0-9]+.?[0-9]*.?[0-9]*[^a-zA-Z/]|\1${version}|"`
                        fi
                        
                        echo "newline: ${newline}"
                        echo "**********"
                        sed -E -i "s|${line}|${newline}|" ${filepath}
                    done
                fi
            done
      done
    fi
done
echo "***************************************************"
echo "FINISHED UPDATING VERSIONS $(date)"
echo "***************************************************"
