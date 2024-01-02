FROM maven:3.8.5-openjdk-17-slim AS war-builder

WORKDIR /app
COPY src/ src/
COPY pom.xml .
RUN ["mvn", "package"]

FROM tomcat:jre17 AS application

COPY ssl/ /ssl/
COPY build/ /usr/local/tomcat/conf
COPY --from=war-builder /app/wars/ /usr/local/tomcat/webapps/

EXPOSE 8080
