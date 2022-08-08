## RUDI - FACET - EMAIL

Cette facette permet d'ajouter simplement un service d'envoi de courriel.

L'ajout de la facette requière de compléter dans la classe _AppFacadeApplication_ la liste des packages scannés :

<pre>
@SpringBootApplication(scanBasePackages = { "org.rudi.facet.email" ...})

</pre>

### Propriétés de la facette

L'ajout de cette facette dans un µservice nécessite la configuration des propriétés _obligatoires_ suivantes :

* `mail.smtp.host` le serveur de courriel

L'ajout de cette facette dans un µservice dispose aussi de la configuration des propriétés suivantes :
* `mail.transport.protocol:smtp` Le protocol email - valeur par défaut : `smtp`
* `mail.smtp.auth` Indique si une authentification est nécessaire - valeur par défaut : `false`
* `mail.smtp.port` Le port d'écoute - valeur par défaut `25`
* `mail.smtp.user` L'utilisateur pour l'authentification
* `mail.smtp.password` Le mot de passe pour l'authentification
* `mail.from` L'adresse par défaut d'émission - valeur par défaut `robot@rudi.bzh`
* `mail.smtp.starttls.enable`- valeur par défaut : `false`
* `mail.debug` Permet d'activer le mode debug pour l'émission de courriel - valeur par défaut : `false`
