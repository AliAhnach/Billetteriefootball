<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html><html lang="fr"><head><meta charset="UTF-8"><title>Matchs</title></head><body>
<h1>Matchs disponibles</h1><ul><c:forEach var="match" items="${matchs}"><li>
<a href="${pageContext.request.contextPath}/match?id=${match.id}"><c:out value="${match.equipeDomicile}"/> – <c:out value="${match.equipeExterieure}"/></a>
</li></c:forEach></ul></body></html>
