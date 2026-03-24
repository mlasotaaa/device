FROM amazoncorretto:21
EXPOSE 8080

WORKDIR /app
COPY target/*.jar /app/app.jar

RUN mkdir -p /device-data/logs/dumps /device-data/logs/gc \
    && chown -R 1000:1000 /app /device-data

USER 1000:1000

ENV JDK_JAVA_OPTIONS="-XX:+UseG1GC \
 -XX:MaxRAMPercentage=70 \
 -XX:+UseStringDeduplication \
 -XX:+HeapDumpOnOutOfMemoryError \
 -XX:HeapDumpPath=/device-data/logs/dumps \
 -Xlog:gc*:file=/device-data/logs/gc.log:uptime,level,tags:filecount=5,filesize=10M"

ENTRYPOINT ["java", "-jar", "/app/app.jar"]