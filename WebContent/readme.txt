Download the DataTables pre-req at: http://datatables.net/releases/DataTables-1.10.zip (extract into /tmp/DataTables-1.10)
Download the Editor pre-req at: http://editor.datatables.net/download/download?type=php (extract into /tmp/Editor-PHP-1.4)
Download the dragonfly WAR and TAR files - copy the dragonfly.tar file to /tmp 
If you don't have access to run wget, download the following files
http://apache.mirrors.tds.net/tomcat/tomcat-7/v7.0.59/bin/apache-tomcat-7.0.59.tar.gz
http://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-5.1.34.tar.gz


Installed RHEL 6.5.5 from DVD media
Add dirve sharing, configure network as bridged
vi /etc/selinux/config - 
	SELINUX=disabled
command line setup utility - Configure host name and IP (dragonfly.ibm.com / 192.168.1.101)
gui - System, Administration, Firewall, Disable Firewall - apply
vi hosts - 
	127.0.0.1	localhost.localdomain	localhost.localdomain	
	::1		localhost.localdomain	localhost.localdomain	
	192.168.1.101   localhost
	192.168.1.101   dragonfly.ibm.com
reboot, shutdown and take VMWare Snapshot
	
Make sure you have access to YUM repositories. To mount the iso images as repositories I use:
source /etc/sdtk/setup_env.sh
/mnt/hgfs/Macintosh\ HD/ibm/toolkits/sdtk/scripts/tksRHEL6_64.mountIso.sh

Which basically to the steps below:
	Mount the iso images
		mount -o loop "YourPathToIso/RHEL6.5-20131111.0-Server-i386-DVD1.iso" /mnt/RHEL6_32
		mount -o loop "YourPathToIso/RHEL6.5-20131111.0-Server-x86_64-DVD1.iso" /mnt/RHEL6_64
	
	edit /etc/yum.repos.d/iso.repo
		[iso32]
		name=RHEL6_32ISO
		baseurl=file:///mnt/RHEL6_32/
		enabled=1
		gpgcheck=1
		gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-redhat-release
		[iso64]
		name=RHEL6_64ISO
		baseurl=file:///mnt/RHEL6_64/
		enabled=1
		gpgcheck=1
		gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-redhat-release
	

######################################################################
# Configure and test apache
vi /etc/httpd/conf/httpd.conf - add
	ServerName www.{FQM}:80
vi /var/www/html/index.html - make
	<html><head></head><body><h1>It Works!</h1></body></html>
	
/sbin/service httpd start
# Now visit 192.168.1.100/index.html - you should get your "It Works!" page

######################################################################
# Install and Configure mysql 
# NOTE: If you use a password other than drowssap, make adjustments to context.xml, env vars, and config.php later in instructions
yum install -y mysql-server
service mysqld start
mysqladmin -u root password drowssap
chkconfig httpd on
chkconfig mysqld on
 
######################################################################
# Install and Configure php
yum install -y php php-mysql

vi /var/www/html/phpinfo.php - set to
	<?php phpinfo(); ?>

/sbin/service httpd  restart

# Now visit 192.168.1.101/phpinfo.php - you should get the phpinfo page

######################################################################
# Install and Configure Tomcat
yum install -y java
cd /tmp
wget http://apache.mirrors.tds.net/tomcat/tomcat-7/v7.0.59/bin/apache-tomcat-7.0.59.tar.gz
tar xzf apache-tomcat-7.0.59.tar.gz
mv apache-tomcat-7.0.59 /usr/local/tomcat7

vi /usr/local/tomcat7/conf/tomcat-users.xml -
  - Uncomment the "users" section and edit to add manager-gui role
  - Add the role "manager-gui" to the user tomcat - make note of password
  <role rolename="tomcat"/>
  <role rolename="manager-gui"/>
  <user username="tomcat" password="tomcat" roles="tomcat,manager-gui"/>
 
cd /tmp
wget http://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-5.1.34.tar.gz
tar -xvf mysql-connector-java-5.1.34.tar.gz
cd /tmp/mysql-connector-java-5.1.34
cp mysql-connector-java-5.1.34-bin.jar /usr/local/tomcat7/lib/
/usr/local/tomcat7/bin/startup.sh

# Now visit http://192.168.1.100:8080 - you should get the Tomcat Home page

############################################################################
# Install and Configure DragonFly Web App
# Copy files to /tmp for install (ADJUST)
cd /tmp
cp /mnt/hgfs/Macintosh\ HD/Users/flatballflyer/Desktop/dragonfly.tar .
mkdir Editor-PHP-1.4
cp -r /mnt/hgfs/Macintosh\ HD/Users/flatballflyer/Desktop/Editor-PHP-1.4-3.0/* ./Editor-PHP-1.4/
mkdir DataTables-1.10
cp -r /mnt/hgfs/Macintosh\ HD/Users/flatballflyer/Desktop/DataTables-1.10.5/* ./DataTables-1.10/

cd /var/www/html
tar -xvf /tmp/dragonfly.tar
cd /var/www/html
mkdir Editor
mv /tmp/Editor-PHP-1.4/* ./Editor
cd /var/www/html
mkdir -p DataTables
cd DataTables
cp -r /tmp/DataTables-1.10/media/* ./
cp -r /tmp/DataTables-1.10/extensions/ .
cp    /var/www/html/Bootstrap.php /var/www/html/Editor/php/Bootstrap.php
# (Yes you want to replace Bootstrap.php)

#########################################
# Create and Load Databases
cd /var/www/html/sql

mysql -u root -p < createTemplateDatabase.sql
# You will be prompted for database password

mysql -u root -p < createSampleDatabase.sql
# You will be prompted for database password

#########################################
# Configure Environment Vars
vi /etc/init.d/httpd - add the following lines (change password to match mysql password)
	export OPENSHIFT_MYSQL_DB_USERNAME=root
	export OPENSHIFT_MYSQL_DB_PASSWORD=drowssap
	export OPENSHIFT_MYSQL_DB_HOST=localhost
	export OPENSHIFT_MYSQL_DB_PORT=3306
	export OPENSHIFT_APP_NAME=dragonfly
	export PHP_LIB_ROOT=/var/www/html

/sbin/service httpd  restart
/sbin/service httpd  restart
# Now go to 192.168.1.101/phpinfo.php, scroll down to the Environment section, and make sure the above values are present.
# you may need to reboot here - and /usr/local/tomcat7/bin/startup.sh 

#########################################
# Configure JNDI Data Sources
vi /usr/local/tomcat7/conf/context.xml - add the following before the close </Context> tag:
NOTE: If you used something other than drowssap for the mysql password update the values below.
	<Resource auth="Container" 
		driverClassName="com.mysql.jdbc.Driver" 
		maxActive="100" 
		maxIdle="30" 
		maxWait="10000" 
		name="jdbc/dragonflyDB" 
		password="drowssap"
		type="javax.sql.DataSource" 
		url="jdbc:mysql://localhost:3306/dragonfly" 
		username="root"/>
	
	<Resource auth="Container" 
		driverClassName="com.mysql.jdbc.Driver" 
		maxActive="100" 
		maxIdle="30" 
		maxWait="10000" 
		name="jdbc/testgenDB" 
		password="drowssap"
		type="javax.sql.DataSource" 
		url="jdbc:mysql://localhost:3306/testgen" 
		username="root"/>

# If you used a password other than drowssap for the database, 
# edit /var/www/html/php/config.php and configTest.php


#########################################
# Restart Everything
/usr/local/tomcat7/bin/shutdown.sh
/sbin/service httpd restart
/usr/local/tomcat7/bin/startup.sh
# Good idea, go to 192.168.1.101/phpinfo.php, scroll down to the Environment section, and make sure the OPENSHIFT values are there


#########################################
# Deploy the WAR file
browse to 192.168.1.100:8080 - click on the "Manager" button
under Deploy, choose the dragonfly.war file, and deploy it.

You should now be able to go to http://192.168.1.100/admin.php or http://192.168.1.100/training.php
