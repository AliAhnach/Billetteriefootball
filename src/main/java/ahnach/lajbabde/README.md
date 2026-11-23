# Organisation Java

- `model` contient les objets métier (`Match`, `Billet`, `Utilisateur`).
- `dao` définit les contrats d'accès aux données.
- `service` contient les services métier construits autour des DAO.
- `servlet` contient les contrôleurs HTTP Jakarta Servlet.

Le frontend a été retiré. `src/main/webapp` ne contient que la configuration Servlet; les contrôleurs sont destinés à être appelés par un client frontend externe.
