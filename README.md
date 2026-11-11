# GestionDesBilletsDeMatchFoot

## Matchs du jour sur la page d’accueil

Le panorama récupère directement dans le navigateur tous les matchs du jour couverts par API-Football et passe au match suivant toutes les trois secondes. Le JavaScript parcourt les pages de résultats disponibles.

L’API-Football nécessite une clé. Pour la version HTML/CSS/JS uniquement, ajouter cette clé à la constante `API_FOOTBALL_KEY` dans `src/main/webapp/js/script.js`. La clé sera visible dans le navigateur et ne doit pas être utilisée ainsi sur un site public. Le forfait gratuit limite les saisons disponibles selon la compétition et le nombre de requêtes quotidiennes.

L’API sportive ne fournit ni stock de billets ni tarifs. Ces valeurs restent « à confirmer » jusqu’à leur connexion à l’inventaire de billetterie du projet. Le bouton d’achat ouvre la page de réservation avec l’identifiant du match.
