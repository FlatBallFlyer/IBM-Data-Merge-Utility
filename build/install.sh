#!/usr/bin/env bash
###################################################################
# VMWare Setup / Linux Install
#	- Installing from RHEL7_64 ISO images
#	- Do not use "Easy Install"
# Custom VM Settings
#		- Share iso folder
#		- 2CPU, 1024Mb RAM
#		- Network Bridged
# Start Install
#	- Software Selection - Server with GUI
#	- Configure Network (IPv4 address)
#	- Begin Install
#	- Set password and administrator while installing
#	- Reboot and accept license
#	- Install VMWare Tools
#	- Configure firewall 
#		- http/https protocols
#		- port 8080
#		- whitelist /usr/local/tomcat7/bin/startup.sh
###################################################################

###################################################################
# Copy these files to your shared mount point, and update the export statement with the path to this directory: 
#	- apache-tomcat-7.0.62.tar from http://www.trieuvan.com/apache/tomcat/tomcat-7/v7.0.62/bin/apache-tomcat-7.0.62.tar
# 	- mysql-connector-java-5.1.34.tar from (http://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-5.1.34.tar
#	- jdk-8u45-linux-x64.rpm from http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
#	- idmu.war from https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility
#	- TESTDB.sql from https://github.com/FlatBallFlyer/IBM-Data-Merge-Utility
#	- TIADB.sql (from email)
#	- RHEL-7.0-20140507.0-Server-x86_64-dvd1.iso (RHEL ISO Image if you need to mount local YUM repo)
###################################################################
export IMAGES="/mnt/hgfs/iso/"

###################################################################
# Mount ISO as YUM Repo (Skip if you have a yum repo)
###################################################################
cat <<EOF > /etc/yum.repos.d/iso.repo	
[iso64]
name=RHEL7_64ISO
baseurl=file:///mnt/RHEL7_64/
enabled=1
gpgcheck=1
gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-redhat-release
EOF
mkdir -p /mnt/RHEL7_64
mount -o loop $IMAGES/RHEL-7.0-20140507.0-Server-x86_64-dvd1.iso /mnt/RHEL7_64/
yum clean all	

###################################################################
# Install Java 1.8 and Tomcat 7
###################################################################
cd /tmp
yum erase -y java
rpm -ivh --force $IMAGES/jdk-8u45-linux-x64.rpm

cd /tmp
tar -xvf $IMAGES/apache-tomcat-7.0.62.tar
mv apache-tomcat-7.0.62 /usr/local/tomcat7

###################################################################
# Install MySql (MariaDB)
###################################################################
yum install -y mariadb-server mariadb
systemctl start mariadb
mysqladmin -u root password drowssap
systemctl enable mariadb.service

###################################################################
# Install MySql JBDC Drivers
###################################################################
cd /tmp
tar -xvf $IMAGES/mysql-connector-java-5.1.34.tar
cd /tmp/mysql-connector-java-5.1.34
cp mysql-connector-java-5.1.34-bin.jar /usr/local/tomcat7/lib/

###################################################################
# Load Databases
###################################################################
cd /tmp
mysql --user=root --password=drowssap < $IMAGES/TESTDB.sql
mysql --user=root --password=drowssap < $IMAGES/TIADB.sql
cp $IMAGES/idmu.war /usr/local/tomcat7/webapps

###################################################################
# Add the Test DB to /usr/local/tomcat7/conf/context.xml
###################################################################
# <Resource auth="Container" driverClassName="com.mysql.jdbc.Driver"
# 	maxActive="900" maxIdle="30" maxWait="10" name="jdbc/testgenDB"
# 	password="drowssap" type="javax.sql.DataSource" url="jdbc:mysql://localhost:3306/testgen"
# 	username="root" />
###################################################################
rm -f /usr/local/tomcat7/conf/contextBackup.xml
cp /usr/local/tomcat7/conf/context.xml /usr/local/tomcat7/conf/contextBackup.xml
sed -e "s/<\/Context>/<Resource auth=\"Container\" driverClassName=\"com.mysql.jdbc.Driver\" maxActive=\"900\" maxIdle=\"30\" maxWait=\"10\" name=\"jdbc\/tiaDB\"     password=\"drowssap\" type=\"javax.sql.DataSource\" url=\"jdbc:mysql:\/\/localhost:3306\/TIA\"     username=\"root\"\/><\/Context>/g" /usr/local/tomcat7/conf/context.xml > /tmp/newcontext.xml
rm -f /usr/local/tomcat7/conf/context.xml
cp /tmp/newcontext.xml /usr/local/tomcat7/conf/context.xml


###################################################################
# Add the TIA DB to /usr/local/tomcat7/conf/context.xml
###################################################################
# <Resource auth="Container" driverClassName="com.mysql.jdbc.Driver"
# 	maxActive="900" maxIdle="30" maxWait="10" name="jdbc/tiaDB"
# 	password="drowssap" type="javax.sql.DataSource" url="jdbc:mysql://localhost:3306/TIA"
# 	username="root" />
###################################################################
rm -f /usr/local/tomcat7/conf/contextBackup.xml
cp /usr/local/tomcat7/conf/context.xml /usr/local/tomcat7/conf/contextBackup.xml
sed -e "s/<\/Context>/<Resource auth=\"Container\" driverClassName=\"com.mysql.jdbc.Driver\" maxActive=\"900\" maxIdle=\"30\" maxWait=\"10\" name=\"jdbc\/testgenDB\" password=\"drowssap\" type=\"javax.sql.DataSource\" url=\"jdbc:mysql:\/\/localhost:3306\/testgen\" username=\"root\"\/><\/Context>/g" /usr/local/tomcat7/conf/context.xml > /tmp/newcontext.xml
rm -f /usr/local/tomcat7/conf/context.xml
cp /tmp/newcontext.xml /usr/local/tomcat7/conf/context.xml


###################################################################
# Start Tomcat (and sleep for it to finish)
###################################################################
/usr/local/tomcat7/bin/startup.sh
sleep 3

####################################################################
# Verification (Simple)
# This command should echo "This is the default tempalte" - minimally functional
curl localhost:8080/idmu/Merge/ 

####################################################################
# Verification (SQL Data Provider - Requres testgenDb)
# This command should return a long report of "Customers" and "Contacts"
curl localhost:8080/idmu/Merge/?DragonFlyFullName=jdbc.report.

####################################################################
# Verification (Advanced - tar archive creation)
# This command should return a "catalog" of generated files and create a tar in /tmp/merge
curl "localhost:8080/idmu/Merge/?DragonFlyFullName=root.mon.&PLATFORM=Linux&ZONE=25+Bank+Street+PROD&PERF=history_minimal&PROFILES=%22gti_rhp_base%22%2C%22jkw_test_profile%22"
