#!/bin/bash
ScriptName="sdf2pot" #(.sh) 
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

TEMPLATES_DIR=/x1/www/po/aoo40/templates

# If the (non-empty) file en-US.sdf is in ${HOME}:
if [ -s ${HOME}/en-US.sdf ]
then
    mv ${HOME}/en-US.sdf ${WORK_DIR}/en-US.sdf
    # To be carefull
    rm -r ${WORK_DIR}/POT-UI/
    rm -r ${WORK_DIR}/POT-HELP/helpcontent2

    mkdir -p ${WORK_DIR}/POT-UI
    mkdir -p ${WORK_DIR}/POT-HELP

    # Convert en-US.sdf to templates
    ${POOTLE_DIR}/python ${POOTLE_DIR}/oo2po --progress=verbose \
    --pot ${WORK_DIR}/en-US.sdf ${WORK_DIR}/POT-UI/

    # Move helpcontent2 to POT-HELP to separate HELP from UI
    mv ${WORK_DIR}/POT-UI/helpcontent2 ${WORK_DIR}/POT-HELP/
fi
#generated on 
