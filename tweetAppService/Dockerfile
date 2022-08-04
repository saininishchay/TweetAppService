FROM openjdk:11
EXPOSE 8082
ADD target/tweet-app-service.jar tweet-app-service.jar
ENTRYPOINT ["java","-jar","/tweet-app-service.jar"]
