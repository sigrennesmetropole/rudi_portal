# I - Intégration de la facette Bucket S3

La facette met à disposition un service de stockage objet selon le protocole s3

L'ajout de la facette requière de compléter dans la classe _AppFacadeApplication_ la liste des packages scannés :

<pre>
@SpringBootApplication(scanBasePackages = { "org.rudi.facet.buckets3" ...})

</pre>

### Propriétés de la facette

L'ajout de cette facette dans un µservice nécessite la configuration des propriétés _obligatoires_ suivantes :


* `rudi.documentstorage.endPoint` : url d'accès au stockage s3 (exemple : https://minio.rennes-metropole-rudi.karbon.open.global)
* `rudi.documentstorage.bucketName` : le nom du bucket (Exemple : rudi-dev)
* `rudi.documentstorage.identity` : le login 
* `rudi.documentstorage.credential`: le mot de passe

En compléments, les propriétés suivantes sont disponibles :

* `rudi.documentstorage.trustAllCerts`: pour accepter les certificats autosignés
* `rudi.documentstorage.providerId`: pour utiliser un autre protocole
* `rudi.documentstorage.threadCount`: pour utiliser l'API est multithread
