#!/usr/bin/env bash
#-------------------------------------------------------------------
#
#              UPDATE VERSION FOR AOO
#
#   usage: updateVersion.sh [options] <version> [oo_path]
#
#   Sets the product version throughout an Apache OpenOffice source
#   tree.  Every value is derived from <version> and from what the
#   tree already contains, so the same invocation is correct on any
#   release branch (AOO41X, AOO50X, trunk, ...).
#
#-------------------------------------------------------------------
set -u

progname=$(basename "$0")
configfile=$(dirname "$0")/updateVersion.config
logfile=/tmp/updateVersion_$(date +%Y-%m-%d).log

usage()
{
    cat <<EOF
usage: ${progname} [options] <version> [oo_path]

  <version>   x.y.z (or x.y, treated as x.y.0)
  oo_path     source tree to update (default: current directory)

options:
  -b, --build <n|keep>   build number; default: current BUILD + 1
  -m, --milestone <n>    milestone; default: 1
  -p, --previous <ver>   value for PREVIOUS_VERSION; default: the version
                         being replaced, when only the micro level changes
  -d, --date <y-m-d>     release date for version.lst; default: leave as is
  -c, --config <file>    version map (default: ${configfile})
  -n, --dry-run          report the changes without writing anything
  -h, --help             this text
EOF
}

die()
{
    echo "${progname}: $*" >&2
    exit 1
}

# --- options ------------------------------------------------------
build_opt=""
milestone=1
previous_opt=""
date_opt=""
dryrun=0
args=()

while [ $# -gt 0 ]
do
    case "$1" in
        -b|--build)     [ $# -ge 2 ] || die "$1 needs a value"; build_opt=$2; shift 2 ;;
        -m|--milestone) [ $# -ge 2 ] || die "$1 needs a value"; milestone=$2; shift 2 ;;
        -p|--previous)  [ $# -ge 2 ] || die "$1 needs a value"; previous_opt=$2; shift 2 ;;
        -d|--date)      [ $# -ge 2 ] || die "$1 needs a value"; date_opt=$2; shift 2 ;;
        -c|--config)    [ $# -ge 2 ] || die "$1 needs a value"; configfile=$2; shift 2 ;;
        -n|--dry-run)   dryrun=1; shift ;;
        -h|--help)      usage; exit 0 ;;
        --)             shift; while [ $# -gt 0 ]; do args+=("$1"); shift; done ;;
        -*)             usage >&2; die "unknown option $1" ;;
        *)              args+=("$1"); shift ;;
    esac
done

[ ${#args[@]} -ge 1 ] || { usage >&2; exit 1; }
[ ${#args[@]} -le 2 ] || { usage >&2; die "too many arguments"; }

version=${args[0]}
oo_path=${args[1]:-.}

case "${version}" in
    *.*.*) ;;
    *.*)   version=${version}.0 ;;
esac
echo "${version}" | grep -qE '^[0-9]+\.[0-9]+\.[0-9]+$' \
    || die "invalid version '${args[0]}': expected x.y.z or x.y"

new_x=$(echo "${version}" | cut -d. -f1)
new_y=$(echo "${version}" | cut -d. -f2)
new_z=$(echo "${version}" | cut -d. -f3)

echo "${milestone}" | grep -qE '^[0-9]+$' || die "invalid milestone '${milestone}'"

new_day=""; new_month=""; new_year=""
if [ -n "${date_opt}" ]
then
    echo "${date_opt}" | grep -qE '^[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}$' \
        || die "invalid date '${date_opt}': expected yyyy-mm-dd"
    new_year=$(echo "${date_opt}" | cut -d- -f1)
    new_month=$(echo "${date_opt}" | cut -d- -f2 | sed 's/^0*\([0-9]\)/\1/')
    new_day=$(echo "${date_opt}" | cut -d- -f3 | sed 's/^0*\([0-9]\)/\1/')
fi

[ -d "${oo_path}/main" ] || die "${oo_path} does not contain a main subfolder"
[ -r "${configfile}" ]   || die "${configfile} does not exist or is not readable"

# --- the work, piped so the log is flushed before we exit ------------
run()
{
    echo "***************************************************"
    echo "STARTED UPDATING VERSIONS $(date)"
    echo "***************************************************"

    # --- what the tree holds today ------------------------------------
    minor_mk=${oo_path}/main/solenv/inc/minor.mk
    oo_lst=${oo_path}/main/instsetoo_native/util/openoffice.lst

    [ -r "${minor_mk}" ] || die "${minor_mk} not found; is ${oo_path} an AOO source tree?"
    [ -r "${oo_lst}" ]   || die "${oo_lst} not found; is ${oo_path} an AOO source tree?"

    old_compact=$(sed -nE 's/^RSCVERSION[[:blank:]]*=[[:blank:]]*([0-9]+).*/\1/p' "${minor_mk}" | head -1)
    old_build=$(sed -nE 's/^BUILD[[:blank:]]*=[[:blank:]]*([0-9]+).*/\1/p' "${minor_mk}" | head -1)
    old_full=$(sed -nE 's/^[[:blank:]]*PRODUCTVERSION[[:blank:]]+([0-9][0-9.]*).*/\1/p' "${oo_lst}" | head -1)

    [ -n "${old_compact}" ] || die "no RSCVERSION found in ${minor_mk}"
    [ -n "${old_build}" ]   || die "no BUILD found in ${minor_mk}"
    [ -n "${old_full}" ]    || die "no PRODUCTVERSION found in ${oo_lst}"

    old_x=$(echo "${old_full}" | cut -d. -f1)
    old_y=$(echo "${old_full}" | cut -d. -f2)
    old_z=$(echo "${old_full}" | cut -d. -f3)
    [ -n "${old_z}" ] || old_z=0
    old_base=${old_x}.${old_y}

    if [ "${old_x}${old_y}${old_z}" != "${old_compact}" ]
    then
        echo "WARNING: RSCVERSION (${old_compact}) does not match PRODUCTVERSION (${old_full})"
    fi

    # --- build number -------------------------------------------------
    case "${build_opt}" in
        "")     new_build=$((old_build + 1)) ;;
        keep)   new_build=${old_build} ;;
        *[!0-9]*) die "invalid build '${build_opt}'" ;;
        *)      new_build=${build_opt} ;;
    esac

    # --- PREVIOUS_VERSION ---------------------------------------------
    # Only meaningful for a bump inside one release line; across lines
    # (4.1.x -> 5.0.0) the previous release is not what the tree holds.
    case "${previous_opt}" in
        none) previous="" ;;
        "")   if [ "${old_base}" = "${new_x}.${new_y}" ] && [ "${old_full}" != "${version}" ]
              then
                  previous=${old_full}
              else
                  previous=""
              fi ;;
        *)    previous=${previous_opt} ;;
    esac

    echo "source tree : ${oo_path}"
    echo "old version : ${old_full} (RSCVERSION ${old_compact}, BUILD ${old_build})"
    echo "new version : ${version} (RSCVERSION ${new_x}${new_y}${new_z}, BUILD ${new_build}, milestone m${milestone})"
    if [ -n "${previous}" ]
    then
        echo "PREVIOUS_VERSION: ${previous}"
    else
        echo "PREVIOUS_VERSION: left unchanged"
    fi
    if [ -n "${date_opt}" ]
    then
        echo "release date: ${new_year}-${new_month}-${new_day}"
    else
        echo "release date: left unchanged"
    fi
    [ ${dryrun} -eq 1 ] && echo "DRY RUN - no files will be written"
    echo "***************************************************"

    # --- the editor ---------------------------------------------------
    read -r -d '' editprog <<'PERL'
BEGIN {
    $key   = quotemeta $ENV{UV_KEY};
    $rule  = $ENV{UV_RULE};
    $syn   = $ENV{UV_SYNTAX};
    ($X, $Y, $Z) = @ENV{qw(UV_X UV_Y UV_Z)};
    $build = $ENV{UV_BUILD};
    $ms    = $ENV{UV_MILESTONE};
    $prev  = $ENV{UV_PREVIOUS};
    ($ofull, $ocomp, $obase) = @ENV{qw(UV_OLD_FULL UV_OLD_COMPACT UV_OLD_BASE)};
    ($day, $month, $year)    = @ENV{qw(UV_DAY UV_MONTH UV_YEAR)};
    open($CH, '>>', $ENV{UV_CHANGES}) or die "cannot log changes: $!";
}

sub value {
    my ($old) = @_;

    # never touch a value that is a make/ant reference
    return $old if $old =~ /[\$\{]/;

    return "$X.$Y.$Z"                       if $rule eq 'full';
    return "$X.$Y"                          if $rule eq 'base';
    return $X                               if $rule eq 'major';
    return $Y                               if $rule eq 'minor';
    return $Z                               if $rule eq 'micro';
    return "$X$Y$Z"                         if $rule eq 'compact';
    return "AOO$X$Y$Z"                      if $rule eq 'srcversion';
    return "$X$Y${Z}m$ms(Build:$build)"     if $rule eq 'rscrevision';
    return $build                           if $rule eq 'build';
    return "m$ms"                           if $rule eq 'lastminor';
    return "$X.$Y.$build.500"               if $rule eq 'fileversion';
    return ($prev ne '' ? $prev : $old)     if $rule eq 'previous';
    return ($day   ne '' ? $day   : $old)   if $rule eq 'day';
    return ($month ne '' ? $month : $old)   if $rule eq 'month';
    return ($year  ne '' ? $year  : $old)   if $rule eq 'year';

    if ($rule eq 'keep') {
        return $old unless $old =~ /^(\d+)(?:\.(\d+))?(?:\.(\d+))?$/;
        return defined $3 ? "$X.$Y.$Z" : defined $2 ? "$X.$Y" : $X;
    }

    if ($rule eq 'embed') {
        my $new = $old;
        $new =~ s/(?<!\d)\Q$ofull\E(?!\d)/$X.$Y.$Z/g;
        $new =~ s/(?<!\d)\Q$ocomp\E(?!\d)/$X$Y$Z/g;
        $new =~ s/(?<!\d)\Q$obase\E(?!\.?\d)/$X.$Y/g;
        return $new;
    }

    die "unknown rule '$rule'\n";
}

sub replace {
    my ($old) = @_;
    my $new = value($old);
    print $CH "$ENV{UV_FILE}:$.: $ENV{UV_KEY}: $old -> $new\n" if $new ne $old;
    return $new;
}

if    ($syn eq 'mk')  { s{^([ \t]*$key[ \t]*=[ \t]*)(.*?)([ \t]*)$}{ $1 . replace($2) . $3 }e }
elsif ($syn eq 'lst') { s{^([ \t]*$key)([ \t]+)(.*?)([ \t]*)$}{ $1 . $2 . replace($3) . $4 }e }
elsif ($syn eq 'xml') { s{(name="$key"[ \t]+value=")([^"]*)(")}{ $1 . replace($2) . $3 }e }
elsif ($syn eq 'nsi') { s{^([ \t]*$key[ \t]+")([^"]*)(")}{ $1 . replace($2) . $3 }e }
else                  { die "unknown syntax '$syn'\n" }
PERL

    changes=$(mktemp "${TMPDIR:-/tmp}/updateVersion.XXXXXX")
    trap 'rm -f "${changes}"' EXIT

    apply()  # <file> <syntax> <key> <rule>
    {
        UV_FILE=$1 UV_SYNTAX=$2 UV_KEY=$3 UV_RULE=$4 \
        UV_X=${new_x} UV_Y=${new_y} UV_Z=${new_z} \
        UV_BUILD=${new_build} UV_MILESTONE=${milestone} UV_PREVIOUS=${previous} \
        UV_OLD_FULL=${old_full} UV_OLD_COMPACT=${old_compact} UV_OLD_BASE=${old_base} \
        UV_DAY=${new_day} UV_MONTH=${new_month} UV_YEAR=${new_year} \
        UV_CHANGES=${changes} \
        perl ${perl_inplace} -p -e "${editprog}" -- "$1" > /dev/null || die "failed while editing $1 (${3})"
    }

    if [ ${dryrun} -eq 1 ]
    then
        perl_inplace=""
    else
        perl_inplace="-i"
    fi

    # --- walk the version map -----------------------------------------
    file=""
    syntax=""
    total=0

    while IFS= read -r line || [ -n "${line}" ]
    do
        case "${line}" in
            ""|\#*) continue ;;
        esac

        if echo "${line}" | grep -qE '^file:'
        then
            file=$(echo "${line}" | awk '{ for (i = 1; i < NF; i++) if ($i == "file:") print $(i + 1) }')
            syntax=$(echo "${line}" | awk '{ for (i = 1; i < NF; i++) if ($i == "syntax:") print $(i + 1) }')
            [ -n "${file}" ] && [ -n "${syntax}" ] || die "malformed section in ${configfile}: ${line}"
            echo "*********************"
            if [ -f "${oo_path}/${file}" ]
            then
                echo "updating file: ${oo_path}/${file}"
            else
                echo "not in this branch, skipping: ${file}"
                file=""
            fi
            continue
        fi

        [ -n "${file}" ] || continue

        key=$(echo "${line}" | awk '{print $1}')
        rule=$(echo "${line}" | awk '{print $2}')
        [ -n "${key}" ] && [ -n "${rule}" ] || die "malformed entry in ${configfile}: ${line}"

        before=$(wc -l < "${changes}" | tr -d " ")
        apply "${oo_path}/${file}" "${syntax}" "${key}" "${rule}"
        after=$(wc -l < "${changes}" | tr -d " ")

        if [ "${before}" -eq "${after}" ]
        then
            echo "  ${key}: no change"
        else
            sed -n "$((before + 1)),${after}p" "${changes}" | sed "s|^${oo_path}/||;s/^/  /"
        fi
        total=$((after))
    done < "${configfile}"

    echo "***************************************************"
    if [ ${dryrun} -eq 1 ]
    then
        echo "${total} line(s) would change; nothing written (dry run)"
    else
        echo "${total} line(s) changed"
    fi
    echo "FINISHED UPDATING VERSIONS $(date)"
    echo "***************************************************"
}

run 2>&1 | tee -a "${logfile}"
exit ${PIPESTATUS[0]}
