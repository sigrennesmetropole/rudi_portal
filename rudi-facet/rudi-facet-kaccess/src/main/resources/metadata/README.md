Doc de Dataverse : <https://guides.dataverse.org/en/5.1/admin/metadatacustomization.html>.

# Ajout de champs Dataverse

On ouvre le fichier Excel `rudi.xlsx`.

On ajoute chaque nouvelle propriété dans la section `#datasetField`. Le remplissage de certaines colonnes est décrit
ci-après.

Une fois les champs ajoutés on [exporte](#export-au-format-tsv-avec-excel) le tableau au format tsv.

On uploade ensuite le fichier sur Dataverse.

## Remplissages des colonnes

### name

Nom complet du champ envoyé à RUDI (cf [RudiMetadataField]).

### description

On reprend la description dans l'annotation `@Schema(description = ...)` du Bean généré par OpenAPI.

### fieldType

| Caractéristiques du champ | fieldType |
|---------------------------|-----------|
| Composé d'autres valeurs  | `none`    |
|                           |           |
|                           |           |

# Export au format TSV avec Excel

Pour cela on supprime d'abord le fichier `rudi.tsv`. On exporte dans Excel avec les options suivantes :

- Nom de fichier : rudi.tsv
- Type : Texte (séparateur : tabulation) (*.txt)

On ferme Excel. Enfin on renomme le fichier pour supprimer l'extension `.txt` ajouté par Excel.

**Ne pas oublier de mettre à jour également le [fichier utilisé par Ansible].**

# Upload du fichier sur Dataverse

On utilise le script `upload_rudi_tsv.sh`.

# Vérifications

Pour vérifier la configuration d'un champ dans Dataverse, on peut appeler directement son API.

Exemple pour le champ `rudi_media_type` :

```
http://dv.open-dev.com:8095/api/admin/datasetfield/rudi_media_type
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

[fichier utilisé par Ansible]: ../../../../../../ansible/roles/rudi/files/rudi.tsv