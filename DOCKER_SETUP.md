# MySQL partagé avec Docker

Cette configuration fait tourner **un seul serveur MySQL** sur l'ordinateur hôte d'un développeur. Le deuxième développeur rejoint ce serveur par un tunnel SSH. Le port MySQL est lié à `127.0.0.1` et n'est donc pas publié sur le réseau. Les données persistent dans le volume Docker `mysql_data`.

## 1. Prérequis Ubuntu

Sur l'ordinateur qui héberge la base et sur celui qui exécute l'application : Ubuntu, Git et une connexion SSH. Pour travailler sur le projet, installez aussi un JDK 21, Maven et Tomcat 10.1 compatible avec Jakarta Servlet 5.

```bash
sudo apt update
sudo apt install -y git openjdk-21-jdk maven openssh-client
java -version
mvn -version
```

Seul l'hôte de la base a besoin de Docker Engine et du plugin Docker Compose. Suivez le guide d'installation Docker Engine pour Ubuntu sur docs.docker.com, puis vérifiez :

```bash
docker --version
docker compose version
sudo docker run --rm hello-world
```

Si vous utilisez `sudo` pour Docker, gardez ce préfixe dans les commandes suivantes. L'ajout au groupe `docker` donne des droits élevés à l'utilisateur système.

## 2. Créer les secrets locaux

À la racine du dépôt, sur l'ordinateur hôte :

```bash
cp .env.example .env
openssl rand -base64 36
```

Mettez deux secrets différents, générés avec la commande, dans `MYSQL_PASSWORD` et `MYSQL_ROOT_PASSWORD` du fichier `.env`. Ce fichier est ignoré par Git. Ne le transmettez pas dans le dépôt ni dans un message partagé.

La base cible configurée est `gestion_projet`, avec l'utilisateur applicatif `billetterie_app`. L'utilisateur généré par l'image MySQL reçoit les droits sur cette base, pas les droits root.

## 3. Démarrer et vérifier MySQL

Sur l'ordinateur hôte de la base :

```bash
docker compose up -d
docker compose ps
docker compose logs --follow mysql
```

Attendez l'état `healthy` dans `docker compose ps`. Connectez-vous localement avec le compte applicatif :

```bash
docker compose exec mysql sh -c 'exec mysql --host=127.0.0.1 --user="$MYSQL_USER" --password="$MYSQL_PASSWORD" "$MYSQL_DATABASE"'
```

Dans le client MySQL, `SHOW TABLES;` liste les tables puis `exit` ferme la session. Un conteneur nouvellement initialisé contient une base vide : les tables métier ne sont pas générées par ce fichier Compose.

## 4. Connecter Hibernate sur l'ordinateur hôte

`src/main/resources/application.properties` définit des valeurs de repli adaptées à l'hôte Docker. Hibernate accepte `DB_PASSWORD` ou, à défaut, `MYSQL_PASSWORD` (le nom présent dans `.env`). Configurez ces variables dans la configuration d'exécution IntelliJ ou dans l'environnement du service Tomcat :

```text
DB_URL=jdbc:mysql://localhost:3306/gestion_projet?serverTimezone=UTC
DB_USER=billetterie_app
DB_PASSWORD=<valeur de MYSQL_PASSWORD>
```

Dans IntelliJ, ouvrez **Run > Edit Configurations**, sélectionnez votre configuration Tomcat et ajoutez `DB_URL`, `DB_USER` et `DB_PASSWORD` dans **Environment variables**. Collez le secret local depuis `.env` dans IntelliJ, sans l'ajouter au dépôt. Le compte `billetterie_app` est le compte applicatif limité à cette base.

Le pilote reste `com.mysql.cj.jdbc.Driver`, le dialecte est celui de Hibernate 6 (`org.hibernate.dialect.MySQLDialect`) et `hibernate.hbm2ddl.auto` vaut `validate`. La validation ne crée ni ne met à jour les tables. Pour afficher les requêtes en développement, `HIBERNATE_SHOW_SQL=true` peut être défini dans l'environnement de Tomcat.

Construisez le WAR et déployez-le selon votre configuration Tomcat habituelle :

```bash
mvn clean package
```

## 5. Connexion du deuxième développeur (tunnel SSH)

L'hôte doit être joignable en SSH par un moyen sécurisé (VPN ou réseau privé) et son serveur SSH doit accepter le compte du deuxième développeur. N'ouvrez pas le port 3306 dans le pare-feu ni sur Internet. Depuis l'ordinateur du deuxième développeur, ouvrez un terminal et laissez cette commande active :

```bash
ssh -N -L 3307:127.0.0.1:3306 <compte_ssh>@<adresse_vpn_ou_privee_de_l_hote>
```

Le port local `3307` est transmis vers MySQL, qui reste lié à `127.0.0.1` sur l'hôte. Utilisez ensuite dans Tomcat sur le deuxième ordinateur :

```text
DB_URL=jdbc:mysql://localhost:3307/gestion_projet?serverTimezone=UTC
DB_USER=billetterie_app
DB_PASSWORD=<secret MYSQL_PASSWORD transmis de façon sûre>
```

Le tunnel doit rester ouvert pendant l'utilisation de l'application. Pour vérifier qu'il fonctionne, connectez-vous au serveur avec un client MySQL ou lancez l'application. Ne transmettez pas les secrets via GitHub.

## 6. Sauvegarder et restaurer

Effectuez la sauvegarde depuis le répertoire contenant le fichier `.env`. La redirection crée un fichier SQL local ; gardez-le hors de Git et protégez-le car il peut contenir des données personnelles :

```bash
umask 077
docker compose exec -T mysql sh -c 'exec mysqldump --user="$MYSQL_USER" --password="$MYSQL_PASSWORD" --single-transaction "$MYSQL_DATABASE"' > gestion_projet-$(date +%F).sql
```

Pour restaurer cette sauvegarde dans la base déjà initialisée :

```bash
docker compose exec -T mysql sh -c 'exec mysql --user="$MYSQL_USER" --password="$MYSQL_PASSWORD" "$MYSQL_DATABASE"' < gestion_projet-AAAA-MM-JJ.sql
```

La restauration peut modifier les données présentes dans la base cible. Vérifiez le fichier et faites une sauvegarde avant de restaurer.

## 7. Commandes courantes et persistance

```bash
docker compose ps
docker compose logs -f mysql
docker compose restart mysql
docker compose stop
docker compose start
```

Le volume `mysql_data` conserve les données après arrêt, redémarrage ou recréation du conteneur. Ne lancez pas `docker compose down -v` : l'option `-v` supprime le volume et donc les données qu'il contient.

## 8. Schéma existant et migrations

Le dépôt ne contient pas de dump de la base actuellement utilisée. Il ne permet donc pas de confirmer ses tables ni ses données. Les entités repérées sont `utilisateur` (héritage `SINGLE_TABLE`, discriminant `type_utilisateur`), `matchs`, `stade`, `place`, `reservation` et `billet`. Les références comme `clientId`, `matchId`, `placeId` et `stadeId` sont des champs numériques simples, pas des associations JPA déclarées. Le seul SQL versionné actuellement est `database/create_database.sql`, qui crée l'ancienne base `billetriedb` sans tables.

N'importez pas une structure supposée et ne pointez pas `validate` vers une base vide : Hibernate échouera si les tables attendues manquent. Avant de migrer la base existante, sauvegardez-la, examinez `SHOW CREATE TABLE` et comparez les colonnes/types avec les entités. Flyway pourra ensuite versionner des migrations fondées sur ce schéma vérifié. Aucune migration initiale n'est créée dans cette étape, pour éviter de recréer des tables ou d'affecter des données sans inventaire.

L'image MySQL initialise `gestion_projet` seulement lors du premier démarrage avec un volume neuf. Si vous avez déjà des données dans `billetriedb`, gardez cette base intacte et préparez d'abord un export, un contrôle du schéma et une procédure de transfert explicitement validée avant de déplacer les données.
