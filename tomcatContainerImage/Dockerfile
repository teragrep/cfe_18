FROM rockylinux:9
# Make it uptodate
RUN dnf -y update
# Install wget for downloading tomcat package
RUN dnf install wget -y && dnf clean all
# Rocky linux 9 does not come with java at all. Thats why java-11 install
RUN dnf install java-11-openjdk-headless -y && dnf clean all
# Tomcat user
RUN useradd -r -m -U -d /opt/tomcat -s /bin/false tomcat

ENV TOMCAT_MAJOR 9
ENV TOMCAT_VERSION 9.0.98
ENV CATALINA_HOME /opt/tomcat/apache-tomcat-$TOMCAT_VERSION/
RUN mkdir -p "$CATALINA_HOME"
WORKDIR $CATALINA_HOME

# WGET
RUN wget -c https://dlcdn.apache.org/tomcat/tomcat-$TOMCAT_MAJOR/v$TOMCAT_VERSION/bin/apache-tomcat-$TOMCAT_VERSION.tar.gz

# install tomcat
RUN tar xf apache-tomcat-$TOMCAT_VERSION.tar.gz -C /opt/tomcat

# Tomcat ownership
RUN chown -R tomcat: /opt/tomcat/*

# Make files executable
RUN sh -c 'chmod +x /opt/tomcat/apache-tomcat-'$TOMCAT_VERSION'/bin/*.sh'

EXPOSE 8080
# Start with Catalina
CMD ["bin/catalina.sh","run"]
