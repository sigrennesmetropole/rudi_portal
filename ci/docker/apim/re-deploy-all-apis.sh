#!/bin/bash

# Script pour retrouver toutes les API (médias) introuvables par API REST dans WSO2 (erreur HTTP 404 NOT_FOUND)
# Pour faire revenir une API, on change son état temporairement, pour déclencher un rafraichissement côté WSO2
# Cf détails : https://jira.open-groupe.com/browse/RUDI-1938

# Récup d'une page d'API pour connaitre la pagination à suivre
apis=$(curl -u "admin:admin" --insecure "https://localhost:9443/api/am/publisher/v1/apis")
limit=$(jq -e '.pagination.limit' <<<"$apis")
total=$(jq -e '.pagination.total' <<<"$apis")

# @param $1 : Action à réaliser
# @param $2 : ID de l'API concernée par l'action
change-lifecycle() {
  curl -u "admin:admin" --insecure --request POST "https://localhost:9443/api/am/publisher/v1/apis/change-lifecycle?action=$1&apiId=$2" -o /dev/null -w "HTTP Status : %{http_code}\n"
}

# Pour chaque page
for ((offset = 0; offset < total; offset = offset + limit)); do
  echo -e "\e[32mFetching\e[39m API list with offset=$offset and limit=$limit... "
  apis=$(curl -u "admin:admin" --insecure "https://localhost:9443/api/am/publisher/v1/apis?offset=$offset")
  count=$(jq -e '.count' <<<"$apis")

  # Traitement de chaque api de la page
  for ((i = 0; i < count; i++)); do
    apiId=$(jq -e ".list[$i].id" --raw-output <<<"$apis")
    apiNumber=$((offset + i + 1))
    echo -e "\e[34mRedeploying\e[39m API $apiId $apiNumber/$total... "

    change-lifecycle "Publish" "$apiId" # Le bouton s'appelle Redeploy dans WSO2 mais déclenche l'action "Publish" côté API
  done

done
