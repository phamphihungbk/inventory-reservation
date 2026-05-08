FROM gradle:8.10.2-jdk21-alpine AS deps
WORKDIR /workspace
COPY settings.gradle.kts build.gradle.kts ./
RUN gradle help --no-daemon

FROM deps AS build
COPY src ./src
RUN gradle bootJar --no-daemon

FROM gradle:8.10.2-jdk21-alpine AS dev
WORKDIR /workspace
EXPOSE 8080
CMD ["gradle", "bootRun", "--no-daemon"]

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
