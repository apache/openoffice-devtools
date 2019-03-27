#!/usr/bin/env perl
#
# Simple perl script to "bump" the build numbers in
# AOO main/solenv/inc/minor.mk
#
# We assume the string RSCREVISION is *canon*!
#
use File::Slurp;

my $build, $milestone;
my $s1;
my $s2;
my $bumped;
print "Looking for './main/solenv/inc/minor.mk'...";
if ( -r "main/solenv/inc/minor.mk" && -w "main/solenv/inc/minor.mk") {
    print "Good, we can read and write the file.\n";
} else {
    die "Error accessing file. Wrong perms or location.\n"
}
my @lines = read_file("main/solenv/inc/minor.mk");

while (!$s1 || !$s2) {

    foreach my $i (0 .. $#lines) {
        if ($lines[$i] =~ /^RSCREVISION=\d+m(\d+).*Build:(\d+)\).*$/) {
            if (!$bumped) {
                print "Build was $2, now is ";
                $build = $2 + 1;
                print $build . "\n";
                print "Milestone was $1, now is ";
                $milestone = $1 + 1;
                print $milestone . "\n";
                $lines[$i] = "RSCREVISION=420m${milestone}(Build:${build})$/";
                $bumped = 1;
                print "Now:  $lines[$i]\n";
            }
        } elsif ($lines[$i] =~ /^BUILD=\d+$/) {
            if ($bumped && !$s1) {
                $lines[$i] = "BUILD=${build}$/";
                $s1 = 1;
                print "Updated BUILD  ";
            }
        } elsif ($lines[$i] =~ /^LAST_MINOR=m\d+$/) {
            if ($bumped && !$s2) {
                $lines[$i] = "LAST_MINOR=m${milestone}$/";
                $s2 = 1;
                print "Updated LAST_MINOR  ";
            }
        }
    }
}

print "\nWriting file...";
write_file("main/solenv/inc/minor.mk", @lines);
print "Done\n";