FROM adoptopenjdk/openjdk11:jre-11.0.6_10-alpine

ENV LANG=C.UTF-8

# Install glibc
RUN apk -v --update add ca-certificates && \
    rm -rf /tmp/*.apk

ADD lc-mt-sample-app-*.jar /local/lc-mt-sample-app/bin/lc-mt-sample-app.jar

ENTRYPOINT ["java", "-jar", "/local/lc-mt-sample-app/bin/lc-mt-sample-app.jar"]

EXPOSE 5000 25000