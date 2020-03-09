#
# Run
#
FROM java:openjdk-8-jdk-alpine
ADD target/serviceB-0.0.1-SNAPSHOT.jar serviceB.jar
EXPOSE 8081
ENTRYPOINT [ "java", "-jar", "serviceB.jar" ]