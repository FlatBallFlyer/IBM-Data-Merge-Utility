# Based on official Cloudant Developer edition
FROM ibmcom/cloudant-developer

# Set the working directory to /app
WORKDIR /app

# Copy the startup script and load data into the container at /app
ADD ./testData.txt /app
ADD ./initialize.sh /app

# Make port 80 available to the world outside this container as port 81
EXPOSE 81:80

# Run data load command
#RUN bash /app/initialize.sh

