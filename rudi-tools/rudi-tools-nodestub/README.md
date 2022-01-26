# Installation locale

Pour faire fonctionner le NodeStub sur un poste de dev local, il faut ajuster certains points.

## URL du NodeProvider

Dans le schéma `providers_data`, ouvrir la table `provider` et noter l'UUID du fournisseur portant le code `'NODE_STUB'`
.

Ouvrir la table `node_provider` et se placer sur la ligne portant cet UUID. Remplacer alors son `url` :

```
http://10.50.1.45:28001/nodestub
```

par

```
http://localhost:28001/nodestub
```

## Création des dossiers locaux

Pour créer ses rapports d'intégration, le NodeStub a besoin de pouvoir écrire dans les dossiers suivants :

```
D:\Projets\workspace-rm-rudi\rudi-volume\data\nodestub\da7946d3-10ff-4374-9759-0b6b43ac70fb.rpt
```

S'il n'est pas possible de créer ce dossier sur son poste, alors on peut changer le répertoire en surchargeant le
paramètre JVM au lancement de l'application NodeStub. Exemple :

```
-Drudi.nodestub.reports-directory=C:\\Users\\MLA20849\\AppData\\Local\\rudi\\rudi\\0.0.1-SNAPSHOT\\reports
-Drudi.nodestub.resources-directory=C:\\Users\\MLA20849\\AppData\\Local\\rudi\\rudi\\0.0.1-SNAPSHOT\\resources
```

**TODO [RUDI-628]** : il ne faudrait pas avoir à le créer la main.

[RUDI-628]: https://jira.open-groupe.com/browse/RUDI-682
