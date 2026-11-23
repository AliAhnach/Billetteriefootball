<%@ page contentType="text/html;charset=UTF-8" %>
<!doctype html><html lang="fr"><head><meta charset="UTF-8"><title>Connexion</title></head><body>
<h1>Connexion</h1><p>${erreur}</p><form method="post" action="${pageContext.request.contextPath}/login">
<label>E-mail <input type="email" name="email" required></label><label>Mot de passe <input type="password" name="motDePasse" required></label>
<button type="submit">Se connecter</button></form><a href="${pageContext.request.contextPath}/inscription">Créer un compte</a></body></html>
