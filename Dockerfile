# STAGE 0
FROM rockylinux:9 as BASE

# Rocky linux 9 does not come with java at all. Wget for downloading package
RUN dnf update -y && dnf install wget java-11-openjdk-headless -y && dnf clean all
# Tomcat user
RUN useradd -r -m -U -d /opt/tomcat -s /bin/false tomcat

ENV TOMCAT_MAJOR 9
ENV TOMCAT_VERSION 9.0.98
ENV CATALINA_HOME /opt/tomcat/apache-tomcat-${TOMCAT_VERSION}/
RUN mkdir -p "${CATALINA_HOME}"
WORKDIR ${CATALINA_HOME}

# WGET and verify
RUN wget -cq https://downloads.apache.org/tomcat/tomcat-${TOMCAT_MAJOR}/v${TOMCAT_VERSION}/KEYS
RUN gpg2 --import KEYS
RUN wget -cq https://downloads.apache.org/tomcat/tomcat-${TOMCAT_MAJOR}/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz
RUN wget -cq https://downloads.apache.org/tomcat/tomcat-${TOMCAT_MAJOR}/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz.asc
RUN gpg2 --verify ${CATALINA_HOME}/apache-tomcat-${TOMCAT_VERSION}.tar.gz.asc ${CATALINA_HOME}/apache-tomcat-${TOMCAT_VERSION}.tar.gz

# install tomcat
RUN tar xf apache-tomcat-${TOMCAT_VERSION}.tar.gz -C /opt/tomcat

# Tomcat ownership
RUN chown -R tomcat: /opt/tomcat/*

# STAGE 1
# Tomcat image
FROM BASE

COPY target/cfe_18.war ${CATALINA_HOME}/webapps/

EXPOSE 8080

CMD ["bin/catalina.sh", "run"]
