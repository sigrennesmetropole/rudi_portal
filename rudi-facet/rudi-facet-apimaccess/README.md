## RUDI - FACET - APIMACCESS
Cette facette permet d'offrir un mécanisme d'accès à l'API Manager.

### Propriétés de la facette

L'ajout de cette facette dans un µservice nécessite la configuration des propriétés suivantes :

* `apimanager.base-url` L'url de base (sans port ni chemin) d'accès à l'api REST de l'API Manager WSO2 (par exemple `https://wso2.open-dev.com`)
* `apimanager.port` Port de l'api REST de l'API Manager WSO2 (par défaut `9443`)
* `apimanager.api.url` L'url d'accès à l'api REST de l'API Manager WSO2 (par exemple `https://wso2.open-dev.com:9443/api/am`)
* `apimanager.api.admin.context` Le contexte d'accès à l'api REST en mode admin  (par défaut `/admin/v1`)
* `apimanager.api.publisher.context` Le contexte d'accès à l'api REST en mode admin  (par défaut `/publisher/v1`)
* `apimanager.api.store.context` Le contexte d'accès à l'api REST en mode admin  (par défaut `/store/v1`)
* `apimanager.api.store.api.categories` Nom de la catégorie d'API dans laquelle les APIs sont crées (par défaut on utilise la catégorie existante `RudiData`)
* `apimanager.api.store.application.default.name` Nom de l'application WSO2 par défaut pour un utilisateur
* `apimanager.api.store.application.default.requestPolicy` Nom de la politique de requêtage par défaut pour l'application par défaut de l'utilisateur connecté
* `apimanager.api.store.subscription.default.policy` Nom de la politique de souscription à une API par défaut pour l'utiilsateur connecté (autre que anonymous)
* `apimanager.api.store.subscription.anonymous.policy` Nom de la politique de souscription à une API par défaut pour l'utiilsateur anonymous

* `apimanager.gateway.url` L'url du gateway WSO2 utilisé pour télécharger les données d'une API via son contexte

* `apimanager.oauth2.client.registration-v0.17.path` Chemin pour l'enregistrement client v0.17 (par défaut : `/client-registration/v0.17/register`)
* `apimanager.oauth2.client.registration-v1.1.path` Chemin pour l'enregistrement client v1.1 (par défaut : `/api/identity/oauth2/dcr/v1.1/register`)
* `apimanager.oauth2.client.admin.registration.client-id` Client-id de l'utilisateur admin pour l'autorisation OAuth2
* `apimanager.oauth2.client.admin.registration.client-secret` Client-secret de l'utilisateur admin pour l'autorisation OAuth2
* `apimanager.oauth2.client.admin.registration.id` Il correspond au nom du client correspondant à l'utilisateur admin pour l'autorisation OAuth2 (il est possible de mettre le nom qu'on veut, par exemple `rest_api_admin`, à condition que la valeur soit différente pour chaque utilisateur)
* `apimanager.oauth2.client.admin.registration.scopes=apim:admin` Scope définissant les droits de l'utiisateur WSO2 admin (certaines actions demandent d'avoir des droits admin, raison pour laquelle le scope `apim:admin` est utilisé)
* `apimanager.oauth2.client.admin.username` Login de connexion d'un utilisateur qui a les droits admin
* `apimanager.oauth2.client.admin.password` Password de connexion d'un utilisateur qui a les droits admin
* `apimanager.oauth2.client.rudi.username` Login de connexion de l'utilisateur rudi
* `apimanager.oauth2.client.rudi.password` Password de connexion de l'utilisateur rudi
* `apimanager.oauth2.client.anonymous.username` Login de connexion de l'utilisateur anonymous
* `apimanager.oauth2.client.anonymous.password` Password de connexion de l'utilisateur anonymous

* `apimanager.oauth2.client.default.registration.scopes` Liste des scopes utilisés par les users connectés pour faire des requêtes vers WSO2
* `apimanager.oauth2.client.registration.uri` Url utilisé pour générer les paramètres de connection Oauth2 (notamment clienId et clientSecret) de l'utilisateur connecté (par exemple `https://wso2.open-dev.com:9443/client-registration/v0.17/register`)
* `apimanager.oauth2.client.provider.token-uri` Url permettant d'obtenir un token WSO2 (par exemple `https://wso2.open-dev.com:9443/oauth2/token`)

La procédure à exécuter pour générer le client-id et le client-secret est défini sur la page `https://apim.docs.wso2.com/en/latest/develop/product-apis/admin-apis/admin-v0.17/admin-v0.17/#section/Authentication`.

Il est toutefois possible d'utiliser les propriétés définies dans le fichier properties de test de la facet apimaccess (fichier `test/resources/apimaccess-test.properties`)

### Role WSO2

Quelques informations sur des roles associés aux utilisateurs via l'interface WSO2:
* `Internal/subscriber` Ce role permet de se connecter à l'interface devportal, de créer une application et générer le token, souscrire à des apis
* `Internal/creator` Ce role permet de se connecter à l'interface publisher, de visualiser/créer/modifier/supprimer une api, mais pas de changer le cycle d'une api (publish, deprecate, block...)
* `Internal/publisher` Ce role permet de se connecter à l'interface publisher, de visualiser une api ou de changer le cycle de vie si l'utilisateur a aussi le role `Internal/creator`


### Génération d'un token pour accéder à l'API Rest WSO2

Pour accéder à l'api Rest WSO2, il faut générer un token en utilisant les commandes suivantes:
* créer un fichier `payload.json` en y ajoutant le contenu décrit ci-dessous

```json
{
    "client_name": "{{login}}",
    "grant_types": [
        "client_credentials",
        "password",
        "refresh_token"
    ]
}
```
`{{login}}` correspond au login de l'utilisateur qui génère la clé
* Exécuter la commande `curl -X POST -H "Authorization: Basic base64(login:password)" -k -v -H "Content-Type: application/json" -d @payload.json https://wso2.open-dev.com:9443/api/identity/oauth2/dcr/v1.1/register > clientkey.json`.
Le header **Authorization** contient la conversion en base64 de la chaine de caractères `login:password`. La requête va généré un fichier `clientkey.json` (décrit en dessous) dans lequel on a notamment le **clientId** et le **clientSecret** lié à l'utilisateur.
  
```json
{
	"client_id": "aaaaaaaaaaa",
	"client_secret": "bbbbbbbbbbb",
	"client_name": "rest_api_admin"
}
```

* Exécuter la commande `curl -v -X POST -H "Authorization: Basic base64(clientId:clientSecret)" -k -d "grant_type=password&username=login&password=password&scope=apim:admin" -H "Content-Type:application/x-www-form-urlencoded" https://wso2.open-dev.com:9443/oauth2/token > token.json`.
On utilise dans le grant-type `password` mais il est possible d'utiliser n'importe lequel des grant-type autorisés.
Cette commande va générer le token pour l'API rest WSO2 dans un fichier token.json (décrit ci-dessous). Le header **Authorization** contient la conversion en base64 de la chaine de caractères `clientId:clientSecret`, le **clientId** et le **clientSecret** résultant de la requête précédente

```json
  {
    "access_token": "ccccccccc",
    "refresh_token": "ddddddddd",
    "scope": "apim:admin",
    "token_type": "Bearer"
  }
```

## Swagger WSO2

Corrections apportées sur les swagger 2.0 de WSO2.

Remplacer :

```yaml
404: |
  Not Found.
  The specified resource does not exist.
```

par : 

```yaml
404:
  description: |
    Not Found.
    The specified resource does not exist.
```

## Voir Aussi

- [APIs WSO2](https://apim.docs.wso2.com/en/3.2.0/develop/product-apis/overview/)
