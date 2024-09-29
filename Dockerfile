FROM openjdk:17.0.1-jdk-slim AS build-env

RUN apt-get update && apt-get install -y maven

WORKDIR /app
COPY . .
RUN /bin/bash -c "mvn clean package"

FROM openjdk:17.0.1-jdk-slim

WORKDIR /app
COPY --from=build-env /app/target /app/target

ENTRYPOINT ["java", "-classpath", "/app/target/classes:/app/target/lib/kotlin-stdlib.jar", "org.main.MainKt"]