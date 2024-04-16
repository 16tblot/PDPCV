# PDPCV

Ce document vise à fournir un aperçu des objectifs principaux concernant le développement de l'application mobile, en mettant particulièrement l'accent sur la sécurisation des requêtes vers le serveur de base de données et dans la base de données elle-même, ainsi que sur les requêtes vers le certificat d'authentification.
Objectifs principaux :

## Développement de l'Application Mobile :
L'objectif principal est de développer une application mobile intuitive permettant la communication entre plusieurs utilisateurs. Les fonctionnalités clés doivent être développées selon les spécifications du projet (c.f cahier des charges).

### prototype d'autentification de compte utilisant OkHttp pour les requetes à un [serveur](https://github.com/16tblot/PDPCV/tree/Infra/server):
[pdp_app](https://github.com/16tblot/PDPCV/tree/application/pdp_app)

#### credentials possibles:

```test:test``` Utilisateur non verifié

```test2:test``` Utilisateur verifié

#### testserver (obsolète):

```python manage.py runserver 0.0.0.0:8000```

##### Dans ```API.kt```, asigner à ```baseUrl``` l'adresse IP locale du serveur.


## Requêtes vers le Certificat d'Authentification :
Les interactions avec le certificat d'authentification doivent être sécurisées et fiables. Cela implique la mise en place de mécanismes d'authentification robustes pour vérifier l'identité des utilisateurs autorisés et garantir que seules les requêtes légitimes sont traitées.
    
## Requêtes vers la base de données :
Les interactions avec la base de données doivent permettre la connexion à l'utilisateurs qui aura été validé par le certificat d'authentification. Les données doivent être stockées crypté dans la base de données.

