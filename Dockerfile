FROM openjdk:8-alpine

COPY target/uberjar/tartataing.jar /tartataing/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/tartataing/app.jar"]
