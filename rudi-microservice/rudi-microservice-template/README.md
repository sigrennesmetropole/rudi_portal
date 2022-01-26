Suite à la création d'un nouveau microservice, mettre à jour les fichiers suivants :

- .gitlab-ci.yml
- ansible/roles/rudi/tasks/main.yml
- ansible/roles/rudi/templates/docker-compose.yml.j2
- ansible/roles/rudi/templates/gateway/gateway.yml.j2
- ansible/roles/rudi/templates/rudi.conf.j2
- ansible/roles/rudi/templates/rudi_ssl.conf.j2
- ansible/roles/rudi/vars/main.yml
- docker/copy-rudi.sh
- docker/config/gateway/gateway.yml
- docker/docker-compose.yml
- rudi-microservice/rudi-microservice-gateway/rudi-microservice-gateway-facade/src/main/resources/gateway/gateway.yml

Créer également ces fichiers :

- ansible/roles/rudi/templates/<microservice>/<microservice>.properties.j2 (très proche du fichier
  docker/config/<microservice>/<microservice>.properties)
- docker/<microservice>/Dockerfile
- docker/config/<microservice>/<microservice>.properties
