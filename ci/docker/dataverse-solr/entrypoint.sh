#!/bin/bash
# Fail on any error
set -e

if [ -z "$DATAVERSE_HOST" ]; then
    echo "DATAVERSE_HOST not set defaulted to \"dataverse\""
    DATAVERSE_HOST=dataverse
fi
if [ -z "$DATAVERSE_PORT" ]; then
    echo "DATAVERSE_PORT not set defaulted to \"8080\""
    DATAVERSE_PORT=8080
fi
DATAVERSE_URL="http://$DATAVERSE_HOST:$DATAVERSE_PORT"
echo "Dataverse URL: $DATAVERSE_URL"

SOLR_URL=${SOLR_URL:-"http://localhost:$SOLR_PORT"}

solr start -v
echo "Starting Solr...."
# if [ -f /tmp/updateSchemaMDB.sh ]; then
if [ -f /tmp/updateSchemaMDB.sh ] && [ ! -d /var/solr/data/collection1 ]; then
    sleep 25;
    solr stop -v
    unzip -o -qq /tmp/collection1.zip -d /var/solr/data/
    solr start -v

    # Méthode de génération des fichiers XML via Dataverse (fonctionnement par défaut)
    if [ "$SKIP_UPDATE_SCHEMA_MDB" != "true" ]; then
      echo "Checking Dataverse....";
      sleep 10;
      until curl -sS -f "$DATAVERSE_URL/robots.txt" -m 2 2>&1 > /dev/null;
      do echo ">>>>>>>> Waiting for Dataverse...."; echo "---- Dataverse is not ready...."; sleep 20; done;
      echo "Dataverse is running...";
      echo "Trying to update scheme from Dataverse...";
      echo "Updating";
      /tmp/updateSchemaMDB.sh -d $DATAVERSE_URL -t /var/solr/data/collection1/conf
      sleep 5;
      echo "-----Scheme updated------";

    # Méthode de reprise des fichiers XML à partir de la ConfigMap Karbon
    else

      echo "Waiting ${WAIT_SOLR}s....";
      sleep "$WAIT_SOLR";

      echo "Trying to update scheme from Karbon ConfigMap...";
      echo "Updating";
      cp -r /etc/solr/karbon-conf/* /var/solr/data/collection1/conf

      # Rechargement SOLR
      echo "Triggering Solr RELOAD at ${SOLR_URL}/solr/admin/cores?action=RELOAD&core=collection1"
      curl -f -sS "${SOLR_URL}/solr/admin/cores?action=RELOAD&core=collection1"

      sleep 5;
      echo "-----Scheme updated------";
    fi

    rm /tmp/updateSchemaMDB.sh
else
    echo ":) :) :)"
fi

tail -f /dev/null

