FROM openjdk:11

ARG JAVA_OPTS

ENV JAVA_OPTS=$JAVA_OPTS

COPY ./build/libs/Codebase-0.0.1-SNAPSHOT.jar codebase.jar

EXPOSE 8080

ENTRYPOINT exec java $JAVA_OPTS -jar codebase.jar
