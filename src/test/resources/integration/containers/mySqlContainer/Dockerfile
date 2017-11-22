# Based on official MySql container
FROM mysql:5.7

# Set the working directory 
#WORKDIR /docker-entrypoint-initdb.d

# Copy the current directory contents into the container at /app
ADD ./mysql.test.sql /docker-entrypoint-initdb.d

