FROM amazoncorretto:11-alpine-jdk
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILE=api/build/libs/*.jar
ARG DEPENDENCY=api/build/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib         /app/lib
COPY ${DEPENDENCY}/META-INF             /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes     /app
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-cp", "app:app/lib/*", "/app.jar"]
