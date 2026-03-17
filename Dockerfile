# Use Eclipse Temurin for Java 21
FROM eclipse-temurin:21-jdk
WORKDIR /backend

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .

# Download dependencies (cache layer)
RUN ./gradlew dependencies --no-daemon || true

# Copy rest of the project
COPY . .

# Run the app (overridden by docker-compose for dev)
CMD ["./gradlew", "bootRun", "--no-daemon"]
