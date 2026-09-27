#!/usr/bin/env perl
#
# Bump the copyright year of an AOO source tree (the yearly "Update
# copyright year to YYYY" commit).
#
#   usage: bumpYear.pl [-n|--dry-run] [-y|--year <yyyy>] [oo_path]
#
# The current year is read from the ASF line of main/NOTICE, so the same
# script serves any release branch; the default target is that year + 1.
# Each rule rewrites the year wherever its pattern matches, whatever the
# old value, so copies that were missed in earlier years are caught up.

use strict;
use warnings;
use Getopt::Long;

my ($dry_run, $year);
GetOptions("n|dry-run" => \$dry_run, "y|year=i" => \$year)
    or die "usage: $0 [-n|--dry-run] [-y|--year <yyyy>] [oo_path]\n";

my $root = shift @ARGV;
$root = "." unless defined $root;
$root =~ s|/+$||;

die "usage: $0 [-n|--dry-run] [-y|--year <yyyy>] [oo_path]\n" if @ARGV;

my $notice = "$root/main/NOTICE";
my ($current) = map { /^Copyright 2011-(\d{4}) The Apache Software Foundation/ ? $1 : () }
                read_lines($notice);
die "$notice: no 'Copyright 2011-yyyy The Apache Software Foundation' line\n"
    unless defined $current;

$year = $current + 1 unless defined $year;
die "target year $year is before the tree's current year $current\n"
    if $year < $current;

print "Copyright year: $current -> $year", ($dry_run ? " (dry run)" : ""), "\n";

my $packinfo = qr/^(copyright = "2012-)\d{4}( by The Apache Software Foundation")/;
my $odk_html = qr/(Copyright &copy; 2011-)\d{4}( The Apache Software Foundation)/;
my $license  = qr/^(\s*Copyright 2011-)\d{4}( Apache Software Foundation)/;

my @rules = (
    [ "main/LICENSE",                     $license ],
    [ "main/LICENSE_ALv2",                $license ],
    [ "main/helpauthoring/license/LICENSE", $license ],
    [ "main/NOTICE",                      qr/^(Copyright 2011-)\d{4}( The Apache Software Foundation)/ ],
    [ "main/cui/source/dialogs/about.cxx", qr/(rtl::OUString sYear\( RTL_CONSTASCII_USTRINGPARAM\(")\d{4}("\) \);)/ ],
    # also feeds VER_YEAR, the copyright year of the Windows .rc version resources
    [ "main/solenv/inc/version.lst",      qr/^(OOOBASEVERSIONYEAR=)\d{4}()/ ],
    [ "main/setup_native/source/win32/nsis/downloadtemplate.nsi",
      qr/^(VIAddVersionKey LegalCopyright "\(c\) 2012-)\d{4}( The Apache Software Foundation")/ ],
    [ "main/odk/index.html",                           $odk_html ],
    [ "main/odk/docs/install.html",                    $odk_html ],
    [ "main/odk/docs/notsupported.html",               $odk_html ],
    [ "main/odk/docs/tools.html",                      $odk_html ],
    [ "main/odk/examples/examples.html",               $odk_html ],
    [ "main/odk/examples/DevelopersGuide/examples.html", $odk_html ],
    map { [ "main/setup_native/source/packinfo/packinfo_$_.txt", $packinfo ] }
        qw(brand office office_lang sdkoo ure),
);

my $failed = 0;
for my $rule (@rules) {
    my ($rel, $re) = @$rule;
    my $file = "$root/$rel";
    unless (-e $file) {
        print "Not in this branch, skipped: $rel\n";
        next;
    }
    unless (-r $file && -w $file) {
        warn "$rel: not readable/writable\n";
        $failed++;
        next;
    }

    my @lines = read_lines($file);
    my ($hits, $changed) = (0, 0);
    for (@lines) {
        my $before = $_;
        $hits++ if s/$re/$1$year$2/;
        next if $_ eq $before;
        $changed++;
        if ($dry_run) {
            print "  $rel:\n    - $before    + $_";
        }
    }

    if (!$hits) {
        warn "$rel: expected copyright line not found\n";
        $failed++;
    } elsif (!$changed) {
        print "Already $year: $rel\n";
    } else {
        write_lines($file, \@lines) unless $dry_run;
        print(($dry_run ? "Would update" : "Updated"), " $rel ($changed line", ($changed == 1 ? "" : "s"), ")\n");
    }
}

# Report ASF copyright ranges the rules above do not cover, so new copies
# get noticed and added here rather than silently left behind.
if (-e "$root/.git" && $year != $current) {
    my %known = map { $_->[0] => 1 } @rules;
    my @left = grep { my ($f) = split /:/; !$known{$f} }
               qx(git -C "$root" grep -n -I -E "20(11|12)-$current .{0,4}Apache Software Foundation" -- . ":!*.sdf" ":!*.po" 2>/dev/null);
    if (@left) {
        print "\nOther ASF copyright lines still at $current (not handled, check by hand):\n";
        print "  $_" for @left;
    }
}

exit($failed ? 1 : 0);

sub read_lines {
    my ($file) = @_;
    open my $fh, "<", $file or die "$file: $!\n";
    my @l = <$fh>;
    close $fh;
    return @l;
}

sub write_lines {
    my ($file, $lines) = @_;
    open my $fh, ">", $file or die "$file: $!\n";
    print {$fh} @$lines;
    close $fh or die "$file: $!\n";
}
