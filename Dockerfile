# Multi-stage Dockerfile: build the jar with Maven, then run with a slim JDK
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /build

# Copy only what we need for a build to leverage Docker cache
COPY pom.xml .
COPY src ./src

# Build the project and create the jar; then normalize the jar name to app.jar
RUN mvn -B -DskipTests package \
	&& mkdir -p /build/out \
	&& cp /build/target/*.jar /build/out/app.jar

# Use a minimal JRE image to run the application
FROM eclipse-temurin:19-jre-jammy
WORKDIR /app

# Copy the normalized jar from the build stage
COPY --from=build /build/out/app.jar app.jar

# Create a non-root user and ensure /app is owned by that user
RUN addgroup --system app && adduser --system --ingroup app app \
    && chown -R app:app /app

# Switch to the non-root user
USER app

# Expose the application port
EXPOSE 8080

# Run the application with optimized JVM settings for container environments
ENTRYPOINT ["java","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=25.0","-jar","/app/app.jar"]
