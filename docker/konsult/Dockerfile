FROM openjdk:11.0.9.1-jre-buster
USER root
MAINTAINER rudi@rennes-metropole.fr
ENV TZ=Europe/Paris

RUN mkdir -p /etc/rudi/config
RUN chmod 777 -R /etc/rudi/config && chmod o+t -R /etc/rudi/config

RUN mkdir -p /opt/rudi/data

RUN mkdir /tmp/jetty
RUN chmod 777 -R /tmp && chmod o+t -R /tmp

EXPOSE 8087

ADD rudi-microservice-konsult-facade.jar /opt/rudi/microservice.jar

CMD [ "sh", "-c", "exec java -Djava.io.tmpdir=/tmp/jetty \
      -Drudi.datadir=/etc/rudi/data/konsult -Drudi.config=/etc/rudi/config \
      ${ADD_JAVA_OPTS}                                   \
      -Xmx${XMX:-1G} -Xms${XMX:-1G}                      \
      -jar /opt/rudi/microservice.jar" ]