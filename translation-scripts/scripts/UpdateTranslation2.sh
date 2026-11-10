#!/bin/bash
# Update translation
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
#          2018-2021

# Dependencies: translate-toolkit

# Logging error messages
LOG_PATH=/home/mechtilde/log
LOG_FILE=${LOG_PATH}/01_UpdateTranslation.log
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

PROJECT_PATH=${POOTLE_TRANSLATION_DIRECTORY}
TEMPLATES_DIR=/x1/www/po/aoo40/templates

# Directory of the pootle command
POOTLE_DIR=${VIRTUAL_ENV}/bin

# Work directory
WORK_DIR=/home/mechtilde/aoo/workdir

function SDF2POT {
    # Called by CommonTasks
    # If the (non-empty) file en-US.sdf is in ~/aoo/workdir:
    if [ -s ${WORK_DIR}/en-US.sdf ]
    then
        echo "New en-US.sdf file?"
        echo "Is "$(ls -la ${WORK_DIR}/en-US.sdf)" a recent one?" 
        echo "Yes/No (Y/n)"
        read answer
        if [[ ${answer} =~ ^[Nn] ]]
        then
            CommonTasks
        else
            # To be carefull
            rm -r ${WORK_DIR}/POT-UI/
            rm -r ${WORK_DIR}/POT-HELP/helpcontent2

            mkdir -p ${WORK_DIR}/POT-UI
            mkdir -p ${WORK_DIR}/POT-HELP

            # Convert en-US.sdf to templates
            ${POOTLE_DIR}/python ${POOTLE_DIR}/oo2po \
            --progress=verbose \
            --pot ${WORK_DIR}/en-US.sdf ${WORK_DIR}/POT-UI/

            # Move helpcontent2 to POT-HELP to separate HELP from UI
            mv ${WORK_DIR}/POT-UI/helpcontent2 ${WORK_DIR}/POT-HELP/

            # Copy PotFiles-* to project directory
            cp -rv ${WORK_DIR}/POT-UI/* ${PROJECT_PATH}/aoo40/templates/
            cp -rv ${WORK_DIR}/POT-HELP/* ${PROJECT_PATH}/aoo40help/templates/
        fi
    fi

    CommonTasks
}

function POT2DB {
    # Called by CommonTasks
    # Update the translation templates into the Pootle database
    # To be prepared for creating new languages

    ${POOTLE_DIR}/python ${POOTLE_DIR}/pootle \
    --config ${VIRTUAL_ENV}/pootle.conf \
    update_stores --project=aoo40 --language=templates
    
    ${POOTLE_DIR}/python ${POOTLE_DIR}/pootle \
    --config ${VIRTUAL_ENV}/pootle.conf \
    update_stores --project=aoo40help --language=templates

    CommonTasks
}

function CheckLanguageFile {
    # Called by CommonTasks
    # To edit language lists
    
    nano --linenumbers --mouse --softwrap \
    /home/mechtilde/local/bin/00_langs.sh

    CommonTasks
}

function UpdateGit {
    # Called by ReconcileLanguagesUI ReconcileLanguagesHelp
    # Commands for updating git repository

    AddStr=$1
    cd  ${WORK_DIR}
    git add . 
    git commit -am "update $(date) ${Language} - Updated ${AddStr}"

    echo "Finished "${Language} >> ${LOG_FILE}
    echo $(date +%Y-%m-%d-%H%M%S) >> ${LOG_FILE}
}

function ReconcileLanguagesUI {
    # Called by CommonTasks
    # Reconcile languages with templates in UI

    for Language in ${LANGS}
    do
        echo "Sync_stores UI "${Language} >> ${LOG_FILE}
        echo $(date +%Y-%m-%d-%H%M%S) >> ${LOG_FILE}

        # Sync UI stores: 
        # Saves all translations currently in the database 
        # to the file system
        # When the --overwrite option is specified, the sync operation 
        # will not be conservative and it will overwrite the existing 
        # files on disk, making strings obsolete and updating the 
        # file’s structure.

        ${POOTLE_DIR}/python ${POOTLE_DIR}/pootle \
        --config ${VIRTUAL_ENV}/pootle.conf \
        sync_stores --overwrite --project=aoo40 --language=${Language}

        echo "Pomerge UI "${Language} >> ${LOG_FILE}
        echo $(date +%Y-%m-%d-%H%M%S) >> ${LOG_FILE}

        # Update UI po files from templates
        ${POOTLE_DIR}/python ${POOTLE_DIR}/pomerge \
        --template=${PROJECT_PATH}/aoo40/templates/ \
        --input ${WORK_DIR}/aoo40/${Language}/ \
        --output ${PROJECT_PATH}/aoo40/${Language}/

        echo "Update_stores UI "${Language} >> ${LOG_FILE}
        echo $(date +%Y-%m-%d-%H%M%S) >> ${LOG_FILE}

        # Loads translation files currently on the file system 
        # into the database

        ${POOTLE_DIR}/python ${POOTLE_DIR}/pootle \
        --config ${VIRTUAL_ENV}/pootle.conf \
        update_stores --project=aoo40 --language=${Language}

        cp -r ${PROJECT_PATH}/aoo40/${Language} ${WORK_DIR}/aoo40/
        
        UpdateGit "UI"
    done

    CommonTasks
}

function ReconcileLanguagesHelp {
    # Called by CommonTasks
    # Reconcile languages with templates in Help

    for Language in ${LANGS}
    do
        echo "Sync_stores HELP "${Language} >> ${LOG_FILE}
        echo $(date +%Y-%m-%d-%H%M%S) >> ${LOG_FILE}

        # Sync Help stores: 
        # Saves all translations currently in the database 
        # to the file system
        # When the --overwrite option is specified, the sync operation 
        # will not be conservative and it will overwrite the existing 
        # files on disk, making strings obsolete and updating the 
        # file’s structure.

        ${POOTLE_DIR}/python ${POOTLE_DIR}/pootle \
        --config ${VIRTUAL_ENV}/pootle.conf \
        sync_stores --overwrite --project=aoo40help --language=${Language}

        echo "Pomerge HELP "${Language} >> ${LOG_FILE}
        echo $(date +%Y-%m-%d-%H%M%S) >> ${LOG_FILE}

        # Update Help po files from templates
        ${POOTLE_DIR}/python ${POOTLE_DIR}/pomerge \
        --template=${PROJECT_PATH}/aoo40help/templates/ \
       	--input ${WORK_DIR}/aoo40help/${Language}/ \
       	--output ${PROJECT_PATH}/aoo40help/${Language}/

        echo "Update_stores HELP "${Language} >> ${LOG_FILE}
        echo $(date +%Y-%m-%d-%H%M%S) >> ${LOG_FILE}

        # Loads translation files currently on the file system 
        # into the database
        ${POOTLE_DIR}/python ${POOTLE_DIR}/pootle \
        --config ${VIRTUAL_ENV}/pootle.conf \
        update_stores --project=aoo40help --language=${Language}

        cp -r ${PROJECT_PATH}/aoo40help/${Language}/helpcontent2 \
        ${WORK_DIR}/aoo40help/${Language}/

        UpdateGit "Help"
    done

    CommonTasks
}

function CommonTasks {
    # Called by main program

    echo -e "First you need to create a file en_US.sdf in \n\
    a build environment and copy it to this machine. \n\
    What do you like to do?"

    select Task in "Read the notices" \
      "Create *.pot files from en_US.sdf" \
      "Put *.pot files into pootle database" \
      "Check language data" \
      "Reconcile languages with UI templates" \
      "Reconcile languages with Help templates" \
      "Work in progress" \
      "work in progress" \
      "Create new language" \
      "Exit"

    do 2>&1

        case "$Task" in
        # Display notices
        "Read the notices") DisplayNotice;;
         # Create *.pot files from en_US.sdf
        "Create *.pot files from en_US.sdf") SDF2POT;;
        # Put *.pot files into pootle database
        # To be prepared for creating new languages
        "Put *.pot files into pootle database") POT2DB;;
        # Check language data
        "Check language data") CheckLanguageFile;;
        # Reconcile languages with UI templates
        # Include language data
        "Reconcile languages with UI templates") . /home/mechtilde/local/bin/00_langs.sh
            ReconcileLanguagesUI;;
        # Reconcile languages with Help templates
        # Include language data
        "Reconcile languages with Help templates")  . /home/mechtilde/local/bin/00_langs.sh
            ReconcileLanguagesHelp;;
        "Work in progress")  echo "Create new Language?";;
        "work in progress")  echo "Create new Language?";;
        "Create new language")  echo "Create new Language?";;
        "Exit") break;;
        esac
    done    
}
# Mainprogram

CommonTasks

# This is the end, my friend
