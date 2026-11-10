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

# Logging error messages
LOG_PATH=/home/mechtilde/log
LOG_FILE=${LOG_PATH}/UpdateTranslation.log
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

# Include the Language data
. /home/mechtilde/local/bin/langs.sh
echo ${LANGS}

# If the (non-empty) file en-US.sdf is in ~/aoo/workdir:
if [ -s ${WORK_DIR}/en-US.sdf ]
then
	# To be carefull
#	rm -r ${WORK_DIR}/POT-UI/
#	rm -r ${WORK_DIR}/POT-HELP/helpcontent2

#	mkdir -p ${WORK_DIR}/POT-UI
#	mkdir -p ${WORK_DIR}/POT-HELP

	# Convert en-US.sdf to templates
#       ${POOTLE_DIR}/python ${POOTLE_DIR}/oo2po --progress=verbose --pot ${WORK_DIR}/en-US.sdf ${WORK_DIR}/POT-UI/

	# Move helpcontent2 to POT-HELP to separate HELP from UI
#	mv ${WORK_DIR}/POT-UI/helpcontent2 ${WORK_DIR}/POT-HELP/

	# Copy PotFiles-* to project directory
#	cp -rv ${WORK_DIR}/POT-UI/* ${PROJECT_PATH}/aoo40/templates/
#	cp -rv ${WORK_DIR}/POT-HELP/* ${PROJECT_PATH}/aoo40help/templates/

	# Update the translation templates into the Pootle database
#        ${POOTLE_DIR}/python ${POOTLE_DIR}/pootle \
#        --config ${VIRTUAL_ENV}/pootle.conf \
#	update_stores --project=aoo40 --language=templates
#        ${POOTLE_DIR}/python ${POOTLE_DIR}/pootle \
#        --config ${VIRTUAL_ENV}/pootle.conf \
#	update_stores --project=aoo40help --language=templates

	# Reconcile languages with templates
	for Language in ${LANGS}
	do
#		echo "Sync_stores UI "${Language} >> ${LOG_FILE}
#		echo $(date +%Y-%m-%d-%H%M%S) >> ${LOG_FILE}

#        	${POOTLE_DIR}/python ${POTLE_DIR}/pootle \
#	        --config ${VIRTUAL_ENV}/pootle.conf \
#		sync_stores --project=aoo40 --language=${Language}

#                echo "Pomerge UI "${Language} >> ${LOG_FILE}
#                echo $(date +%Y-%m-%d-%H%M%S) >> ${LOG_FILE}

#		${POOTLE_DIR}/python ${POOTLE_DIR}/pomerge \
#		--template=${PROJECT_PATH}/aoo40/templates/ \
#		--input ${WORK_DIR}/aoo40/${Language}/ \
#		--output ${PROJECT_PATH}/aoo40/${Language}/

#                echo "Update_stores UI "${Language} >> ${LOG_FILE}
#                echo $(date +%Y-%m-%d-%H%M%S) >> ${LOG_FILE}

#		${POOTLE_DIR}/python ${POOTLE_DIR}/pootle \
#	        --config ${VIRTUAL_ENV}/pootle.conf \
#		update_stores --project=aoo40 --language=${Language}

#		cp -r ${PROJECT_PATH}/aoo40/${Language} ${WORK_DIR}/aoo40/

                echo "Sync_stores HELP "${Language} >> ${LOG_FILE}
                echo $(date +%Y-%m-%d-%H%M%S) >> ${LOG_FILE}

		${POOTLE_DIR}/python ${POOTLE_DIR}/pootle \
	        --config ${VIRTUAL_ENV}/pootle.conf \
        	sync_stores --project=aoo40help --language=${Language}

                echo "Pomerge HELP "${Language} >> ${LOG_FILE}
                echo $(date +%Y-%m-%d-%H%M%S) >> ${LOG_FILE}

		${POOTLE_DIR}/python ${POOTLE_DIR}/pomerge \
	        --template=${PROJECT_PATH}/aoo40help/templates/ \
        	--input ${WORK_DIR}/aoo40help/${Language}/ \
        	--output ${PROJECT_PATH}/aoo40help/${Language}/

                echo "Update_stores HELP "${Language} >> ${LOG_FILE}
                echo $(date +%Y-%m-%d-%H%M%S) >> ${LOG_FILE}

	        ${POOTLE_DIR}/python ${POOTLE_DIR}/pootle \
	        --config ${VIRTUAL_ENV}/pootle.conf \
        	update_stores --project=aoo40help --language=${Language}

		cp -r ${PROJECT_PATH}/aoo40help/${Language}/helpcontent2 \
		${WORK_DIR}/aoo40help/${Language}/

		# Commands for updating git repository
		cd  ${WORK_DIR}
		git add . 
		git commit -am "update $(date) ${Language} - Corrected Helpdir"

                echo "Finished "${Language} >> ${LOG_FILE}
                echo $(date +%Y-%m-%d-%H%M%S) >> ${LOG_FILE}

	done
fi
