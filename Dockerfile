FROM openjdk:8

ADD target/qa-0.0.1-SNAPSHOT.jar application.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "application.jar"]

