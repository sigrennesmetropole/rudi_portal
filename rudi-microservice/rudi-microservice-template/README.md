Suite à la création d'un nouveau microservice, mettre à jour les fichiers suivants :

- [ ] ansible/roles/rudi/tasks/main.yml
- [ ] ansible/roles/rudi/templates/docker-compose.yml.j2
- [ ] ansible/roles/rudi/templates/gateway/gateway.yml.j2
- [ ] ansible/roles/apache/templates/rudi.conf.j2
- [ ] ansible/roles/apache/templates/rudi_ssl.conf.j2
- [ ] ansible/roles/rudi/vars/main.yml
- [ ] docker/copy-rudi.sh
- [ ] docker/config/gateway/gateway.yml
- [ ] docker/docker-compose.yml
- [ ] rudi-microservice/pom.xml
- [ ] rudi-microservice/rudi-microservice-gateway/rudi-microservice-gateway-facade/src/main/resources/gateway/gateway.yml

Créer également ces fichiers :

- [ ] .run/<microservice>.run.xml
- [ ] ansible/roles/rudi/templates/<microservice>/<microservice>.properties.j2 (très proche du fichier
  docker/config/<microservice>/<microservice>.properties)
- [ ] docker/<microservice>/Dockerfile
- [ ] docker/config/<microservice>/<microservice>.properties
- [ ] rudi-microservice/rudi-microservice-<microservice>/rudi-microservice-<microservice>-facade/src/main/*/*Controller.java : ne pas oublier d'ajouter les @PreAuthorize
- [ ] rudi-microservice/rudi-microservice-acl/rudi-microservice-acl-service/src/main/resources/bdd/V*__microservice_<microservice>.sql avec :
  - [ ] un `user` dans `acl_data` avec les champs suivants :
    - uuid : Un UUID préalablement généré par un outil externe
    - company : `RUDI`
    - login = module.oauth2.client-id
    - firstname = login
    - lastname = login
    - password = module.oauth2.client-secret chiffré (cf doc du champ password dans les fichiers *.yml du dossier ansible/vars)
  - [ ] Différents `role` dans `acl_data` en fonction des besoins. Au moins :
    - `MODULE_<Le nom du microservice en majuscule>`
    - `MODULE_<Le nom du microservice en majuscule>_ADMINISTRATOR`
  - [ ] Les liens nécessaires `user_role` dans `acl_data` pour chaque lien `role` <-> `user`. Pour faire des appels intermicroservices, au minimum le `user` du microservice doit avoir le rôle :
    - `MODULE`
