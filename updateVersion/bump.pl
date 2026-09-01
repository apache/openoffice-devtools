#!/usr/bin/env perl
#
# Bump the build and milestone numbers of an AOO source tree, for the
# rebuilds between two version bumps.  The product version is left
# alone; use updateVersion.sh to change that.
#
#   usage: bump.pl [oo_path]
#
# RSCVERSION in main/solenv/inc/minor.mk is canon, so the same script
# serves any release branch (AOO41X, AOO50X, trunk, ...).

use strict;
use warnings;

my $root = shift @ARGV;
$root = "." unless defined $root;
$root =~ s|/+$||;

die "usage: $0 [oo_path]\n" if @ARGV;

my $minor = "$root/main/solenv/inc/minor.mk";
my $nsi   = "$root/main/setup_native/source/win32/nsis/downloadtemplate.nsi";

die "$minor: not readable/writable. Wrong perms or location?\n"
    unless -r $minor && -w $minor;

my @lines = read_lines($minor);

my ($rscversion) = map { /^RSCVERSION=(\S+)/ ? $1 : () } @lines;
die "$minor: no RSCVERSION found\n" unless defined $rscversion;

my ($milestone, $build);
for (@lines) {
    ($milestone, $build) = ($1, $2) if /^RSCREVISION=\Q$rscversion\Em(\d+)\(Build:(\d+)\)/;
}
die "$minor: no RSCREVISION matching RSCVERSION=$rscversion\n"
    unless defined $build;

my $new_milestone = $milestone + 1;
my $new_build     = $build + 1;

print "Version $rscversion: build $build -> $new_build, milestone m$milestone -> m$new_milestone\n";

my $seen = 0;
for (@lines) {
    $seen++ if s/^RSCREVISION=\Q$rscversion\Em\d+\(Build:\d+\)\s*$/RSCREVISION=${rscversion}m${new_milestone}(Build:${new_build})\n/;
    $seen++ if s/^BUILD=\d+\s*$/BUILD=${new_build}\n/;
    $seen++ if s/^LAST_MINOR=m\d+\s*$/LAST_MINOR=m${new_milestone}\n/;
}
die "$minor: expected RSCREVISION, BUILD and LAST_MINOR, found $seen of 3\n"
    unless $seen == 3;

write_lines($minor, \@lines);
print "Updated $minor\n";

# VIProductVersion carries the build number as its third field
if (-r $nsi && -w $nsi) {
    my @nsi_lines = read_lines($nsi);
    my $hit = 0;
    for (@nsi_lines) {
        $hit++ if s/^(VIProductVersion\s+"\d+\.\d+\.)\d+(\.\d+")/$1${new_build}$2/;
    }
    if ($hit) {
        write_lines($nsi, \@nsi_lines);
        print "Updated $nsi\n";
    } else {
        print "No VIProductVersion in $nsi, skipped\n";
    }
} else {
    print "Not in this branch, skipped: $nsi\n";
}

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
