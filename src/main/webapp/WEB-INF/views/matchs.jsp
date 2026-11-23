<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html><html lang="fr"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Matchs disponibles</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"><link href="${pageContext.request.contextPath}/css/app.css" rel="stylesheet"></head><body>
<nav class="navbar navbar-expand navbar-football"><div class="container"><a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/matchs">⚽ Billetterie</a><div class="navbar-nav ms-auto"><a class="nav-link" href="${pageContext.request.contextPath}/login">Connexion</a></div></div></nav>
<main class="container py-4"><header class="page-header"><span class="badge text-bg-success mb-2">Calendrier</span><h1 class="display-6 fw-bold">Matchs disponibles</h1><p>Choisissez votre prochaine rencontre et réservez votre place.</p></header>
<c:if test="${empty matchs}"><div class="alert alert-light border">Aucun match disponible pour le moment.</div></c:if>
<div class="row g-3"><c:forEach var="match" items="${matchs}"><div class="col-12 col-md-6 col-xl-4"><article class="card card-football h-100"><div class="card-body p-4">
<span class="badge rounded-pill text-bg-light mb-3"><c:out value="${match.competition}"/></span><h2 class="h5 fw-bold"><c:out value="${match.equipeDomicile}"/> <span class="text-success">vs</span> <c:out value="${match.equipeExterieure}"/></h2>
<p class="text-muted-football mb-4"><c:out value="${match.stade}"/><br><c:out value="${match.dateHeure}"/></p><a class="btn btn-football" href="${pageContext.request.contextPath}/match?id=${match.id}">Voir le match</a>
</div></article></div></c:forEach></div></main></body></html>
