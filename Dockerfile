FROM openjdk:11-jdk-slim AS build-env

RUN apt-get update && apt-get install -y maven

WORKDIR /app
COPY . .
RUN mvn clean package

FROM openjdk:11-jre-slim

WORKDIR /app
COPY --from=build-env /app/target /app/target

ENTRYPOINT ["java", "-classpath", "/app/target/classes:/app/target/lib/kotlin-stdlib.jar", "org.main.MainKt"]