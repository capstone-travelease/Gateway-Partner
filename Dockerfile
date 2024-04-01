FROM openjdk:21

VOLUME /tmp

COPY target/*.jar Gateway-API-0.0.1-SNAPSHOT.jar

EXPOSE 7590
ENTRYPOINT ["java","-jar","/Gateway-API-0.0.1-SNAPSHOT.jar"]