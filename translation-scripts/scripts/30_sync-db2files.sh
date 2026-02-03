#!/bin/bash
ScriptName="sync-db2files" #(.sh)
# Version 0.1.0
# This file should be in ~/local/bin/.
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

# This script syncronizes the pootle database to the file system
# for aoo42 and/or aoo42help.

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


for Language in ${LANGS[*]}
do

    echo ${Language} >> ${LOG_FILE}

# When the --overwrite option is specified, the sync operation will not be
# conservative and it will overwrite the existing files on disk, making
# strings obsolete and updating the file’s structure.

    echo "UI" >> ${LOG_FILE}
    ${VIRTUAL_ENV}/bin/python ${VIRTUAL_ENV}/bin/pootle \
    --config ${VIRTUAL_ENV}/pootle.conf \
    sync_stores --overwrite --project=aoo40 --language=${Language}

    echo "HELP" >> ${LOG_FILE}
    ${VIRTUAL_ENV}/bin/python ${VIRTUAL_ENV}/bin/pootle \
    --config ${VIRTUAL_ENV}/pootle.conf \
    sync_stores --overwrite --project=aoo40help --language=${Language}

    echo "Done "${Language} >> ${LOG_FILE}
done
echo "Done all"${date} >> ${LOG_FILE}
#generated on 
