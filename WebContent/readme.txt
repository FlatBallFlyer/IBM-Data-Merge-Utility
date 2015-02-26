yum install php
yum install java
yum install mysql-server

wget http://php.net/get/php-5.6.6.tar.gz/from/this/mirror


vi hosts - fqdm
vi /etc/httpd/conf/httpd.conf - add
	ServerName www.{FQM}:80

apachectl start
/usr/local/tomcat7/bin/startup.sh

mysqladmin password drowsapp
mysqladmin -u root -p < createthetables.sql

cd /tmp
wget http://apache.bytenet.in/tomcat/tomcat-7/v7.0.57/bin/apache-tomcat-7.0.59.tar.gz
tar xzf apache-tomcat-7.0.57.tar.gz
mv apache-tomcat-7.0.57 /usr/local/tomcat7
cd /usr/local/tomcat7
vi ./conf/context.xml

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

/var/www/html 




PHPMYADMIN
adduser phpmy
passwd phpmy
cd /tmp
wget http://sourceforge.net/projects/phpmyadmin/files/phpMyAdmin/4.3.10/phpMyAdmin-4.3.10-english.tar.gz
tar -xvf phpMyAdmin-4.3.10-english.tar.gz
mv phpMyAdmin-4.3.10-english /var/www/html/phpMyAdmin
cd /var/www/html
chown -R phpmy.daemon phpMyAdmin/

cd /var/www/html/phpMyAdmin
mkdir config
chmod o+rw config
cp config.sample.inc.php config/config.inc.php

