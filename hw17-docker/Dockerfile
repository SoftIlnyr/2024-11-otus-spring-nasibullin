FROM bellsoft/liberica-openjdk-alpine-musl:21.0.1
COPY /target/hw17-docker-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
