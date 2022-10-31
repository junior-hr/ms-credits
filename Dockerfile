FROM openjdk:11
VOLUME /tmp
EXPOSE 8084
ADD ./target/ms-credits-0.0.1-SNAPSHOT.jar ms-credits.jar
ENTRYPOINT ["java","-jar","/ms-credits.jar"]