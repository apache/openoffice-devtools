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

# You can use this script to create all language
# directories in /x1/www/po/ on a new server.
# (if not exist)

# Import languages list
. /home/mechtilde/local/bin/langs.sh

cd /x1/www/po/aoo42

mkdir ${LANGS_ALL}

ls -la
echo "Done in /x1/www/po/aoo42"

cd /x1/www/po/aoo42help

mkdir -pv $(find /home/mechtilde/aoo/workdir/PO-Files/ -name '*' | sed -e 's/\/home\/mechtilde\/aoo\/workdir\/PO-Files\///' | grep helpcontent2 | sed -e 's/\/[a-z 0-9]*.po$//')

ls -la

echo "Done in /x1/www/po/aoo42help"

echo -e 'Please become root and adapt the rights\nusing "chown -R www-data ."'

