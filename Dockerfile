FROM openjdk:11 AS builder

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x ./gradlew
RUN ./gradlew bootJar

FROM openjdk:11
COPY --from=builder build/libs/*.jar app.jar
COPY --from=builder src/main/resources/dict/synonym.dict dict/synonym.dict

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]