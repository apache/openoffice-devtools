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
echo "Generating SHA256/SHA512 checksum files ..."
echo ""

# check for executables
gpg2="`which gpg2 2> /dev/null | head -1`"
gpg="`which gpg 2> /dev/null | head -1`"
pgp="`which pgp 2> /dev/null | head -1`"
sha512sum="`which sha512sum 2> /dev/null | head -1`"
sha256sum="`which sha256sum 2> /dev/null | head -1`"
sha512="`which sha512 2> /dev/null | head -1`"
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
      if ! test -f "${file}.sha512" ; then
        echo "openssl: creating sha512 checksum file for ${file} ..."
        ${openssl} sha512 ${file} |\
            ${sed} -e 's#^SHA512(\(.*/\)*\(.*\))= \([0-9a-f]*\)$#\3 *\2#' > ${file}.sha512
      fi
      if ! test -f "${file}.sha256" ; then
        echo "openssl: creating sha256 checksum file for ${file} ..."
        ${openssl} sha256 ${file} |\
            ${sed} -e 's#^SHA256(\(.*/\)*\(.*\))= \([0-9a-f]*\)$#\3 *\2#' > ${file}.sha256
      fi
    fi
  done
# no openssl found - check if we have gpg2
elif test -x "${gpg2}"; then
  for file in ${allfiles}; do
    if test -f "${file}"; then
      if ! test -f "${file}.sha512" ; then
        echo "gpg2: creating sha512 checksum file for ${file} ..."
        ${gpg2} --print-md sha512 ${file} |\
            ${sed} -e '{N;s#\n##;}' |\
            ${sed} -e 's#\(.*/\)*\(.*\): \(.*\)#\3::\2#;s#[\r\n]##g;s# ##g' \
                -e 'y#ABCDEF#abcdef#;s#::# *#' > ${file}.sha512
      fi
      if ! test -f "${file}.sha256" ; then
        echo "gpg2: creating sha256 checksum file for ${file} ..."
        ${gpg2} --print-md sha256 ${file} |\
            ${sed} -e '{N;s#\n##;}' |\
            ${sed} -e 's#\(.*/\)*\(.*\): \(.*\)#\3::\2#;s#[\r\n]##g;s# ##g' \
                -e 'y#ABCDEF#abcdef#;s#::# *#' > ${file}.sha256
      fi
    fi
  done
# no gpg2 found - check if we have gpg
elif test -x "${gpg}"; then
  for file in ${allfiles}; do
    if test -f "${file}"; then
      if ! test -f "${file}.sha512" ; then
        echo "gpg: creating sha512 checksum file for ${file} ..."
        ${gpg} --print-md sha512 ${file} |\
            ${sed} -e '{N;s#\n##;}' |\
            ${sed} -e 's#\(.*/\)*\(.*\): \(.*\)#\3::\2#;s#[\r\n]##g;s# ##g' \
                -e 'y#ABCDEF#abcdef#;s#::# *#' > ${file}.sha512
      fi
      if ! test -f "${file}.sha256" ; then
        echo "gpg: creating sha256 checksum file for ${file} ..."
        ${gpg} --print-md sha256 ${file} |\
            ${sed} -e '{N;s#\n##;}' |\
            ${sed} -e 's#\(.*/\)*\(.*\): \(.*\)#\3::\2#;s#[\r\n]##g;s# ##g' \
                -e 'y#ABCDEF#abcdef#;s#::# *#' > ${file}.sha256
      fi
    fi
  done
else
  # no openssl or gpg found - check for sha512sum
  if test -x "${sha512sum}"; then
    for file in ${allfiles}; do
      if test -f "${file}"; then
        echo "sha512sum: creating sha512 checksum file for ${file} ..."
        ${sha512sum} -b ${file} > ${file}.sha512
      fi
    done
  # no openssl or gpg found - check for sha512
  elif test -x "${sha512}"; then
    for file in ${allfiles}; do
      if test -f "${file}"; then
        echo "sha512: creating sha512 checksum file for ${file} ..."
        ${sha512} -r ${file} | ${sed} -e 's# # *#' > ${file}.sha512
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
      ${gpg2} --yes --armor ${args} --detach-sign ${file}
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
      ${gpg} --yes --armor ${args} --detach-sign ${file}
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
