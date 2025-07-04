# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the fat jar into the container at /app
# Assuming the JAR is in the target directory and named based on artifactId and version
ARG JAR_FILE=target/journalApp-weather-caller-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# Make port 8081 available to the world outside this container
EXPOSE 8081

# Run the jar file
ENTRYPOINT ["java","-jar","/app/app.jar"]
