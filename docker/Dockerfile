# Use a Maven image to build the Spring Boot application
FROM maven:3.9.9-amazoncorretto-21-alpine AS build
WORKDIR /app
COPY ./starter_code /app
RUN mvn clean package -DskipTests

# target for development environment
FROM maven:3.9.9-amazoncorretto-21-alpine AS development
# Update and upgrade Alpine packages
RUN apk update --no-cache && apk upgrade --no-cache
RUN apk add --no-cache sudo=1.9.15_p5-r0

# Install dependencies
RUN apk add --no-cache \
    bash \
    # sudo \
    wget \
    unzip \
    xvfb \
    libx11 \
    # libgconf-2 \
    curl \
    chromium \
    chromium-chromedriver

# Create a group and user
RUN addgroup -g 1000 appgroup && adduser -D -G appgroup -s /bin/bash -u 1000 appuser
RUN echo "appuser ALL=(ALL) NOPASSWD:ALL" > /etc/sudoers.d/appuser
# Set the working directory
WORKDIR /app
# Create log directory with permissions
RUN chmod -R 777 /app
# Expose the port the Spring Boot application runs on
EXPOSE 8080
# Copy the entrypoint script
COPY ./docker/entrypoint.sh /app/entrypoint.sh
# Make the entrypoint script executable
RUN chmod +x /app/entrypoint.sh
# Switch to the non-root user
USER appuser
# Set the entrypoint to the script
CMD ["/bin/sh", "-c", "sleep infinity"]


# stage deefault runtime
# Use Amazon Corretto JDK 21 as the base image for running the Spring Boot application
FROM amazoncorretto:21-alpine3.20 AS runtime
# Update and upgrade Alpine packages
RUN apk update --no-cache && apk upgrade --no-cache
RUN apk add --no-cache sudo=1.9.15_p5-r0
# Create a group and user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
RUN echo "appuser ALL=(ALL) NOPASSWD:ALL" > /etc/sudoers.d/appuser
# Set the working directory
WORKDIR /app
# Create log directory with permissions
RUN chmod -R 777 /app

# Copy the application JAR file
COPY --from=build /app/target/webapp.jar /app/webapp.jar

# COPY ./ /app
# RUN mvn clean package -DskipTests

# Set ownership for the JAR file
RUN chown appuser:appgroup /app/webapp.jar

# Expose the port the Spring Boot application runs on
EXPOSE 8086
# Copy the entrypoint script
COPY ./docker/entrypoint.sh /app/entrypoint.sh
# Make the entrypoint script executable
RUN chmod +x /app/entrypoint.sh
# Switch to the non-root user
USER appuser
# Default command to run the application
CMD ["java", "-jar", "/app/webapp.jar"]
 