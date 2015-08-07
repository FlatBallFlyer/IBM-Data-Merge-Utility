#!/usr/bin/perl -w 
#--------------------------------------
# Install the IBM Data Merge Utility Pre-reqs
#
# usage: ./setupPrerequisites.pl [-i | -q]
#	no parameters displays help screen
#   -q = quiet, accept all defaults
#   -i = Install, prompt for options
#---------------------------------------
use strict;

#-----------------------------------------
# Declare & Initialize Variables
my $IMAGES = "/mnt/hgfs/iso/";
my $MOUNT_YUM = "yes";
my $INSTALL_TOMCAT = "yes";
my $INSTALL_MYSQL = "yes";
my $RHEL_ISO = "RHEL-7.0-20140507.0-Server-x86_64-dvd1.iso";
my $TOMCAT_TAR = "apache-tomcat-7.0.62";
my $TOMCAT_DIR = "/usr/local/tomcat7";
my $MYSQL_TAR = "mysql-connector-java-5.1.34";
my $MYSQL_PW = "drowssap";
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
	$MOUNT_YUM = prompt("Mount ISO yum repository", $MOUNT_YUM);
	$INSTALL_TOMCAT = prompt("Install Tomcat Application Server", $INSTALL_TOMCAT);
	$INSTALL_MYSQL = prompt("Install MySql RDBMS", $INSTALL_MYSQL);

	if ($MOUNT_YUM eq 'yes') {
		$RHEL_ISO = prompt("RHEL7 ISO Iamge", $RHEL_ISO);
	}
	
	if ($INSTALL_TOMCAT eq 'yes') {
		$TOMCAT_TAR = prompt("Tomcat tar file name", $TOMCAT_TAR);
	}
	$TOMCAT_DIR = prompt("Tomcat home directory", $TOMCAT_DIR);
	
	if ($INSTALL_MYSQL eq 'yes') {
		$MYSQL_TAR = prompt("MySql JDBC Driver tar file name", $MYSQL_TAR);
	}
}

#------------------------------------------------------------------
# Mount ISO yum repository
if ($MOUNT_YUM eq "yes") { 
	print "\nMounting ISO as yum repository ";
	(-e $IMAGES . '/' . $RHEL_ISO) or die "RHEL ISO Image $IMAGES/$RHEL_ISO not found!";
	
	my $filename = '/etc/yum.repos.d/iso.repo';
	open(my $fh, '>', $filename) or die "Could not open file '$filename' $!";
	print $fh "[iso64]\n";
	print $fh "name=RHEL7_64ISO\n";
	print $fh "baseurl=file:///mnt/RHEL7_64/\n";
	print $fh "enabled=1\n";
	print $fh "gpgcheck=1\n";
	print $fh "gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-redhat-release\n";
	close $fh;
	
	`mkdir -p /mnt/RHEL7_64`;
	`mount -o loop ${IMAGES}/${RHEL_ISO} /mnt/RHEL7_64/`;
	`yum clean all`;
}

#------------------------------------------------------------------
# Install Tomcat 7
if ( $INSTALL_TOMCAT eq "yes" ) { 
	(-e $IMAGES . '/' . $TOMCAT_TAR . '.tar.gz') or die "Tomcat tar Image $IMAGES/$TOMCAT_TAR not found!";

	print "\nInstalling Tomcat ";
	`tar -C /tmp -xvf ${IMAGES}/${TOMCAT_TAR}.tar.gz`;
	`mv /tmp/${TOMCAT_TAR} ${TOMCAT_DIR}`;
	`${TOMCAT_DIR}/bin/startup.sh`;
}

#------------------------------------------------------------------
# Install MySql (MariaDB)
if ( $INSTALL_MYSQL eq "yes" ) { 
	print "\nInstalling MySQL ";
	`yum install -y mariadb-server mariadb 2> /dev/null`;
	`systemctl start mariadb 2> /dev/null`;
	`mysqladmin -u root password ${MYSQL_PW} 2> /dev/null`;
	`systemctl enable mariadb.service 2> /dev/null`;
}

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
 	If you are installing tomcat 
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
