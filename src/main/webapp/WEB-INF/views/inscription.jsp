<%@ page contentType="text/html;charset=UTF-8" %>
<!doctype html><html lang="fr"><head><meta charset="UTF-8"><title>Inscription</title></head><body>
<h1>Créer un compte</h1><p>${erreur}</p><form method="post" action="${pageContext.request.contextPath}/inscription">
<label>Nom <input name="nom" required></label><label>E-mail <input type="email" name="email" required></label>
<label>Mot de passe <input type="password" name="motDePasse" required></label><button type="submit">S'inscrire</button></form></body></html>
