## RUDI - FACET - KACCESS
Cette facette permet d'offrir un mécanisme d'accès au méta-catalogue.

L'ajout de cette facette dans un µservice peut nécessiter la configuration des propriétés suivantes :

* _dataverse.api.token_
* _dataverse.api.url_
* _dataverse.api.root.alias_
* _dataverse.api.rudi.alias_
* _dataverse.api.archive.alias_

### Dataverse rudi

Sur le serveur dataverse, on a créé un dataverse  __RUDI Root__  qui contient 3 dataverses "fils" :
* **RUDI Data** : contient les metadata des jeux de données
* **RUDI Archive** : contient les metadata des jeux de données archivés
* **RUDI Test** : utilisé par les tests unitaires uniquement

La structure du dataverse est définie uniquement au niveau de RUDI Root. Les dataverses fils héritent automatiquement de sa configuration.

#### Metadata
Les données exposées par kaccess sont définies dans le fichier  __rudi-kaccess-model.yml__

Cette API définit entre autres l'objet  __Metadata__ , qui correspond à un jeu de données (JDD) de RUDI.
C'est cet objet qui est stocké dans le dataverse RUDI.

Le Metadata de kaccess doit être identique au Metadata défini dans l'API RUDI-PRODUCER de l'IRISA (pour une même version de l'API) : [RUDI-PRODUCER](https://app.swaggerhub.com/apis/OlivierMartineau/RUDI-PRODUCER)

#### Champs du dataverse RUDI
Le fichier qui définit les champs du dataverse rudi est  __rudi.xlsx__

Ce fichier sert de référence pour produire le fichier  __rudi.tsv__  qui correspond au format utilisé pour importer les champs d'un dataverse dans le serveur dataverse.
Ce fichier est généré en faisant un Export du fichier rudi.xlsx au format Texte, avec le séparateur tabulation.

Le nom des champs de rudi.tsv est repris dans le code java dans le fichier  `RudiMetadataField`.

Le nom d'un champ doit être unique dans le dataverse. Pour cette raison, chaque champ du dataverse rudi est préfixé par  __"rudi_"__

La gestion des champs du dataverse est documentée ici : [metadatacustomization](https://guides.dataverse.org/en/latest/admin/metadatacustomization.html) 

### Limitations du dataverse / solutions de contournement 
__Gestion des dates__

Les dates d'un Metadata sont des LocalDateTime. Le type date du dataverse est une date du jour, sans la partie hh/mm/ss.
Pour palier à cette limitation les dates du metadata sont stockées dans le dataverse dans le type float, qui correspond à la conversion d'une date en long, sans les nanosecondes. 

__Objets imbriqués__
 
Dans le dataverse, il est possible de définir un objet qui contient des propriétés. Cela fonctionne bien pour 1 seul niveau d'imbrication (l'IHM du dataverse n'affiche pas les objets contenus dans des objets. 
De plus le dataverse ne sait pas gérer une propriété de type liste dans un sous-objet).

A cause de cette limitation, certains objets de Metadata ont été "mis à plat" dans les champs du dataverse RUDI (ex de Mediafile et de Mediaseries dans Media).

__Propriété geographic_distribution__

Dans l'API RUDI-PRODUCER de l'IRISA, l'objet Metadata contient une propriété geographic_distribution de type GeoJsonObject.
Cet objet GeoJsonObject n'a pas été modélisé dans rudi ni dans le dataverse.   
Dans le Metadata de rudi, la propriété geographic_distribution est un string. Une conversion GeoJsonObject -> string devra donc être faite avant d'insérer le jeu de données.

### Documentation
Pour interroger le dataverse, kaccess utilise l'API du dataverse : [API guide](https://guides.dataverse.org/en/5.1/api/index.html)

La recherche dans le dataverse utilise SOLR : [SOLR v 8.5](https://solr.apache.org/guide/8_5/searching.html)

Code source du dataverse : [dataverse v 5.1.1](https://github.com/IQSS/dataverse/releases/tag/v5.1.1)
