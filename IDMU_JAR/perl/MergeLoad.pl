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
# usage: ./MergeLoad.pl [-t Threads] [-w WaitTime] [-i Iterations]
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
my $verbose = 0;
my $url="http://localhost:8080/dragonfly/Merge.html?";
my @dataset=(
	"collection=test.root&name=allReports", 
	"collection=test.root&name=OneCustomer&idcustomer=1",
	"collection=test.root&name=OneCustomer&idcustomer=2",
	"collection=test.root&name=OneCustomer&idcustomer=3",
	"collection=test.root&name=OneCustomer&idcustomer=4",
	"collection=test.root&name=OneCustomer&idcustomer=5",
	"collection=test.root&name=OneCustomer&idcustomer=6",
	"collection=test.root&name=OneCustomer&idcustomer=7",
	"collection=test.root&name=OneCustomer&idcustomer=8",
	"collection=test.root&name=OneCustomer&idcustomer=9",
	"collection=test.root&name=OneCustomer&idcustomer=10",
	"collection=test.root&name=OneCustomer&idcustomer=11",
	"collection=test.root&name=OneCustomer&idcustomer=12",
	"collection=test.root&name=OneCustomer&idcustomer=13",
	"collection=test.root&name=OneCustomer&idcustomer=14",
	"collection=test.root&name=OneCustomer&idcustomer=15",
	"collection=test.root&name=OneCustomer&idcustomer=16",
	"collection=test.root&name=OneCustomer&idcustomer=17",
	"collection=test.root&name=OneCustomer&idcustomer=18",
	"collection=test.root&name=OneCustomer&idcustomer=19",
	"collection=test.root&name=OneCustomer&idcustomer=20",
	"", 
	"", 
	"", 
	"", 
	"", 
	"DragonFlyCacheReset", 
	"collection=test.root&name=SMSroot",
	"collection=test.root&name=SMTProot",
	"collection=test.root&name=RPTroot",
);

#======================================================================
# get command arguments
my $args = @ARGV;
while (my $opt = shift @ARGV) {
	if ($opt eq '-t') {$threads = shift @ARGV;}
	if ($opt eq '-w') {$wait 	= shift @ARGV;}
	if ($opt eq '-i') {$count 	= shift @ARGV;}
	if ($opt eq '-v') {$verbose = shift @ARGV;}
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
		my $random = int(rand(6)+rand(6)+rand(6));
		my $cmdLine = 'curl "' . $url;
		$cmdLine .= $dataset[$random];
		$cmdLine .= '"';
		if ($verbose < 2) {
			$cmdLine .= ' > /dev/null ';
		}
		if ($verbose < 1) {
			$cmdLine .= ' 2> /dev/null ';			
		}
		usleep ($msWait);
		print('X');
		#print("$c from $t         \r");
		system($cmdLine);
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
