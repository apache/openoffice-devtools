#!/bin/bash

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

# This script creates the *.sdf files from the *.po files
# of aoo42 and aoo42help.

# Logging error messages
LOG_PATH=/home/mechtilde/log
LOG_FILE=${LOG_PATH}/mksdf.log
exec 2>> ${LOG_FILE}
echo $(date +%Y-%m-%d-%H%M%S) >> ${LOG_FILE}
echo "Messages from "$0 >> ${LOG_FILE}

# Newer pootle versions run inside a virtual environment
VIRTUAL_ENV=/x1/www/pootle/env
POOTLE_SETTINGS=${VIRTUAL_ENV}/pootle.conf
POOTLE_TRANSLATION_DIRECTORY=/x1/www/po
PYTHONPATH=${VIRTUAL_ENV}/lib/python2.7/site-packages
PATH="$VIRTUAL_ENV/bin:$PATH"

export VIRTUAL_ENV
export PATH
export POOTLE_SETTINGS
export PYTHONPATH


# Directory of the pootle command
POOTLE_DIR=${VIRTUAL_ENV}/bin

PROJECT_PATH=${POOTLE_TRANSLATION_DIRECTORY}

# SDF_DIR=/x1/www/SDF-Files
WORK_DIR=/home/mechtilde/aoo/workdir
SDF_DIR=${WORK_DIR}/SDF-Files

# Include the Language data
. /home/mechtilde/local/bin/langs.sh
# echo ${LANGS}

for Language in ${LANGS[*]}
do

    # SDF needs other language encoding than Pootle
    # Example: 'en-GB' instead of 'en_GB'
    Language2=$(echo ${Language} | sed -e 's/_/-/1')

    mkdir -p ${SDF_DIR}/${Language2}

    cp ${WORK_DIR}/header.txt ${SDF_DIR}/${Language2}/localize.sdf
    
    # copy Help into UI
    cp -a ${WORK_DIR}/aoo40help/${Language}/helpcontent2 \
    ${WORK_DIR}/aoo40/${Language} &&

    ${POOTLE_DIR}/python ${POOTLE_DIR}/po2oo \
    --progress=verbose \
    --template=${WORK_DIR}/en-US.sdf \
    --skipsource \
    --keeptimestamp \
    --language=${Language} ${WORK_DIR}/aoo40/${Language}/ ${SDF_DIR}/${Language2}/localize.${Language2} &&

    cat ${SDF_DIR}/${Language2}/localize.${Language2} >> ${SDF_DIR}/${Language2}/localize.sdf &&

    rm ${SDF_DIR}/${Language2}/localize.${Language2}
    chmod g+w ${SDF_DIR}/${Language2}/localize.sdf
    
    rm -r ${WORK_DIR}/aoo40/${Language}/helpcontent2
done
