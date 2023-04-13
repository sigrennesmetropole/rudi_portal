Suite à la création d'un nouveau microservice, mettre à jour les fichiers suivants :

- [ ] ansible/roles/rudi/tasks/main.yml
- [ ] ansible/roles/rudi/templates/docker-compose.yml.j2
- [ ] ansible/roles/rudi/templates/gateway/gateway.yml.j2
- [ ] ansible/roles/apache/templates/rudi.conf.j2
- [ ] ansible/roles/apache/templates/rudi_ssl.conf.j2
- [ ] ansible/roles/rudi/vars/main.yml
- [ ] ci/docker/gateway/gateway.yml
- [ ] rudi-application/rudi-application-front-office/angular-project/package.json
- [ ] rudi-common/rudi-common-core/src/main/java/org/rudi/common/core/security/QuotedRoleCodes.java
- [ ] rudi-common/rudi-common-core/src/main/java/org/rudi/common/core/security/Role.java
- [ ] rudi-common/rudi-common-core/src/main/java/org/rudi/common/core/security/RoleCodes.java
- [ ] rudi-microservice/pom.xml
- [ ] 
  rudi-microservice/rudi-microservice-gateway/rudi-microservice-gateway-facade/src/main/resources/gateway/gateway.yml
  - Rajouter la ligne permettant de générer les fichiers swagger
  - generate:[MICROSERVICE]-api
  - generate:[MICROSERVICE]-model
  - Ajouter les 2 lignes dans l'instruction generate:all
- [ ] .gitignore : ajouter les lignes suivantes:
  - [ ] `/rudi-application/rudi-application-front-office/angular-project/src/app/<microservice>/<microservice>-api`
  - [ ] `/rudi-application/rudi-application-front-office/angular-project/src/app/<microservice>/<microservice>-model`

Créer également ces fichiers :

- [ ] .run/<microservice>.run.xml
- [ ] ansible/roles/rudi/templates/<microservice>/<microservice>.properties.j2 (très proche du fichier
  docker/config/<microservice>/<microservice>.properties)
- [ ] ci/docker/<microservice>/Dockerfile
- [ ] ci/karbon/apps/rudi-<microservice>/conf-env/<microservice>-dev-karbon.properties
- [ ] ci/karbon/apps/rudi-<microservice>/rudi-<microservice>.yml
- [ ] ci/karbon/apps/rudi-<microservice>/rudi-<microservice>-${CI_ENVIRONMENT_NAME}.deployment.yml
- [ ] rudi-microservice/rudi-microservice-<microservice>/rudi-microservice-<microservice>-facade/src/main/*/*Controller.java : ne pas oublier d'ajouter les @PreAuthorize
- [ ] rudi-microservice/rudi-microservice-acl/rudi-microservice-acl-service/src/main/resources/bdd/V*__microservice_<microservice>.sql avec :
  - [ ] un `user` dans `acl_data` avec les champs suivants :
    - uuid : Un UUID préalablement généré par un outil externe
    - company : `rudi`
    - login = module.oauth2.client-id
    - firstname = login
    - lastname = login
    - password = module.oauth2.client-secret chiffré (cf doc du champ password dans les fichiers *.yml du dossier ansible/vars)
  - [ ] Différents `role` dans `acl_data` en fonction des besoins. Au moins :
    - `MODULE_<Le nom du microservice en majuscule>`
    - `MODULE_<Le nom du microservice en majuscule>_ADMINISTRATOR`
  - [ ] Les liens nécessaires `user_role` dans `acl_data` pour chaque lien `role` <-> `user`. Pour faire des appels intermicroservices, au minimum le `user` du microservice doit avoir le rôle :
    - `MODULE`
