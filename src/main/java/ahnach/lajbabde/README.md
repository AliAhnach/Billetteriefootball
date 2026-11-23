# Organisation Java

- `model` contient les objets métier (`Match`, `Billet`, `Utilisateur`).
- `dao` définit les contrats d'accès aux données.
- `service` contient les services métier construits autour des DAO.
- `servlet` contient les contrôleurs HTTP Jakarta Servlet.

Les pages, styles, scripts et images existants restent dans `src/main/webapp`.
