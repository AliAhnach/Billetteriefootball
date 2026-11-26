<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Mon billet</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="${pageContext.request.contextPath}/css/app.css?v=foot-ticket-12" rel="stylesheet">
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-football">
  <div class="container">
    <a class="navbar-brand brand-lockup" href="${pageContext.request.contextPath}/matchs"><img src="${pageContext.request.contextPath}/images/logos/logo-FootTicket.png" alt="" width="42" height="42"><span class="brand-wordmark" aria-label="FootTicket"><span class="brand-wordmark-foot">Foot</span><span class="brand-wordmark-ticket">Ticket</span></span></a>
    <div class="navbar-nav ms-auto"><a class="nav-link" href="${pageContext.request.contextPath}/billets">Mes réservations</a></div>
  </div>
</nav>
<main class="container py-5">
  <section class="card card-football mx-auto" style="max-width: 680px">
    <div class="card-body p-4 p-md-5">
      <span class="badge text-bg-success mb-2">Billet confirmé</span>
      <h1 class="h2 fw-bold mb-4">Votre billet</h1>
      <dl class="row mb-4">
        <dt class="col-sm-5">Numéro de réservation</dt><dd class="col-sm-7">#<c:out value="${reservation.id}"/></dd>
        <dt class="col-sm-5">Match</dt><dd class="col-sm-7"><c:out value="${match.equipeDomicile}"/> vs <c:out value="${match.equipeExterieure}"/></dd>
        <dt class="col-sm-5">Compétition</dt><dd class="col-sm-7"><c:out value="${match.competition}"/></dd>
        <dt class="col-sm-5">Stade</dt><dd class="col-sm-7"><c:out value="${match.stade}"/></dd>
        <dt class="col-sm-5">Date et heure</dt><dd class="col-sm-7"><c:out value="${match.dateHeureAffichee}"/></dd>
        <dt class="col-sm-5">Tribune</dt><dd class="col-sm-7"><c:out value="${reservation.categorie}"/></dd>
        <dt class="col-sm-5">Nombre de places</dt><dd class="col-sm-7"><c:out value="${reservation.nombrePlaces}"/></dd>
        <dt class="col-sm-5">Prix par place</dt><dd class="col-sm-7"><c:choose><c:when test="${not empty prixUnitaire}"><c:out value="${prixUnitaire}"/> DH</c:when><c:otherwise>À confirmer</c:otherwise></c:choose></dd>
        <dt class="col-sm-5">Total</dt><dd class="col-sm-7"><c:choose><c:when test="${not empty prixTotal}"><strong><c:out value="${prixTotal}"/> DH</strong></c:when><c:otherwise>À confirmer</c:otherwise></c:choose></dd>
      </dl>
      <a class="btn btn-football btn-lg" href="${pageContext.request.contextPath}/billets?telecharger=${reservation.id}">Télécharger mon billet en PDF</a>
      <a class="btn btn-outline-secondary btn-lg ms-2" href="${pageContext.request.contextPath}/billets">Retour à mes réservations</a>
    </div>
  </section>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<%@ include file="/WEB-INF/views/footer.jspf" %>
</body>
</html>
