# Based on official MySql container
FROM mysql:5.7

# Copy the load sql into the container at /docker-entrypoint-initdb.d
ADD ./mysql.test.sql /docker-entrypoint-initdb.d

# Expose the default mysql port
EXPOSE 3306

# Setup MySql Password
ENV MYSQL_ROOT_PASSWORD drowssap
