## RUDI - FACET - DATAVERSE
Cette facette permet d'offrir une API d'accès au dataverse, utilisée par les facettes spécialisées de chaque dataverse (kaccess, kmedia).
Elle définit également les classes et méthodes communes aux facets kaccess et kmedia.

#### DONNEES
La facette dataverse modélise les données qui sont manipulées par l'API du dataverse, comme par exemple Dataset et DatasetFile. 

Ces données sont définies dans le fichier  __rudi-dataverse-model.yml__

### Documentation
Pour interroger le dataverse, kaccess utilise l'API du dataverse : [API guide v5.1](https://guides.dataverse.org/en/5.1/api/index.html)

La gestion des champs du dataverse est documentée ici : [metadatacustomization](https://guides.dataverse.org/en/5.1/admin/metadatacustomization.html) 

La recherche dans le dataverse utilise SOLR : [SOLR v 8.5](https://solr.apache.org/guide/8_5/searching.html)

Code source du dataverse : [dataverse v 5.1.1](https://github.com/IQSS/dataverse/releases/tag/v5.1.1)
