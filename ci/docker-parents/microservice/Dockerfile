# syntax=docker/dockerfile:1
FROM openjdk:11.0.9.1-jre-buster
USER root
MAINTAINER rudi@rennes-metropole.fr
ENV TZ=Europe/Paris

RUN mkdir -p /etc/rudi/config
RUN chmod 777 -R /etc/rudi/config && chmod o+t -R /etc/rudi/config

RUN mkdir -p /opt/rudi/data

RUN mkdir /tmp/jetty
RUN chmod 777 -R /tmp && chmod o+t -R /tmp

# ansible/roles/rudi/tasks/tool.yml : {{tool}} : Copy id_rsa
ADD id_rsa /etc/rudi/config/key/id_rsa

# ansible/roles/rudi/tasks/tool.yml : {{tool}} : Copy id_rsa.pub
ADD id_rsa.pub /etc/rudi/config/key/id_rsa.pub

# ansible/roles/rudi/tasks/microservice.yml : {{microservice}} : Copy log4j2.xml
ADD log4j2.xml /etc/rudi/config/microservice/log4j2.xml

# ansible/roles/rudi/tasks/microservice.yml : {{microservice}} : Copy *.properties
# Ajouté par un configmap Kubernetes

CMD [ "sh", "-c", "exec java -Djava.io.tmpdir=/tmp/jetty \
      -Drudi.datadir=/etc/rudi/data/microservice \
      -Dlogging.config=/etc/rudi/config/microservice/log4j2.xml \
      -Dspring.config.additional-location=file:/etc/rudi/config/microservice/ \
      ${ADD_JAVA_OPTS} \
      -Xmx${XMX:-1G} -Xms${XMX:-1G} \
      -jar /opt/rudi/microservice.jar" ]
