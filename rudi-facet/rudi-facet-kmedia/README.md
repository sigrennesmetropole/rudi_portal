## RUDI - FACET - KMEDIA

Cette facette permet d'offrir un mécanisme d'accès au dataverse RUDI Media.

L'ajout de cette facette dans un µservice peut nécessiter la configuration des propriétés suivantes :

* _dataverse.api.token_
* _dataverse.api.url_
* _dataverse.api.rudi.media.data.alias_

### Dataverse rudi

Sur le serveur dataverse, on a créé un dataverse  __RUDI Media Root__  qui contient 2 dataverses "fils" :

* **RUDI Media Data** : contient les metadata des jeux de données
* **RUDI Media Test** : utilisé par les tests unitaires uniquement

La structure du dataverse est définie uniquement au niveau de RUDI Media Root. Les dataverses fils héritent
automatiquement de sa configuration.

#### Media

Les données exposées par kaccess sont définies dans le fichier  __rudi-kmedia-model.yml__

Cette API définit l'objet  __Media__ , qui correspond à un media de RUDI.
C'est cet objet qui est stocké dans le dataverse RUDI Media.

#### Champs du dataverse RUDI Media

On utilise les champs suivants du bloc citation qui est le bloc fourni par défaut par le dataverse :

* author identifier : uuid producteur/fournisseur
* author affiliation : le type de ressource (producteur ou fournisseur)
* author name  : à titre indicatif et pour faciliter le suivi (recherche via l'IHM du dataverse) il pourrait contenir le
  nom du fournisseur/producteur
* kind of data : le type de document stocké (LOGO)
* title : provider/producer + "nom du producteur/fournisseur"
* description : logo de + "nom du producteur/fournisseur"

#### Examples de commandes

* Chargement des informations d'un dataset en fonction de son persistentId :

curl -H "X-Dataverse-key:
{{dataverse_token_api}}" http://dv.open-dev.com:8095/api/datasets/:persistentId?persistentId=doi:10.5072/FK2/EDIQSA

* Téléchargement d'un fichier à partir de son identifiant, que l'on trouve dans le dataset (
  data.latestVersion.files.dataFile.id) :

curl -O -J -H "X-Dataverse-key:{{dataverse_token_api}}" http://dv.open-dev.com:8095/api/access/datafile/581

### Documentation

Pour interroger le dataverse, kaccess utilise l'API du
dataverse : [API guide](https://guides.dataverse.org/en/5.1/api/index.html)

La recherche dans le dataverse utilise SOLR : [SOLR v 8.5](https://solr.apache.org/guide/8_5/searching.html)

Code source du dataverse : [dataverse v 5.1.1](https://github.com/IQSS/dataverse/releases/tag/v5.1.1)
