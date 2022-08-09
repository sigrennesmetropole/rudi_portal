### Mise en place d'un handler WSO2

* Le jar contenant le handler doit être déposé dans *conf/apim/repository/components/lib/*
* Il faut créer un fichier velocity_template.xml à partir du [template Ansible](../../ansible/roles/wso2/templates/velocity_template.xml.j2) et le déposer dans *conf/apim/repository/resources/api_templates/velocity_template.xml*

Ce fichier contient 

```
<handler class="org.rudi.wso2.mediation.EncryptedMediaHandler">
    <property name="publicKeyURL" value="http://www.rudi.bzh/encrypt"/>
    <property name="privateKeyPath" value="repository/conf/privatekey.pem"/>
</handler>
```

ce qui permet de positionner 

* la valeur de l'url donnant le clé publique prise en compte par le handler
* le chemin de l'emplacement de la clé privée utilisée par le handler

Les propriétés utilisées sur l'API sont :
* *crypted* pour indiquer que le flux est chiffré
* *mime_type* pour indiquer le type mime à retourner après déchiffrement
* *encrypted_mime_type* pour indiquer le type mime avant déchiffrement

et éventuellement plein d'autres propriétés pour effectuer plus tard le contrôle des droits

### Comment le tester ?

* Aller dans le publisher
* Choisir une API au hazard 
* Ajouter les properties nécessaires (voir ci-dessus)

* Souscrire pour le user que l'on souhaite utiliser
* Récupérer les client_id/client_secret de l'application du user ayant souscrit à l'API
* Faire :

```
curl -vk -X POST -H "Authorization: Basic base64(client_key:client_secret)" -d "grant_type=client_credentials" "https://wso2.open-dev.com:8243/token"

curl -vk -X GET -H "Authorization: Bearer <le token retourner par l'appel précédent>" "https://wso2.open-dev.com:8243/datasets/03140de1-625b-44c7-b84f-ff737c11248a/dwnl/1.0.0"
```

## Voir aussi

- [Documentation WSO2](https://apim.docs.wso2.com/en/3.2.0/develop/extending-api-manager/extending-gateway/writing-custom-handlers/#engaging-the-custom-handler)
