################################
######       BUILD        ######
################################

FROM maven:3.8.6-jdk-11 AS build

WORKDIR /app

COPY pom.xml /app
RUN mvn org.apache.maven.plugins:maven-dependency-plugin:3.0.0:go-offline

COPY . /app
RUN mvn package -DskipTests -o

CMD ["mvn", "spring-boot:run"]

################################
######      RELEASE       ######
################################

FROM adoptopenjdk/openjdk11:jdk-11.0.16.1_1-alpine-slim AS release

LABEL maintainer="grupacosmo/webdev"
WORKDIR /app

COPY --from=build /app/target/*.jar /app/service.jar

RUN addgroup --system app && adduser -S -s /bin/false -G app app
RUN chown -R app:app /app

USER app
CMD ["java", "-jar", "service.jar"]
