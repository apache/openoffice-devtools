#!/bin/sh
#
# hash-sign.sh : hash and sign the following file types
#                in the current dir: .tar.gz, .tar.bz2, .dmg, .zip
#                and .exe
#
user=""
case "$1" in
  -u)
    shift
    user="$1"
    shift
    ;;
esac

if test -z "$@"; then
  allfiles=`find . -type f \( -name '*.tar.gz' -or -name '*.tar.bz2' -or -name '*.dmg' -or -name '*.zip' -or -name '*.exe' \) -print`
else
  allfiles="$@"
fi

echo ""
echo "Generating MD5/SHA256 checksum files ..."
echo ""

# check for executables
gpg2="`which gpg2 2> /dev/null | head -1`"
gpg="`which gpg 2> /dev/null | head -1`"
pgp="`which pgp 2> /dev/null | head -1`"
md5sum="`which md5sum 2> /dev/null | head -1`"
sha256sum="`which sha256sum 2> /dev/null | head -1`"
md5="`which md5 2> /dev/null | head -1`"
sha256="`which sha256 2> /dev/null | head -1`"
openssl="`which openssl 2> /dev/null | head -1`"
sed="`which gsed 2> /dev/null | head -1`"
if ! test -x "${sed}"; then
  sed="`which sed 2> /dev/null | head -1`"
fi;

# if found we use openssl for generating the checksums
# and convert the results into machine-readable format.
if test -x "${openssl}"; then
  for file in ${allfiles}; do
    if test -f "${file}"; then
      echo "openssl: creating md5 checksum file for ${file} ..."
      ${openssl} md5 ${file} |\
          ${sed} -e 's#^MD5(\(.*/\)*\(.*\))= \([0-9a-f]*\)$#\3 *\2#' > ${file}.md5
      echo "openssl: creating sha256 checksum file for ${file} ..."
      ${openssl} sha256 ${file} |\
          ${sed} -e 's#^SHA256(\(.*/\)*\(.*\))= \([0-9a-f]*\)$#\3 *\2#' > ${file}.sha256
    fi
  done
# no openssl found - check if we have gpg2
elif test -x "${gpg2}"; then
  for file in ${allfiles}; do
    if test -f "${file}"; then
      echo "gpg2: creating md5 checksum file for ${file} ..."
      ${gpg2} --print-md md5 ${file} |\
          ${sed} -e '{N;s#\n##;}' |\
          ${sed} -e 's#\(.*/\)*\(.*\): \(.*\)#\3::\2#;s#[\r\n]##g;s# ##g' \
              -e 'y#ABCDEF#abcdef#;s#::# *#' > ${file}.md5
      ${gpg2} --print-md sha256 ${file} |\
          ${sed} -e '{N;s#\n##;}' |\
          ${sed} -e 's#\(.*/\)*\(.*\): \(.*\)#\3::\2#;s#[\r\n]##g;s# ##g' \
              -e 'y#ABCDEF#abcdef#;s#::# *#' > ${file}.sha256
    fi
  done
# no gpg2 found - check if we have gpg
elif test -x "${gpg}"; then
  for file in ${allfiles}; do
    if test -f "${file}"; then
      echo "gpg: creating md5 checksum file for ${file} ..."
      ${gpg} --print-md md5 ${file} |\
          ${sed} -e '{N;s#\n##;}' |\
          ${sed} -e 's#\(.*/\)*\(.*\): \(.*\)#\3::\2#;s#[\r\n]##g;s# ##g' \
              -e 'y#ABCDEF#abcdef#;s#::# *#' > ${file}.md5
      ${gpg} --print-md sha256 ${file} |\
          ${sed} -e '{N;s#\n##;}' |\
          ${sed} -e 's#\(.*/\)*\(.*\): \(.*\)#\3::\2#;s#[\r\n]##g;s# ##g' \
              -e 'y#ABCDEF#abcdef#;s#::# *#' > ${file}.sha256
    fi
  done
else
  # no openssl or gpg found - check for md5sum
  if test -x "${md5sum}"; then
    for file in ${allfiles}; do
      if test -f "${file}"; then
        echo "md5sum: creating md5 checksum file for ${file} ..."
        ${md5sum} -b ${file} > ${file}.md5
      fi
    done
  # no openssl or gpg found - check for md5
  elif test -x "${md5}"; then
    for file in ${allfiles}; do
      if test -f "${file}"; then
        echo "md5: creating md5 checksum file for ${file} ..."
        ${md5} -r ${file} | ${sed} -e 's# # *#' > ${file}.md5
      fi
    done
  fi
  if test -x "${sha256sum}"; then
    for file in ${allfiles}; do
      if test -f "${file}"; then
        echo "sha256sum: creating sha256 checksum file for ${file} ..."
        ${sha256sum} -b ${file} > ${file}.sha256
      fi
    done
  elif test -x "${sha256}"; then
    for file in ${allfiles}; do
      if test -f "${file}"; then
        echo "sha256: creating sha256 checksum file for ${file} ..."
        ${sha256} -r ${file} | ${sed} -e 's# # *#' > ${file}.sha256
      fi
    done
  fi

fi

echo $split
echo ""
echo "Signing the files ..."
echo ""

# We have gpg2
if test -x "${gpg2}"; then
  if test -n "${user}"; then
    args="-u ${user} ${args}"
  fi
  for file in ${allfiles}; do
    if test -f "${file}"; then
      echo "gpg2: creating asc signature file for ${file} ..."
      ${gpg2} --armor ${args} --detach-sign ${file}
    fi
  done
# no gpg2 found - check for gpg
elif test -x "${gpg}"; then
  if test -n "${user}"; then
    args="-u ${user} ${args}"
  fi
  for file in ${allfiles}; do
    if test -f "${file}"; then
      echo "gpg: creating asc signature file for ${file} ..."
      ${gpg} --armor ${args} --detach-sign ${file}
    fi
  done
# ... no GnuPG? Try PGP
elif test -x "${pgp}"; then
  if test -n "${user}"; then
    args="-u ${user}"
  fi
  for file in ${allfiles}; do
    if test -f "${file}"; then
      echo "pgp: creating asc signature file for ${file} ..."
      ${pgp} -sba ${file} ${args}
    fi
  done
else
  echo "PGP or GnuPG not found!  Not signing release!"
fi
