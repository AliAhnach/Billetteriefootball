<%@ page contentType="text/html;charset=UTF-8" %>
<!doctype html><html lang="fr"><head><meta charset="UTF-8"><title>Réservation</title></head><body>
<h1>Réserver une place</h1><p>${erreur}</p><form method="post" action="${pageContext.request.contextPath}/reserver">
<input type="hidden" name="matchId" value="${param.matchId}"><label>Place <input type="number" name="placeId" required></label>
<button type="submit">Réserver</button></form></body></html>
