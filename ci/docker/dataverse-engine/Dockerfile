FROM payara/server-full:5.2021.4-jdk11
USER root
RUN apt update
RUN apt install -y jq curl python-pip postgresql-client
RUN pip install psycopg2-binary


# Copy stuff
RUN mkdir -p /opt/dv/dvinstall
RUN mkdir -p /data

#Ccopy properties
COPY lang.properties /opt/dv
COPY lang.templates /opt/dv
COPY dataverse-property-files /opt/dv/dataverse-property-files
COPY dvinstall /opt/dv/dvinstall
# Copy file data
COPY dvinstall/file-data /data

COPY dataverse_entrypoint.sh /opt/payara/scripts
COPY dvinstall/post-boot-commands.asadmin /opt/payara/config
COPY dvinstall/pre-boot-commands.asadmin /opt/payara/config
COPY dvinstall/jhoveConfig.xsd /opt/payara/appserver/glassfish/domains/domain1/config/jhoveConfig.xsd
COPY dvinstall/jhove.conf /opt/payara/appserver/glassfish/domains/domain1/config/jhove.conf
COPY postgresql-42.2.19.jar /opt/payara/appserver/glassfish/lib/postgresql-42.2.19.jar

# glassfish port
EXPOSE 8080
EXPOSE 4848
EXPOSE 8009
EXPOSE 8181
#VOLUME /opt/glassfish4/glassfish/domains/domain1/docroot

# Owner
RUN chown -R payara:payara /opt/dv
RUN chmod -R a+rw /opt/dv

RUN chown -R payara:payara /data
RUN chmod -R a+rwx /data

RUN chown payara:payara /opt/payara/scripts/dataverse_entrypoint.sh
RUN chmod a+x /opt/payara/scripts/dataverse_entrypoint.sh

# Ready !
USER payara
WORKDIR /opt/payara
RUN echo "Installing and running Dataverse..."
CMD ["/opt/payara/scripts/dataverse_entrypoint.sh"]
