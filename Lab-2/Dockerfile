# Use OpenJDK 23 as base image
FROM eclipse-temurin:23-jdk

# Set the working directory
WORKDIR /app

# Copy the built jar file
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
