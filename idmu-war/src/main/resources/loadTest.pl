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
my $threads=1;
my $wait=0;
my $count=50;
my $url="http://localhost:8080/idmu/merge.html?DragonFlyFullName=jpmc.mon.tar";
my @dataset=(
"platform_Linux_VM,zone_NA_EDC1_PROD_vmware,history_minimal,gti_rhp_base,triggerhappy",
);
my $size = @dataset;

#======================================================================
# get command arguments
my $args = @ARGV;
if ($args == 0) {
    $threads = prompt("Concurrent Threads:", $threads);
    $wait = prompt("Wait (seconds):", $wait);
    $count = prompt("Merges (per thread):", $count);
} else {
    while (my $opt = shift @ARGV) {
        if ($opt eq '-t') {$threads = shift @ARGV;}
        if ($opt eq '-w') {$wait 	= shift @ARGV;}
        if ($opt eq '-i') {$count 	= shift @ARGV;}
    }
}

system("tput cup 1 1");
print STDERR "Threads: $threads Iterate: $count Wait: $wait \n";
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
        getMonTar($url, $profileString);
        print("$t X");
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
my $tps = $threads * $count / $elapsed;
print("\nStart: $start End: $end Elapsed: $elapsed TPS: $tps \n");
exit 0;

#=============================================================================#
# Prompt for an input value
#=============================================================================#
sub getMonTar {
    my $url = shift;
    my $profileString = shift;

	#----------------------------------------
	# Pares Profile String and call merge
	# platform_Linux_VM,zone_NA_EDC1_PROD_vmware,history_minimal,gti_rhp_base,triggerhappy
	my @parts = split($profileString,",");
	my $platform= shift @parts;;
	my $zone = shift @parts;
	my $history = shift @parts;
	my $profiles = "";
	my $priority = "";
	while (my $part = shift @parts) {
		my $size = @parts;
		$profiles .= "\"{$part}\",";
		$priority .= "{++$size},\"{$part}\",";
	}
	$profiles = substr($profiles,0,length($profiles)-1);
	$priority = substr($profiles,0,length($priority)-1);
	
    my $cmdLine = "curl {$url}/idmu/merge&DragonFlyFullName=jpmc.mon.tar?ZONE={$zone}?PROFILES={$profiles}?PRIORITY={$priority}?PLATFORM={$platform}";
	my $catalog = `$cmdLine`;
	
	#----------------------------------------
	# Parse Catalog String and get archive
	my $archive = "";
	my $file = "{$url}/archives/{$archive}";
	my $wget = "wget {$file}; mv $archive mon.tar; curl -X DELETE {$file};";
	my $response = `$wget`;
}


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

