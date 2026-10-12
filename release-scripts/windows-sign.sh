#!/bin/sh
#
# windows-sign.sh : sign all EXE files in the current directory and its
#                   subdirectories using jsign
#
# jsign must be available as a command; install the .deb or .rpm file of the
# latest version, obtained from the GitHub repository:
# https://github.com/ebourg/jsign/
#
# You must have your SSL.com account and you must have obtained your own
# signing credentials. This script requires:
#  - your SSL.com username;
#  - your SSL.com password;
#  - your secret code to the ASF code signing certificate.
#
# After running this script, the EXE files are changed and must be re-hashed
# and re-signed with the developer's PGP key.

have_jsign=1
which jsign > /dev/null || have_jsign=0
if [ $have_jsign = 0 ]; then
    echo "ERROR: jsign was not found. Download it from https://github.com/ebourg/jsign/" 1>&2
    exit 1
fi

printf "Enter SSL.com username: "
read username
printf "Enter SSL.com password: "
read password
printf "Enter SSL.com secret code: "
read code

allfiles=`find . -type f -name '*.exe' -print`
count=0
for file in ${allfiles}; do
    jsign --storetype ESIGNER --alias d97c5110-c66a-4c0c-ac0c-1cd6af812ee6 \
          --storepass "${username}|${password}" --keypass "${code}" \
          --tsaurl="http://ts.ssl.com" --tsmode RFC3161 --alg SHA256 \
          $file || exit 1
    count=$(( $count + 1 ))
done

echo "$count files signed. Now it's time to run hash-sign.sh -f"
