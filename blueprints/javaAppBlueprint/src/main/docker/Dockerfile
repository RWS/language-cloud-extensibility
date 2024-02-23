FROM adoptopenjdk/openjdk11:jre-11.0.6_10-alpine

ENV LANG=C.UTF-8

# Install glibc
RUN apk -v --update add ca-certificates && \
    rm -rf /tmp/*.apk

ADD lc-blueprint-app-*.jar /local/lc-blueprint-app/bin/lc-blueprint-app.jar

ENTRYPOINT ["java", "-jar", "/local/lc-blueprint-app/bin/lc-blueprint-app.jar"]

EXPOSE 5000 25000