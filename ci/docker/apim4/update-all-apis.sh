#!/bin/bash

# Script pour retrouver toutes les API (médias) introuvables par API REST dans WSO2 (erreur HTTP 404 NOT_FOUND)
# Pour faire revenir une API, on fait un update sans rien modifier, pour déclencher un rafraichissement côté WSO2
# Cf détails : https://jira.open-groupe.com/browse/RUDI-1517

APIM_HOST=localhost
APIM_PORT=9443
APIM_PASSWORD=admin

mkdir --parents apis-backup

# Récup d'une page d'API pour connaitre la pagination à suivre
apis=$(curl -u "admin:${APIM_PASSWORD}" --insecure "https://${APIM_HOST}:${APIM_PORT}/api/am/publisher/v4/apis")
limit=$(jq -e '.pagination.limit' <<<"$apis")
total=$(jq -e '.pagination.total' <<<"$apis")

# Pour chaque page
for ((offset = 0; offset < total; offset = offset + limit)); do
  echo -e "\e[32mFetching\e[39m API list with offset=$offset and limit=$limit... "
  apis=$(curl -u "admin:${APIM_PASSWORD}" --insecure "https://${APIM_HOST}:${APIM_PORT}/api/am/publisher/v4/apis?offset=$offset")
  count=$(jq -e '.count' <<<"$apis")

  # Traitement de chaque api de la page
  for ((i = 0; i < count; i++)); do
    apiId=$(jq -e ".list[$i].id" --raw-output <<<"$apis")
    apiNumber=$((offset + i + 1))
    echo -e "\e[34mUpdating\e[39m API $apiId $apiNumber/$total... "
    apiDetails=$(curl -u "admin:${APIM_PASSWORD}" -H "Content-Type: application/json" --insecure "https://${APIM_HOST}:${APIM_PORT}/api/am/publisher/v4/apis/$apiId")
    echo "$apiDetails" >"apis-backup/$apiId.json"
    curl -u "admin:${APIM_PASSWORD}" -H "Content-Type: application/json" --insecure --request PUT "https://${APIM_HOST}:${APIM_PORT}/api/am/publisher/v4/apis/$apiId" --data-raw "$apiDetails" -o /dev/null -w "%{http_code}"
  done

done
