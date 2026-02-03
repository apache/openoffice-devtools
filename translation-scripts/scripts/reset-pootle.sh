#!/bin/bash
ScriptName="reset-pootle" #(.sh)
# Version 0.1.0
# This file should be in ~/local/bin/
#
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


# You can use this script to restore pootle
# e.g after a wrong import of templates
# It does it from home directory into the project path

# Logging error messages
LOG_PATH=${HOME}/log
LOG_FILE=${LOG_PATH}/${ScriptName}"-"$(date +%Y-%m-%d-%H%M%S).log
exec 2>> ${LOG_FILE}
echo "Messages from "$0 >> ${LOG_FILE}


# Newer pootle versions run inside a virtual environment
VIRTUAL_ENV=/x1/www/pootle/env
POOTLE_SETTINGS=${VIRTUAL_ENV}/pootle.conf
PYTHONPATH=${VIRTUAL_ENV}/lib/python2.7/site-packages
PATH="$VIRTUAL_ENV/bin:$PATH"

export VIRTUAL_ENV
export PATH
export POOTLE_SETTINGS
export PYTHONPATH

PROJECT_PATH=/x1/www/po

# Directory of the pootle command and python2
POOTLE_DIR=${VIRTUAL_ENV}/bin

# Work directory
WORK_DIR=${HOME}/aoo/workdir

# Include the Language data
. ${HOME}/local/bin/LanguageLists.sh
echo ${LANGS}
echo ${LANGS} >> ${LOG_FILE}


for Language in ${LANGS}
do
    echo ${Language} >> ${LOG_FILE}
    cp --recursive --verbose ${WORK_DIR}/PO-Files/${Language} \
        ${PROJECT_PATH}/aoo40

    rm --recursive --verbose \
	${PROJECT_PATH}/aoo40help/${Language}/helpcontent2

    mv --verbose ${PROJECT_PATH}/aoo40/${Language}/helpcontent2 \
        ${PROJECT_PATH}/aoo40help/${Language}
done

echo "You should run sync-po-files2db.sh now!"
#generated on 
