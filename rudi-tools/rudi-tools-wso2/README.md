# Configuration complémentaire de WSO2 après installation de l'image
La présente documentation est destinée à lister les modifications de configuration qui viennent compléter l'installation *basique* de WSO2 version 4.2.0.

Les étapes indiquées ci-dessous ne sont pas automatisées par les déploiements Ansible (dev/qualif/r7/ovh) ou Kubernetes (dev-karbon, qualif-karbon).

[TOC]


## Prérequis
Une image de WSO2 doit être installée sur l'environnement cible et les IHM de WSO2 doivent être accessibles :
* https://[ip_wso2]:[port_wso2]/carbon 
* https://[ip_wso2]:[port_wso2]/admin
* https://[ip_wso2]:[port_wso2]/devportal
* https://[ip_wso2]:[port_wso2]/publisher

Si les services ne sont pas accessibles (page de login qui indique que ``Le rappel enregistré ne correspond pas à l'URL fournie.``), suivre les instructions suivantes  https://apim.docs.wso2.com/en/latest/troubleshooting/troubleshooting-invalid-callback-error/
*TODO à compléter car cette action est insuffisante sur dev-karbon pour accéder à l'admin*

## Registration de l'utilisateur admin
Il est nécessaire de récupérer les informations de connexion de l'utilisateur admin (register) pour les renseigner dans le paramétrage des services RUDI.

### Registration
#### Pour la 1ere fois
Exécuter la commande CURL suivante :

``` 
curl --location 'https://[ip_wso2]:[port_wso2]/api/identity/oauth2/dcr/v1.1/register' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic Authorization: Basic [base64(login:password)]' \
--data '
{
	"grant_types":["password", "client_credentials", "refresh_token"],
	"redirect_uris":[],
	"client_name": "admin"
}
' 
```

La réponse doit être du type :

```json
{
    "client_id": "mGd0kmV0fpAbgeIBB7O82AshWkca",
    "client_secret": "7wyhKe_8fD3lgsOkDFWEbeMZ6I4a",
    "client_secret_expires_at": 0,
    "redirect_uris": [
        ""
    ],
    "grant_types": [
        "password",
        "client_credentials",
        "refresh_token"
    ],
    "client_name": "admin"
} 
```

Les informations à prendre en compte sont les valeurs de `` client_id `` et de `` client_secret ``.


#### Récupération des informations les fois suivantes

Si la réponse contient une erreur du type

```
{
    "error": "invalid_client_metadata",
    "error_description": "Application with the name admin already exist in the system",
    "traceId": "ef73aa94-e72f-43af-bd24-e578d2db6659"
}
```

Il est possible de récupérer les infos avec la commande 

```
curl --location --request GET 'https://[ip_wso2]:[port_wso2]/api/identity/oauth2/dcr/v1.1/register/?client_name=admin' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic [base64(login:password)]' \
--data '{}'
```


### Report des informations dans la config applicative RUDI

Les valeurs de `` client_id `` et de `` client_secret `` sont à reporter dans la configuration de RUDI.

Après mise à jour de ces valeurs dans le code, un redéploiement est bien sûr nécessaire.

#### Report dans la configuration Ansible

La valeur de `` client_id `` doit être reportée dans la variable `` apimanager_admin_client_id `` de l'environnement concerné.

La valeur de `` client_secret `` doit être reportée dans la variable `` apimanager_admin_client_secret `` de l'environnement concerné.

#### Report dans la configuration Kubernetes (pour les environnements sur Karbon)

La valeur de `` client_id `` doit être reportée dans la variable `` apimanager.oauth2.client.admin.registration.client-id `` de l'environnement concerné.

La valeur de `` client_secret `` doit être reportée dans la variable `` apimanager.oauth2.client.admin.registration.client-secret `` de l'environnement concerné.


## Configuration des rôles

Les utilisateurs de RUDI utilisent des rôles WSO2 qui leur sont propres. Il faut donc les configurer. Cela se fait par les interfaces d'administration /carbon et /admin.
Après ces mises à jour, il est nécessaire de rédémarrer WSO2

### Configuration dans carbon

Se rendre sur l'URL ``https://[ip_wso2]:[port_wso2]/carbon/role/role-mgt.jsp`` (se connecter si nécessaire)

Configurer les permissions des rôles selon le tableau suivant :

| Rôle | Permissions |
| --- | --- |
| RUDI/ADMINISTRATOR | - Login <br> - Application Management <br> -> Delete <br> -> Create <br> -> Update <br> -> View <br> - API <br> -> Subscribe |
| RUDI/ANONYMOUS | - Login <br> - Application Management <br> -> Delete <br> -> Create <br> -> Update <br> -> View <br> - API <br> -> Subscribe |
| RUDI/USER | - Login <br> - Application Management <br> -> Delete <br> -> Create <br> -> Update <br> -> View <br> - API <br> -> Subscribe |

### Configuration dans admin

Se rendre sur l'URL ``https://[ip_wso2]:[port_wso2]/admin/settings/scope-mapping`` (se connecter avec l'utilisateur admin si nécessaire)

Créer les *scope mapping* pour les rôles 
- ``RUDI/ANONYMOUS`` -> *Role alias* ``Internal/subscriber``
- ``RUDI/USER`` -> *Role alias* ``Internal/subscriber``
- ``RUDI/ADMINISTRATOR`` -> Sélectioner manuellement les *Role Assignments* suivants :
    - publisher
        - Manage all Subscription related operations (apim:subscription_manage)
        - View, Retrieve API list (apim:api_list_view)
        - Publish API (apim:api_publish)
        - View, Retrieve API definition (apim:api_definition_view)
        - View API (apim:api_view)
        - View, create, update and remove endpoint certificates (apim:ep_certificates_manage)
        - Read permission to comments (apim:comment_view)
        - Write permission to comments (apim:comment_write)
        - Read and Write comments (apim:comment_manage)
        - Create API documents (apim:document_create)
        - View Subscription (apim:subscription_view)
        - Create API (apim:api_create)
        - Manage shared scopes (apim:shared_scope_manage)
        - Delete API (apim:api_delete)
        - Retrieve store settings (apim:publisher_settings)
        - Block Subscription (apim:subscription_block)
        - View, create, update and remove API specific mediation policies (apim:api_mediation_policy_manage)
	    
    - devportal
        - Manage all admin operations (apim:admin)
        - Retrieve, Manage and Import, Export applications (apim:app_manage)
        - Retrieve Developer Portal settings (apim:store_settings)
        - Retrieve, subscribe and configure Developer Portal alert types (apim:sub_alert_manage)
        - Import and export applications related operations (apim:app_import_export)
        - Generate API Keys (apim:api_key)
        - Retrieve, Manage subscriptions (apim:sub_manage)
        - Subscribe API (apim:subscribe)

