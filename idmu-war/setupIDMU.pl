#!/usr/bin/perl -w 
#--------------------------------------
# Install the IBM Data Merge Utility
#
# usage: ./install.pl [-i | -q]
#	no parameters displays help screen
#   -q = quiet, accept all defaults
#   -i = Install, prompt for options
#---------------------------------------
use strict;

#-----------------------------------------
# Declare & Initialize Variables
my $IMAGES = "/mnt/hgfs/iso/";
my $INSTALL_TESTDB = "yes";
my $INSTALL_TEMPLATEDB = "yes";
my $TOMCAT_DIR = "/usr/local/tomcat7";
my $MYSQL_PW = "drowssap";
my $INSTALL_SYSTEM_TEMPLATES = "yes";
my $QUIET = '';
#------------------------------------------------------------------
# Get runtime parameters (from command if possible)
if ($ARGV[0] eq "-q") {$QUIET = 'y';}
if ($ARGV[0] eq "-i") {$QUIET = 'n';}
if ($QUIET eq '') {help();}

#------------------------------------------------------------------
# Prompt for Install Options
if ($QUIET eq 'n') {
	$IMAGES = prompt("Install Images path", $IMAGES);
	$TOMCAT_DIR = prompt("Tomcat home directory", $TOMCAT_DIR);
	$INSTALL_SYSTEM_TEMPLATES = prompt("Install System Templates", $INSTALL_SYSTEM_TEMPLATES);
	$INSTALL_TEMPLATEDB = prompt("Install Templates Database Persistence", $INSTALL_SYSTEM_TEMPLATES);
	$INSTALL_TESTDB = prompt("Install Testing Database and Templates", $INSTALL_TESTDB);
	if ($INSTALL_TESTDB eq 'yes' || $INSTALL_TEMPLATEDB eq 'yes') {
		$MYSQL_PW = prompt("MySql Root Password", $MYSQL_PW);
	}
}

#------------------------------------------------------------------
# Install IDMU war and start tomcat
(-e  $IMAGES . '/ROOT.war') or die "Missing IDMU ROOT.war file";
print "\nInstalling IDMU and Starting Tomcat";

`${TOMCAT_DIR}/bin/shutdown.sh`;
`rm -rf ${TOMCAT_DIR}/webapps/ROOT`;
`cp ${IMAGES}/ROOT.war ${TOMCAT_DIR}/webapps`;
`${TOMCAT_DIR}/bin/startup.sh`;
`sleep 3`;

#------------------------------------------------------------------
# Load Template Database
if ( $INSTALL_TEMPLATEDB eq "yes" ) {
    (-e "${TOMCAT_DIR}/webapps/ROOT/WEB-INF/database/IDMU.v3.MYSQL_TEMPLATE.sql") or die "Test Database Load Script $TOMCAT_DIR/webapps/ROOT/WEB-INF/database/IDMU.v3.MYSQL_TEMPLATE.sql not found!";
    print "\nInstalling Test Database ";
    `mysql --user=root --password=${MYSQL_PW} < ${TOMCAT_DIR}/webapps/ROOT/WEB-INF/database/IDMU.v3.MYSQL_TEMPLATE.sql`;
	`${TOMCAT_DIR}/bin/shutdown.sh`;
	`rm ${TOMCAT_DIR}/webapps/ROOT/WEB-INF/web.xml`;
	`cp ${TOMCAT_DIR}/webapps/ROOT/WEB-INF/web_db_persist.xml web.xml`; 
	`${TOMCAT_DIR}/bin/startup.sh`;
}

#------------------------------------------------------------------
# Load System Templates
if ( $INSTALL_SYSTEM_TEMPLATES eq "yes" ) {
    (-e "${TOMCAT_DIR}/webapps/ROOT/WEB-INF/templates/packages/system.json") or die "Test Database Load Script $TOMCAT_DIR/webapps/ROOT/WEB-INF/templates/packages/system.json not found!";
    print "\nLoading System Templates";
    `curl --upload ${TOMCAT_DIR}/webapps/ROOT/WEB-INF/templates/packages/system.json http://localhost:8080/idmu/templatePackage`;
}

#------------------------------------------------------------------
# Load Test Database and Templates
if ( $INSTALL_TESTDB eq "yes" ) {
    (-e "${TOMCAT_DIR}/webapps/ROOT/WEB-INF/database/IDMU.v3.MYSQL_TEST.sql") or die "Test Database Load Script $IMAGES/IDMU.v3.MYSQL_TEST.sql not found!";
    print "\nInstalling Test Database ";
    `mysql --user=root --password=${MYSQL_PW} < ${TOMCAT_DIR}/webapps/ROOT/WEB-INF/database/IDMU.v3.MYSQL_TEST.sql`;
    (-e "${TOMCAT_DIR}/webapps/ROOT/WEB-INF/templates/packages/test.json") or die "Test Database Load Script $TOMCAT_DIR/webapps/ROOT/WEB-INF/templates/packages/test.json not found!";
    print "\nLoading Testing Templates";
    `curl --upload ${TOMCAT_DIR}/webapps/ROOT/WEB-INF/templates/packages/test.json http://localhost:8080/idmu/templatePackage`;
}

print "\nDone!\n";

#------------------------------------------------------------------
# Verification (Simple)
# This command should echo "This is the default tempalte" - minimally functional
print `curl "localhost:8080/idmu/merge"`;

print "\nDone\n";
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

#=============================================================================#
# Print Hepl Message / Instructions
#=============================================================================#
sub help {
	print '
 VMWare Setup Recommendations
	- Installing from RHEL7_64 ISO images
	- Do not use "Easy Install"
	- Share images folder for tar/war/rpm files (iso)
	- 2CPU, 1024Mb RAM
		
 Redhat Install Recommendations
	- Software Selection - Server with GUI
	- Install VMWare Tools
	- Configure firewall 
		- http/https protocols
		- port 8080
		- whitelist $TOMCAT_DIR/bin/startup.sh

This Install supports Tomcat v7 and MariaDB 5.5 to Install copy the files below to an install images folder and run install.pl -q or -i

 Copy these files to your install source directory
	- idmu.war 
		From the release page
	If you are installing the IDMU Test Database
	- TESTDB.sql
		From the release page 
 	If you are installing tomcat 
	- jdk-8u45-linux-x64.rpm 
		see http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
	- apache-tomcat-7.0.62.tar.gz 
		wget http://www.trieuvan.com/apache/tomcat/tomcat-7/v7.0.62/bin/apache-tomcat-7.0.62.tar.gz
	If you are installing MySql 
 	- mysql-connector-java-5.1.34.tar.gz 
 		wget http://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-5.1.34.tar.gz
	If you need to mount an ISO images for yum repository (to install MySql)
	- RHEL-7.0-20140507.0-Server-x86_64-dvd1.iso 

NOTE Different file names can be used, you are prompted for file names, do not include the .tar.gz extension in the file name

Run install.pl -i for an interactive install
Run install.pl -q for a quiet install (accept all defaults shown above, install all steps, install files are in /mnt/hgfs/iso/ )
';
exit 1;
}
