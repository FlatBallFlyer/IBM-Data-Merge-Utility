#!/usr/bin/perl
#======================================================================
# IBM_PROLOG_BEGIN_TAG
# This is an automatically generated prolog.
#
# Licensed Materials - Property of IBM
#
# (C) COPYRIGHT International Business Machines Corp. 2011,2012,2013
# All Rights Reserved
#
# US Government Users Restricted Rights - Use, duplication or
# disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
#
# IBM_PROLOG_END_TAG
#======================================================================
# MergeLoad - Data Merge Tool load generation script
#
# usage: ./loadTest.pl [-t Threads] [-w WaitTime] [-i Iterations]
#
# i.e.
#    ./MergeLoad.pl -t 20 -w 1 -i 1000000
#======================================================================
use Time::HiRes qw(usleep nanosleep);
use strict;

#======================================================================
# initilize local vars
my $threads=-1;
my $wait=-1;
my $count=-1;
my $url="";
my @dataset=(
"platform_Linux_VM,zone_NA_EDC1_PROD_vmware,history_minimal,gti_rhp_base,triggerhappy",
);
my $size = @dataset;

#======================================================================
# get command arguments
	my $args = @ARGV;
    while (my $opt = shift @ARGV) {
    	if ($opt eq '-u') {$url = shift @ARGV;}
        if ($opt eq '-t') {$threads = shift @ARGV;}
        if ($opt eq '-w') {$wait 	= shift @ARGV;}
        if ($opt eq '-i') {$count 	= shift @ARGV;}
	}
	
	if ($url eq '') {$url = prompt("URL to Load:", $url);}
	if ($threads  lt 0) {$threads = prompt("Concurrent Threads:", $threads);}
    if ($wait lt 0) {$wait = prompt("Wait (seconds):", $wait);}
    if ($count  lt 0) {$count = prompt("Merges (per thread):", $count);}

	my $msWait = $wait*1000;

#======================================================================
# Spawn Threads and Generate Load
my $start = time;
for (my $t = 1; $t <= $threads; $t++) {
    print "Thread $t \r";
    fork and next;
    usleep (rand(1)*1000000+rand(1000));
    my $row = $t+1;
    for (my $c=1; $c <= $count;$c++) {
        usleep ($msWait);
        my $random = int(rand($size));
        my $profileString = $dataset[$random];
        
        # Call the merge 
        my $response = `curl $url > /dev/null 2> /dev/null`;
        
        print(".");
    }
    exit;
    print "THIS LINE NEVER NEVER EXECUTES!\n";
}

#======================================================================
# Wait for children to complete
my $kid;
do { $kid = waitpid(-1, 0); } while $kid > 0;
my $end = time;
my $elapsed = $end - $start;
my $volume = $threads * $count;
my $tps = $volume / $elapsed;
print("\nStart: $start End: $end Elapsed: $elapsed Volume: $volume TPS: $tps \n");
exit 0;

#=============================================================================#
# Prompt for an input value
#=============================================================================#
sub prompt {
    my $prompt = shift;
    my $default = shift;
    print "$prompt [$default]:";
    my $input = <STDIN>;
    chomp($input);
    if ($input ne '') {return $input;}
    return $default;
}

