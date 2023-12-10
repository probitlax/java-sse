# Stage 1: Build Maven project
FROM maven:3.8.3-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package

# Stage 2: Run the application
FROM openjdk:17-oracle AS production
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/data-harvester-0.0.1.jar
CMD ["java", "-jar", "/app/data-harvester-0.0.1.jar"]
