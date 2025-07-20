# Use Maven with JDK 21 base image
FROM maven:3.9.7-eclipse-temurin-21

# Set environment variable for Docker context
ENV DOCKER=true
#ENV HUB_URL=http://localhost:4444/


# Set working directory inside the container
WORKDIR /app

# Copy Maven project files into container
COPY pom.xml .
COPY src ./src
COPY testNG_files/grid_testNG.xml ./testNG_files/grid_testNG.xml

# Download dependencies and build the project (skip tests for now)
RUN mvn clean install -DskipTests

# Run ONLY grid_testNG.xml when container starts
CMD ["mvn", "test", "-DsuiteXmlFile=./testNG_files/grid_testNG.xml"]

