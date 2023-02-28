Doc de Dataverse : <https://guides.dataverse.org/en/5.1/admin/metadatacustomization.html>.

# Ajout de champs Dataverse

1. On génére le fichier [rudi.tsv](rudi.tsv) avec maven
2. On uploade ensuite le fichier sur Dataverse.
3. On propage les modifications pour Ansible

# Génération du fichier rudi.tsv par maven

Lancer le Run Configuration "Mettre à jour rudi.tsv" qui lance le main de la
classe [RudiTsvUpdator](../../java/org/rudi/facet/kaccess/helper/tsv/RudiTsvUpdator.java).

# Upload du fichier sur Dataverse

On utilise le script [upload_rudi_tsv.sh](upload_rudi_tsv.sh).

On fait ensuite un RELOAD Solr en appelant ce script sur la machine hébergeant Dataverse (à adapter en fonction de l'instance) : 

```
sudo /opt/dataverse/images/dataverse/dvinstall/updateSchemaMDB.sh -d http://localhost:8095 -t /opt/dataverse/v5.4.1-rudi/instance1/solr-data/collection1/conf/
```

En fonction du nombre de JDD, cette opération peut prendre du temps.

Si la commande ne s'arrête plus, on peut redémarrer SolR (ou même tous les services Dataverse).

Suite à cela il faut vérifier si le fichier [schema_dv_mdb_fields.xml](../../../../../../ansible/roles/dataverse/files/solr-data/collection1/conf/schema_dv_mdb_fields.xml) est toujours synchro avec celui stocké sur le serveur.
On peut pour cela lancer le rôle dataverse via Ansible en mode `--check` pour voir les ajustements nécessaires.

# Vérifications

Pour vérifier la configuration d'un champ dans Dataverse, on peut appeler directement son API.

Exemple pour le champ `rudi_media_type` :

```
http://dv.open-dev.com:8095/api/admin/datasetfield/rudi_media_type
```

On peut aussi vérifier que le catalogue et le détail s'affichent toujours bien.

# Propagation des modifications pour Ansible

L'ajout de champs modifie le fichier schema_dv_mdb_fields.xml de l'instance Dataverse.
À chaque déploiement Ansible, ce fichier est écrasé.
Il faut donc récupérer ce fichier depuis la machine et mettre à jour celui utilisé par Ansible :
[schema_dv_mdb_fields.xml](../../../../../../ansible/roles/dataverse/files/solr-data/collection1/conf/schema_dv_mdb_fields.xml)

# Instances Dataverse

Sur la dev il existe une deuxième instance de Dataverse sur laquelle on peut faire des tests
(si on ne souhaite pas perturber toute l'équipe de dev, par exemple). Dans ce cas pour utiliser cette deuxième instance,
il suffit de remplacer dans les fichiers `*-dev.properties` toutes les occurrences de :

```
${dataverse.instance1.port}
```

Par :

```
${dataverse.instance2.port}
```

# Annulation de modifications

Certaines modifications ne peuvent pas être effectuées via l'API. Pour cela on doit se connecter directement sur la base
SQL de Dataverse.

## Passage du type CONTROLLEDVOCABULARY à PRIMITIVE

Exemple avec un champ portant l'id `225` dans la table `datasetfieldtype` :

```postgresql
-- On supprime le contrôle des valeurs pour le champ
UPDATE datasetfieldtype
SET allowcontrolledvocabulary = false
WHERE id = 225;
-- On supprime les valeurs autorisées pour le champ
DELETE
FROM controlledvocabularyvalue
WHERE datasetfieldtype_id = 225;
```

[RudiMetadataField]: ../../../main/java/org/rudi/facet/kaccess/constant/RudiMetadataField.java


# Liens complémentaires

[Documentation dataverse pour le réindexation](https://guides.dataverse.org/en/latest/admin/solr-search-index.html)