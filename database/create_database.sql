-- À exécuter dans phpMyAdmin sur la même instance MySQL que l'application.
-- Hibernate créera ensuite les tables au démarrage de Tomcat.
CREATE DATABASE IF NOT EXISTS `BilletrieDB`
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
