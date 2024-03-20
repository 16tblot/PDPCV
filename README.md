# Projet README

Ce document vise à fournir des instructions pour démarrer rapidement le projet et comprendre son fonctionnement.

## Serveur de Base de Données SQLite dans un Conteneur Docker

Pour lancer le serveur de base de données SQLite dans un conteneur Docker, suivez ces étapes :
1. Assurez-vous que Docker est installé sur votre système.
2. Clonez le dépôt du projet depuis GitHub : [https://github.com/16tblot/PDPCV/tree/Infra].
3. Accédez au répertoire contenant le Dockerfile pour le serveur de base de données SQLite.
4. Construisez l'image Docker en exécutant la commande suivante :
    ```bash
    docker build -t sqlite-db .
    ```
5. Lancez le conteneur Docker avec la commande suivante :
    ```bash
    docker run -d --name sqlite-db sqlite-db
    ```

## Serveur de Certification Authorities

Le serveur contenant la Certification Authorities (CA) est essentiel pour la gestion des certificats d'authentification. Suivez ces étapes pour démarrer le serveur :
1. Clonez le dépôt du projet depuis GitHub : [https://github.com/16tblot/PDPCV/tree/Infra].
2. LOREM IPSUM

## Conclusion

Ce README fournit les instructions de base pour démarrer les composants principaux du projet : le serveur de base de données SQLite dans un conteneur Docker et le serveur contenant la Certification Authorities. Pour des informations plus détaillées sur l'installation, la configuration et l'utilisation du projet, référez-vous à la documentation fournie ou aux fichiers README spécifiques dans chaque composant du projet.

