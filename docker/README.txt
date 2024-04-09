
Containerized deployment for CFE-18. Requires external database to use. It uses public tomcat docker image (tomcat:8-jre8-temurin-focal)

How to use?

1. Install Docker
2. Fork cfe-18
3. "docker" directory contains external configuration for modifying deployment
4. "docker" directory is also where war file needs to be inserted for deployment
5. Move to docker directory
6. docker build -t cfe_18_containerized .
7. docker run -ti --privileged  -v ~/git/cfe_18/docker/application.properties:/etc/tomcat/Catalina/localhost/cfe-18/application.properties -v ~/git/cfe_18/docker/log4j2.xml:/etc/tomcat/Catalina/localhost/cfe-18/log4j2.xml  -p 8080:8080 cfe_18_containerized
    7.a --privileged is for SELinux
    7.b 8080:8080 is for external and internal ports
    7.c cfe_18_containerized is image name

