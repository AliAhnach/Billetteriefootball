<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html><html lang="fr"><head><meta charset="UTF-8"><title>Mes billets</title></head><body>
<h1>Billets disponibles</h1><c:if test="${empty billets}"><p>Aucun billet à afficher.</p></c:if>
<ul><c:forEach var="billet" items="${billets}"><li><c:out value="${billet.categorie}"/> — <c:out value="${billet.prix}"/>
<c:if test="${billet.disponible}">Disponible</c:if><c:if test="${not billet.disponible}">Indisponible</c:if></li></c:forEach></ul></body></html>
