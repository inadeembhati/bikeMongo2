FROM openjdk
EXPOSE 8080
ADD target/spring-boot-bike.jar spring-boot-bike.jar
CMD ["java" ,"-jar" , "/spring-boot-bike.jar"]