FROM maven:3.8.5-openjdk-17 as build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package

FROM openjdk:17-jdk

COPY --from=build /app/target/radar_control-1.0-jar-with-dependencies.jar /radar-control.jar

CMD ["java", "-jar", "/radar-control.jar"]
