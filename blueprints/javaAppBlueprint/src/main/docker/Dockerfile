FROM adoptopenjdk/openjdk11:jre-11.0.6_10-alpine

ENV LANG=C.UTF-8

# Install glibc
RUN apk -v --update add ca-certificates && \
    rm -rf /tmp/*.apk

ADD lc-blueprint-addon-*.jar /local/lc-blueprint-addon/bin/lc-blueprint-addon.jar

ENTRYPOINT ["java", "-jar", "/local/lc-blueprint-addon/bin/lc-blueprint-addon.jar"]

EXPOSE 5000 25000