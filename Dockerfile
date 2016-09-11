FROM java:openjdk-8-jre-alpine

COPY ./build/libs/back-it-up.jar /app/dist/back-it-up.jar
WORKDIR /app/dist

# TODO may need to consider this in a non coreos env
USER 500

# TODO Need to think about how we handle the memory issue of this backup tool.
ENTRYPOINT ["java", "-Xmx600m", "-jar", "back-it-up.jar"]
