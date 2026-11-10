#!/bin/bash
ScriptName="pomerge" #(.sh)
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

# Logging error messages
LOG_PATH=${HOME}/log
LOG_FILE=${LOG_PATH}/${ScriptName}"-"$(date +%Y-%m-%d-%H%M%S).log
exec 2>> ${LOG_FILE}
echo "Messages from "$0 >> ${LOG_FILE}


# This script merges the recent po files with the new template 
# and creates new po files.
# This file should be in ~/local/bin/.

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


# Reconcile languages with templates
for Language in ${LANGS}
do

    echo "Pomerge UI "${Language} >> ${LOG_FILE}
    echo $(date +%Y-%m-%d-%H%M%S) >> ${LOG_FILE}

    ${POOTLE_DIR}/python ${POOTLE_DIR}/pomerge \
    --template=${PROJECT_PATH}/aoo40/templates/ \
    --input ${WORK_DIR}/aoo40/${Language}/ \
    --output ${PROJECT_PATH}/aoo40/${Language}/

    echo "Pomerge HELP "${Language} >> ${LOG_FILE}
    echo $(date +%Y-%m-%d-%H%M%S) >> ${LOG_FILE}

    ${POOTLE_DIR}/python ${POOTLE_DIR}/pomerge \
    --template=${PROJECT_PATH}/aoo40help/templates/ \
    --input ${WORK_DIR}/aoo40help/${Language}/ \
    --output ${PROJECT_PATH}/aoo40help/${Language}/

done
#generated on 
