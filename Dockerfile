# Use a base image with Java runtime
FROM --platform=linux/amd64 openjdk:17-jdk-slim

# Set a working directory
WORKDIR /app

# Copy the Spring Boot JAR file into the container
COPY target/newsgroup-0.0.1-SNAPSHOT.jar app.jar

# Expose the application's port
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]