# Based on official DB2 Express container
FROM ibmcom/db2express-c:latest

# Set the working directory to /app
WORKDIR /data

# Copy the current directory contents into the container at /app
ADD ./db2.test.sql /data
ADD ./superInit.sh /data
ADD ./initialize.sh /data

# Make port 80 available to the world outside this container
#EXPOSE 80

# Define environment variable
ENV DB2INST1_PASSWORD dr-ow-ss-Ap
ENV LICENSE=accept

# Load the test data when the container launches
ENTRYPOINT /data/superInit.sh

