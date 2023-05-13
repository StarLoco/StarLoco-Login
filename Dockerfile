FROM alpine:latest

RUN apk add --no-cache openjdk11-jre

COPY login.jar /app/login.jar
COPY docker.config.properties /app/login.config.properties

WORKDIR /app

CMD ["java", "-jar", "login.jar"]