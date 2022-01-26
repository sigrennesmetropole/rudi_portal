## RUDI - FACET - OAUTH2
Cette facette permet de simplifier l'authentification d'un module vers un autre.

Cette facette peut-être utilisée par une autre (telle que _rudi-facet-providers) pour permettre l'accès à un autre µservice.
Les propriétés suivantes sont nécessaires :

* _module.oauth2.client-id_  l'id du client (le nom du module)

* _module.oauth2.client-secret_  le mot de passe associé

* _module.oauth2.provider-uri_  l'url d'accès à oauth2 (qq chose de la forme http(s)://<host>:<port>/oauth/token

* _module.oauth2.scope_  un tableau de portée



