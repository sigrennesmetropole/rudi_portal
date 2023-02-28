Facet de gestion de documents.

# Mise en place

## Facade

Ajouter le package `org.rudi.facet.crypto` dans la *AppFacadeApplication* du microservice.

Exemple
avec [la facade de selfdata](../../rudi-microservice/rudi-microservice-selfdata/rudi-microservice-selfdata-facade/src/main/java/org/rudi/microservice/selfdata/facade/AppFacadeApplication.java)
.

## Contrôleur

Ajouter un endpoint sur un contrôleur pour uploader le document et un autre pour le télécharger.

Exemple de contrôleur :
[AttachmentsController](../../rudi-microservice/rudi-microservice-selfdata/rudi-microservice-selfdata-facade/src/main/java/org/rudi/microservice/selfdata/facade/controller/AttachmentsController.java)
dans le microservice selfdata, méthode `uploadAttachment`.

Spécifier la taille maximum des fichiers uploadés dans les properties Spring.

Exemple
avec [selfdata-common.properties](../../rudi-microservice/rudi-microservice-selfdata/rudi-microservice-selfdata-facade/src/main/resources/selfdata/selfdata-common.properties) :

```properties
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

## Base de données

Cette facet manipule des entités JPA.

Modifier le DatabaseConfiguration du microservice pour ajouter le package `org.rudi.facet.doks` dans les
annotations `@EntityScan` et `@EnableJpaRepositories`.

Exemple : [SelfdataDatabaseConfiguration](../../rudi-microservice/rudi-microservice-selfdata/rudi-microservice-selfdata-service/src/main/java/org/rudi/microservice/selfdata/service/config/SelfdataDatabaseConfiguration.java)
.

Indiquer le schéma utilisé dans la propriété `spring.jpa.properties.hibernate.default_schema`. Exemple :

```properties
spring.jpa.properties.hibernate.default_schema=selfdata_data
```
