# Configuration Magnolia pour RUDI

## ContentTypes

#### Généralités

Il existe 3 types de contenu :

* **term** (*contentTypes/term.yaml*) - ce type de contenu permet d'affiche des éléments de type Copyright, CGU etc.
* **news** (*contentTypes/news.yaml*) - ce type de contenu permet d'affiche des actualités
* **projectvalue** (*contentTypes/projectvalue.yaml*) - ce type de contenu permet d'affiche une valeur du projet

#### Tout sur les ContentTypes

* https://docs.magnolia-cms.com/product-docs/6.2/Features/Content-Types.html
* https://docs.magnolia-cms.com/product-docs/6.2/Modules/List-of-modules/Content-Types-module/Content-type-definition/Content-type-Model-definition.html#_submodels
* https://docs.magnolia-cms.com/product-docs/6.2/Developing/Content-Types-tutorial/Part-I-My-first-content-type.html
* https://documentation.magnolia-cms.com/display/DOCS57/Select+field

* https://docs.magnolia-cms.com/product-docs/6.2/Administration/How-to-delete-a-JCR-workspace.html

## Applications

A chaque type de contenu est associé une application (Exemple: *apps/news-app.yaml*).
Cette application permet d'éditer de nouveaux contenus pour chacun des types.

## APIs

4 APIs ont été exposées :

* categories (*http(s)://<host:port>/.rest/delivery/categories/v1*) : permet de lister les catégories
* news (*http(s)://<host:port>/.rest/delivery/news/v1*) : permet de lister les actualités
* projectvalues (*http(s)://<host:port>/.rest/delivery/projectvalues/v1*) : permet de lister les valeurs du projet
* terms (*http(s)://<host:port>/.rest/delivery/terms/v1*) : permet de lister les "terms"

Chacune des APIs peut prendre en paramètre :

* offset
* limit
* order
* <attribute>=<value>

Exemple :
*http(s)://<host:port>/.rest/delivery/terms/v1?offset=0&categories=<uuid>&name=CGU

## Templates

#### Tous sur les templates

* https://docs.magnolia-cms.com/product-docs/6.2/Developing/Templating.html
* https://docs.magnolia-cms.com/product-docs/6.2/Developing/Templating/Renderers.html
* https://docs.magnolia-cms.com/product-docs/6.2/Developing/Templating/Template-definition/Component-definition.html
* https://docs.magnolia-cms.com/product-docs/6.2/Developing/Templating/Template-scripts/Rendering-context-objects.html
* https://docs.magnolia-cms.com/product-docs/6.2/Developing/Templating/Template-scripts/Directives.html
* https://docs.magnolia-cms.com/product-docs/6.2/Developing/Templating/Template-scripts/Templating-functions.html

* https://git.magnolia-cms.com/projects/MODULES/repos/templating-essentials/browse/magnolia-templating-kit-2

##### Gestion des "Terms" (CGU, Copyrights...)
 
Le template "term" (*templates/pages/term.yaml*) permet de créer une page contenant un composant "termOne".

Le composant "termOne" (*templates/components/termOne.yaml*) permet d'insérer :
* soit un composant termDetail (affichage de l'actualité de manière complète)
* soit un composant termSimple (affichage de l'actualité de manière simplifiée - lien cliquable uniquement)
 
Il est donc possible - afin d'afficher un objet "Term" en mode simple ou détaillé - d'appeler la page sous la forme :
 
**http(s)://<host:port>/<nom de la page>?id=<id du term que l'on souhaite afficher>**

Le template "terms" (*templates/pages/terms.yaml*) permet de créer une page contenant (entre autre) un composant "termList".

Le composant "termList" (*templates/components/termList.yaml*) permet d'insérer :
* soit un composant termDetail
* soit un composant termSimple
 
Il est donc possible - afin d'afficher tous les "Terms" en mode simple ou détaillé - d'appeler la page sous la forme :
 
**http(s)://<host:port>/<nom de la page>**


##### Gestion des news

Le template "news" (*templates/pages/news.yaml*) permet de créer une page contenant un composant "newsOne".

Le composant "newsOne" (*templates/components/newsOne.yaml*) permet d'insérer :
* soit un composant newDetail (affichage de l'actualité de manière complète)
* soit un composant newSimple (affichage de l'actualité de manière simplifiée)
 
Il est donc possible - afin d'afficher un objet "News" en mode simple ou détaillé - d'appeler la page sous la forme :
 
**http(s)://<host:port>/<nom de la page>?id=<id de l'actualité que l'on souhaite afficher>**

Le template "newss" (*templates/pages/newss.yaml*) permet de créer une page contenant (entre autre) un composant "newsList".

Le composant "newsList" (*templates/components/newsList.yaml*) permet d'insérer :
* soit un composant newDetail (affichage de l'actualité de manière complète)
* soit un composant newSimple (affichage de l'actualité de manière simplifiée)
 
Il est donc possible - afin d'afficher tous les "News" en mode simple ou détaillé - d'appeler la page sous la forme :
 
**http(s)://<host:port>/<nom de la page>**


## Tips

* https://demo.magnolia-cms.com/

* https://docs.magnolia-cms.com/headless/getting-started-with-Magnolia-headless.html	

* https://git.magnolia-cms.com/projects/DEMOS

* https://www.magnolia-cms.com/blog/customising-the-richtext-field-using-ckeditor.html (configuration du ckeditor)
* https://docs.magnolia-cms.com/product-docs/6.2/Developing/Templating/Dialog-definition/Field-definition/List-of-fields/CKEditor-customization.html


