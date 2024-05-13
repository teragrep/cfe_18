# Tomcat image
FROM tomcat:8-jre8-temurin-focal

COPY target/cfe_18.war ${CATALINA_HOME}/webapps/

EXPOSE 8080

CMD ["catalina.sh", "run"]




