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
my $MOUNT_YUM = "yes";
my $INSTALL_TOMCAT = "yes";
my $INSTALL_MYSQL = "yes";
my $INSTALL_TESTDB = "yes";
my $RHEL_ISO = "RHEL-7.0-20140507.0-Server-x86_64-dvd1.iso";
my $TOMCAT_TAR = "apache-tomcat-7.0.62";
my $TOMCAT_DIR = "/usr/local/tomcat7";
my $JAVA8_RPM = "jdk-8u45-linux-x64.rpm";
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
	$INSTALL_TESTDB = prompt("Install Testing Database", $INSTALL_TESTDB);

	if ($MOUNT_YUM eq 'yes') {
		$RHEL_ISO = prompt("RHEL7 ISO Iamge", $RHEL_ISO);
	}
	
	if ($INSTALL_TOMCAT eq 'yes') {
		$TOMCAT_TAR = prompt("Tomcat tar file name", $TOMCAT_TAR);
		$JAVA8_RPM = prompt("Java v1.8 rpm file", $JAVA8_RPM);
	}
	$TOMCAT_DIR = prompt("Tomcat home directory", $TOMCAT_DIR);
	
	if ($INSTALL_MYSQL eq 'yes') {
		$MYSQL_TAR = prompt("MySql JDBC Driver tar file name", $MYSQL_TAR);
	}
	
	if ($INSTALL_TESTDB eq 'yes' or $INSTALL_MYSQL eq 'yes') {
		$MYSQL_PW = prompt("MySql Root Password", $MYSQL_PW);
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
# Install Java 1.8 and Tomcat 7
if ( $INSTALL_TOMCAT eq "yes" ) { 
	(-e $IMAGES . '/' . $JAVA8_RPM) or die "Java8 RPM Image $IMAGES/$JAVA8_RPM not found!";
	(-e $IMAGES . '/' . $TOMCAT_TAR . '.tar.gz') or die "Tomcat tar Image $IMAGES/$TOMCAT_TAR not found!";

	print "\nInstalling Java 1.8 ";
	`yum erase -y java`;
	`rpm -ivh --force ${IMAGES}/${JAVA8_RPM}`;

	print "\nInstalling Tomcat ";
	`tar -C /tmp -xvf ${IMAGES}/${TOMCAT_TAR}.tar.gz`;
	`mv /tmp/${TOMCAT_TAR} ${TOMCAT_DIR}`;

	if ( $INSTALL_TESTDB eq 'yes' ) {
		(-e $IMAGES . "/" . $MYSQL_TAR . ".tar.gz") or die "MySQL JDBC Driver Tar Image $IMAGES/$MYSQL_TAR.tar.gz not found!";
		print "\nInstalling MySql JDBC Drivers";
		`tar -C /tmp -xvf ${IMAGES}/${MYSQL_TAR}.tar.gz`;
		`cp /tmp/${MYSQL_TAR}/${MYSQL_TAR}-bin.jar ${TOMCAT_DIR}/lib/`;
	}
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

#------------------------------------------------------------------
# Load Test Database
if ( $INSTALL_TESTDB eq "yes" ) { 
	(-e "${IMAGES}/TESTDB.sql") or die "Test Database Load Script $IMAGES/TESTDB.sql not found!";
	(-e "${TOMCAT_DIR}/conf/context.xml") or die "Tomcat Context.xml not found in $TOMCAT_DIR/conf!";

	print "\nInstalling Test Database ";
	`mysql --user=root --password=${MYSQL_PW} < ${IMAGES}/TESTDB.sql`;

	print "\nInstalling testDb JDBC Data Source ";
	`rm -f ${TOMCAT_DIR}/conf/contextBackup.xml`;
	`cp ${TOMCAT_DIR}/conf/context.xml ${TOMCAT_DIR}/conf/contextBackup.xml`;
	`sed -e "s/<\\/Context>/<Resource auth=\\"Container\\" driverClassName=\\"com.mysql.jdbc.Driver\\" maxActive=\\"900\\" maxIdle=\\"30\\" maxWait=\\"10\\" name=\\"jdbc\\/testgenDB\\"     password=\\"${MYSQL_PW}\\" type=\\"javax.sql.DataSource\\" url=\\"jdbc:mysql:\\/\\/localhost:3306\\/testgen\\"     username=\\"root\\"\\/><\\/Context>/g" ${TOMCAT_DIR}/conf/context.xml > /tmp/newcontext.xml`;
	`rm -f ${TOMCAT_DIR}/conf/context.xml`;
	`cp /tmp/newcontext.xml ${TOMCAT_DIR}/conf/context.xml`;
}

#------------------------------------------------------------------
# Install IDMU war and start tomcat
(-e  $IMAGES . '/idmu.war') or die "Missing IDMU.war file";
print "\nInstalling IDMU and Starting Tomcat";
`cp ${IMAGES}/idmu.war ${TOMCAT_DIR}/webapps`;
`${TOMCAT_DIR}/bin/startup.sh`;
`sleep 3`;
print "\nDone!\n";

#------------------------------------------------------------------
# Verification (Simple)
# This command should echo "This is the default tempalte" - minimally functional
`curl "localhost:8080/idmu/Merge/"`; 

#------------------------------------------------------------------
# Verification (SQL Data Provider - Requres testgenDb)
# This command should return a long report of "Customers" and "Contacts"
if ( $INSTALL_TESTDB eq "yes" ) { 
	`curl "localhost:8080/idmu/Merge/?DragonFlyFullName=jdbc.report."`;
}

#------------------------------------------------------------------
# Verification (Advanced - tar archive creation)
# This command should return an empty string and create a tar file in /tmp/merge
if ( $INSTALL_TESTDB eq 'yes' ) { 
	`curl "localhost:8080/idmu/Merge/?DragonFlyFullName=csvDef.functional."`;
}

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

 Copy these files to your install source directory
	- idmu.war 
		download https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility
		you will find this in the idmu-war folder
	If you are installing the IDMU Test Database
	- TESTDB.sql
		in the idmu-war/src/main/resources folder 
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
