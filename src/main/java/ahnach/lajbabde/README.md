# Organisation Java

- `model` contient les objets métier (`Match`, `Billet`, `Utilisateur`).
- `dao` définit les contrats d'accès aux données.
- `service` contient les services métier construits autour des DAO.
- `servlet` contient les contrôleurs HTTP Jakarta Servlet.

Le frontend a été retiré. `src/main/webapp` ne contient que la configuration Servlet; les contrôleurs sont destinés à être appelés par un client frontend externe.

## Connexion à MySQL

La base attendue est `billetriedb` sur `localhost:3306`. Importez le fichier SQL dans cette base, puis renseignez `db.user` et `db.password` dans `src/main/resources/application.properties`, ou définissez `DB_USER`, `DB_PASSWORD` et éventuellement `DB_URL` dans l'environnement du serveur Tomcat. `DB_URL` remplace entièrement l'URL du fichier properties : vérifiez qu'il se termine bien par `/billetriedb`.

Au premier démarrage, Hibernate utilise `hibernate.hbm2ddl.auto=update` pour créer/mettre à jour les tables à partir des entités. La table `matchs` correspond aux rencontres (le mot `match` est réservé dans certaines versions de MySQL). L'application ne crée pas elle-même la base `BilletrieDB`.

Si MySQL affiche `Unknown database 'billetriedb'`, exécutez `database/create_database.sql` dans phpMyAdmin, sur la même instance MySQL (port 3306), puis redémarrez Tomcat. Sur Linux, la casse du nom doit correspondre exactement. Après une modification de la configuration, reconstruisez et redéployez le WAR, puis redémarrez Tomcat. Si Tomcat affiche `Address already in use` sur 8080 ou 8005, arrêtez l'ancienne instance Tomcat avant de relancer SmartTomcat, ou affectez d'autres ports à l'instance.
