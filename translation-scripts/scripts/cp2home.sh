#!/bin/bash
ScriptName="cp2home" #(.sh)
# License:   Apache-2.0
#  Licensed to the Apache Software Foundation (ASF) under one
#  or more contributor license agreements.
#  The ASF licenses this file  to you under the Apache License,
#  Version 2.0 (the "License"); you may not use this file except
#  in compliance with the License. You may obtain a copy of the
#  License at
#
#  http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing,
#  software distributed under the License is distributed on an
#  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
#  KIND, either express or implied.  See the License for the
#  specific language governing permissions and limitations
#  under the License.

# Authors: Michael Stehmann   <mikeadvo@apache.org>
#          Mechtilde Stehmann <mechtilde@apache.org>

# This script copies the *.po files
# of aoo40 and aoo40help into the home directory

# Work directory
WORK_DIR=${HOME}/aoo/workdir

PROJECT_PATH=/x1/www/po

# Logging error messages
LOG_PATH=${HOME}/log
LOG_FILE=${LOG_PATH}/${ScriptName}"-"$(date +%Y-%m-%d-%H%M%S).log
exec 2>> ${LOG_FILE}
echo "Messages from "$0 >> ${LOG_FILE}

# Include the Language data
. ${HOME}/local/bin/LanguageLists.sh
echo ${LANGS}
echo ${LANGS} >> ${LOG_FILE}


for Language in ${LANGS[*]}
do

    # Language2=$(echo ${Language} | sed -e 's/_/-/1')
    # mkdir -p ${WORK_DIR}/SDF-Files/${Language2}/

    mkdir -p ${WORK_DIR}/PO-Files/${Language}/
    cd ${WORK_DIR}/PO-Files/

    cp -r ${PROJECT_PATH}/aoo40/${Language} .
    cp --recursive ${PROJECT_PATH}/aoo40/${Language} ../aoo40

    cp --recursive ${PROJECT_PATH}/aoo40help/${Language}/helpcontent2 \
        ./${Language}
    cp --recursive ${PROJECT_PATH}/aoo40help/${Language} \
        ../aoo40help/

done

# Commands for updating git repository
cd  ${WORK_DIR}
git add . 
git commit -am "update $(date) PO-Files"

#generated on 
