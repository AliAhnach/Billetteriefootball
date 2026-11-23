<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html><html lang="fr"><head><meta charset="UTF-8"><title>Mes billets</title></head><body>
<h1>Mes billets</h1><ul><c:forEach var="billet" items="${billets}"><li><c:out value="${billet.categorie}"/> — <c:out value="${billet.prix}"/></li></c:forEach></ul></body></html>
