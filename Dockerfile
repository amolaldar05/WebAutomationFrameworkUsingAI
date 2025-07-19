# Use Maven with JDK 21 base image
FROM maven:3.9.7-eclipse-temurin-21

# Set environment variable for Docker context
ENV DOCKER=true

# Set working directory inside container
WORKDIR /usr/src/app

# Copy Maven project files
COPY pom.xml .
COPY src ./src
COPY testNG_files ./testNG_files
COPY config.properties .
COPY secretsConfig.properties .

# Build the project (skip tests for now)
RUN mvn clean install -DskipTests

# Run tests when container starts
CMD ["mvn", "test"]
