FROM library/postgres:10.13
USER root

COPY dataverse-dump.sql /docker-entrypoint-initdb.d

EXPOSE 5432

RUN chmod a+wr /docker-entrypoint-initdb.d/dataverse-dump.sql

RUN echo "Installing and running Postgres for dataverse..."
#CMD ["/docker-entrypoint.sh"]
