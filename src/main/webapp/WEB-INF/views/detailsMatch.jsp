<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html><html lang="fr"><head><meta charset="UTF-8"><title>Détail du match</title></head><body>
<h1><c:out value="${match.equipeDomicile}"/> – <c:out value="${match.equipeExterieure}"/></h1>
<p><c:out value="${match.competition}"/> · <c:out value="${match.stade}"/></p><p><c:out value="${match.dateHeure}"/></p>
<a href="${pageContext.request.contextPath}/matchs">Retour aux matchs</a></body></html>
