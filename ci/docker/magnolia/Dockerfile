FROM tomcat:9-jdk11-temurin

RUN mkdir -p /etc/magnolia/config/default /etc/magnolia/config/repo-conf /etc/magnolia/config/shared
RUN mkdir -p /opt/magnolia/data/deploy /opt/magnolia/data/repository /opt/magnolia/data/repositories/magnolia /opt/magnolia/data/workspaces  
RUN mkdir -p /opt/magnolia/modules

RUN chmod 777 -R /etc/magnolia/config && chmod o+t -R /etc/magnolia/config
RUN chmod 777 -R /opt/magnolia/data && chmod o+t -R /opt/magnolia/data
RUN chmod 777 -R /opt/magnolia/modules && chmod o+t -R /opt/magnolia/modules

#COPY magnolia-community-webapp-6.2.40.war /usr/local/tomcat/webapps/ROOT.war
COPY ROOT-6.2.40.tgz /tmp/ROOT.tgz
RUN mkdir -p /usr/local/tomcat/webapps/ROOT
WORKDIR /usr/local/tomcat/webapps/ROOT
RUN tar -zxvf /tmp/ROOT.tgz .
WORKDIR /usr/local/tomcat/
COPY postgresql-42.2.18.jar /usr/local/tomcat/lib/postgresql-42.2.18.jar
#COPY web.xml /usr/local/tomcat/webapps/ROOT/WEB-INF/web.xml
#COPY jackrabbit-bundle-postgres-search.xml /usr/local/tomcat/webapps/ROOT/WEB-INF/config/repo-conf

COPY server.xml /usr/local/tomcat/conf/server.xml

ENV CATALINA_OPTS="-Xmx2048m -Dmagnolia.update.auto=true"
