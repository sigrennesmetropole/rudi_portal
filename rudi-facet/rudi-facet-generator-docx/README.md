# I - Intégration de la facette Generator Docx

La facette met à disposition un service de templating pour les documents docx

L'ajout de la facette requière de compléter dans la classe _AppFacadeApplication_ la liste des packages scannés :

<pre>
@SpringBootApplication(scanBasePackages = { "org.rudi.facet.generator.docx" ...})

</pre>

Deux services sont mis à disposition :

- DocxMerger qui permet de fusionner plusieurs docx en un seul
- DocxGenerator qui permet de générer un docx par injection de données en utilisant des champs de fusion et du freemarker à partir d'un fichier docx template.

Pour la génération, il est nécessaire de créer une classe fille de AbstractDocxDataModel (Cf. TU) afin d'injecter toutes les données nécessaires.


