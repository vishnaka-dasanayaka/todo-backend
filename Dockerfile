FROM maven:3.9.9 AS builder
WORKDIR /build
# Leverage Docker cache by first copying pom.xml separately
COPY pom.xml .
RUN mvn dependency:go-offline -B
# Copy the rest of the code
COPY src ./src
# Package the application
RUN mvn clean package -DskipTests -B

FROM openjdk:21-slim
WORKDIR /app
# Copy only the JAR
COPY --from=builder /build/target/*.jar app.jar
# Expose the application port
EXPOSE 8081
# Use production profile explicitly

ENTRYPOINT ["java", "-jar", "app.jar"]
