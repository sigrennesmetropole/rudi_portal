## RUDI - FACET - RVA

### Description

Cette facette a pour objectif de rechercher des addresses sur un ensemble de communes. Pour y arriver, elle utilise
l'API RVA de Rennes métropole <a>https://api-rva.sig.rennesmetropole.fr
La facette permet ainsi à terme permettra d'intérroger l'ensemble des endpoints mis à disposition par cette API mais à
ce jour ne permet que de taper sur l'endpoint **getfulladdresses** de l'API. La facette via son swagger instancie tous
les objets nécessaires pour communiquer avec l'API. La facette expose un ensemble de **properties** à ré-définir par les
µs utilisateurs.

### Intégration dans un µs

- Rajouter la facette dans les dépendances du µs
- Surcharger les propriétés nécessaires pour taper sur l'API (si non surchargées, les valeurs par défaut sont passées)
    * key: la clé de l'API propre à chaque projet
    * version: la version de l'API (à 1.0 par défaut)
    * format: format de la reponse (JSON ou XML)
    * epsg: le système de référence (voir doc API RVA pour plus d'infos)
- Créer un controlleur dans son µs qui utilise les services de la facette (**AddressService**)