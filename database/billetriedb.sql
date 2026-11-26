-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 27, 2026 at 12:01 AM
-- Server version: 10.11.19-MariaDB
-- PHP Version: 8.3.37

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `billetriedb`
--

-- --------------------------------------------------------

--
-- Table structure for table `billet`
--

CREATE TABLE `billet` (
  `id` bigint(20) NOT NULL,
  `categorie` varchar(255) DEFAULT NULL,
  `disponible` bit(1) NOT NULL,
  `matchId` bigint(20) DEFAULT NULL,
  `prix` decimal(38,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `billet`
--

INSERT INTO `billet` (`id`, `categorie`, `disponible`, `matchId`, `prix`) VALUES
(5, 'Tribune populaire', b'1', 6, 49.54),
(6, 'Tribune latérale', b'1', 6, 80.00),
(7, 'Tribune centrale', b'1', 6, 100.00),
(10, 'Tribune populaire', b'1', 9, 60.00),
(11, 'Tribune latérale', b'1', 9, 80.00),
(12, 'Tribune centrale', b'1', 9, 100.00),
(13, 'Tribune populaire', b'1', 10, 120.00),
(14, 'Tribune latérale', b'1', 10, 200.00),
(15, 'Tribune centrale', b'1', 10, 250.00);

-- --------------------------------------------------------

--
-- Table structure for table `matchs`
--

CREATE TABLE `matchs` (
  `id` bigint(20) NOT NULL,
  `competition` varchar(255) DEFAULT NULL,
  `dateHeure` datetime(6) DEFAULT NULL,
  `equipeDomicile` varchar(255) DEFAULT NULL,
  `equipeExterieure` varchar(255) DEFAULT NULL,
  `stade` varchar(255) DEFAULT NULL,
  `photoDomicile` tinyblob DEFAULT NULL,
  `photoExterieure` tinyblob DEFAULT NULL,
  `prix` decimal(10,2) DEFAULT NULL,
  `photoDomicileFichier` varchar(255) DEFAULT NULL,
  `photoExterieureFichier` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `matchs`
--

INSERT INTO `matchs` (`id`, `competition`, `dateHeure`, `equipeDomicile`, `equipeExterieure`, `stade`, `photoDomicile`, `photoExterieure`, `prix`, `photoDomicileFichier`, `photoExterieureFichier`) VALUES
(6, 'Botola Pro', '2026-11-27 22:15:00.000000', 'AS FAR', 'Ittihad Tanger', 'Stade Larbi Zaouli, Casablanca', NULL, NULL, 100.00, '9c2cef87-bca5-4e00-a9cc-2a2f93f5d7c4.png', 'eeaa3d7b-a81a-4735-a906-4f71bc950229.png'),
(9, 'Botola Pro', '2026-12-02 22:00:00.000000', 'Maghreb de Fès', 'Renaissance de Berkane', 'Complexe sportif de Fès', NULL, NULL, 100.00, '97ade65f-27cb-4c11-8de8-3ff52e20fd62.png', 'b8f1e87f-3e29-4116-9880-ee58780ee074.png'),
(10, 'Botola Pro', '2026-12-11 20:00:00.000000', 'Raja Casablanca', 'Wydad Casablanca', 'Complexe Mohammed V', NULL, NULL, 250.00, '6ac9f9a2-525a-4b0c-b569-f94a45bd800a.png', 'ba88dc72-e179-4af0-97b2-fcf205a84ae3.png');

-- --------------------------------------------------------

--
-- Table structure for table `place`
--

CREATE TABLE `place` (
  `id` bigint(20) NOT NULL,
  `disponible` bit(1) NOT NULL,
  `numero` varchar(255) DEFAULT NULL,
  `rangee` varchar(255) DEFAULT NULL,
  `secteur` varchar(255) DEFAULT NULL,
  `stadeId` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `place`
--

INSERT INTO `place` (`id`, `disponible`, `numero`, `rangee`, `secteur`, `stadeId`) VALUES
(1, b'0', '1', 'A', 'Tribune centrale', 1),
(2, b'1', '1', 'A', 'Tribune centrale', 2),
(3, b'1', '1', 'A', 'Tribune centrale', 3),
(4, b'1', '1', 'A', 'Tribune centrale', 4),
(5, b'1', '1', 'A', 'Tribune centrale', 5),
(6, b'1', '1', 'A', 'Tribune centrale', 6);

-- --------------------------------------------------------

--
-- Table structure for table `reservation`
--

CREATE TABLE `reservation` (
  `id` bigint(20) NOT NULL,
  `clientId` bigint(20) DEFAULT NULL,
  `dateReservation` date DEFAULT NULL,
  `matchId` bigint(20) DEFAULT NULL,
  `placeId` bigint(20) DEFAULT NULL,
  `statut` varchar(255) DEFAULT NULL,
  `categorie` varchar(255) DEFAULT NULL,
  `nombrePlaces` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `reservation`
--

INSERT INTO `reservation` (`id`, `clientId`, `dateReservation`, `matchId`, `placeId`, `statut`, `categorie`, `nombrePlaces`) VALUES
(4, 3, '2026-11-25', 6, NULL, 'VALIDEE', 'Tribune populaire', 3),
(5, 3, '2026-11-26', 6, NULL, 'ANNULEE', 'Tribune populaire', 1);

-- --------------------------------------------------------

--
-- Table structure for table `stade`
--

CREATE TABLE `stade` (
  `id` bigint(20) NOT NULL,
  `capacite` int(11) DEFAULT NULL,
  `nom` varchar(255) DEFAULT NULL,
  `ville` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `stade`
--

INSERT INTO `stade` (`id`, `capacite`, `nom`, `ville`) VALUES
(1, 45000, 'Stade Mohammed V', 'Casablanca'),
(2, 45000, 'Stade Mohammed V', 'Casablanca'),
(3, 45000, 'Stade Mohammed V', 'Casablanca'),
(4, 45000, 'Stade Mohammed V', 'Casablanca'),
(5, 45000, 'Stade Mohammed V', 'Casablanca'),
(6, 45000, 'Stade Mohammed V', 'Casablanca');

-- --------------------------------------------------------

--
-- Table structure for table `utilisateur`
--

CREATE TABLE `utilisateur` (
  `type_utilisateur` varchar(31) NOT NULL,
  `id` bigint(20) NOT NULL,
  `email` varchar(255) NOT NULL,
  `motDePasseHash` varchar(255) DEFAULT NULL,
  `nom` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `photoProfilFichier` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `utilisateur`
--

INSERT INTO `utilisateur` (`type_utilisateur`, `id`, `email`, `motDePasseHash`, `nom`, `role`, `photoProfilFichier`) VALUES
('CLIENT', 1, 'ramos@gmail.com', '$2a$12$OuL/.DlrPAEVgLWVIespvO1Jb8ntdr8/b2PwNfcqcw0DQFpxz7pDC', 'ramos dokali', 'CLIENT', NULL),
('CLIENT', 2, 'lahcenlajbabde27@gmail.com', '$2a$12$yoIKd5lv6D58ZgzxeElZ/.QFVuqrurcZowQgXRG0juLiLZRBVpSdO', 'lahcen lajbabde', 'CLIENT', NULL),
('CLIENT', 3, 'lahcen@gmail.com', '$2a$12$wIBXVLhZz6aI4g3XX4muWOxQiIa7TdOgGCA6nTPSywJHamTJDMbLe', 'lahcen lajbabde', 'CLIENT', '7267f7e6-f87e-4c6b-afea-412e715cbe86.jpg');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `billet`
--
ALTER TABLE `billet`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `matchs`
--
ALTER TABLE `matchs`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `place`
--
ALTER TABLE `place`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `reservation`
--
ALTER TABLE `reservation`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `stade`
--
ALTER TABLE `stade`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `utilisateur`
--
ALTER TABLE `utilisateur`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_rma38wvnqfaf66vvmi57c71lo` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `billet`
--
ALTER TABLE `billet`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `matchs`
--
ALTER TABLE `matchs`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `place`
--
ALTER TABLE `place`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `reservation`
--
ALTER TABLE `reservation`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `stade`
--
ALTER TABLE `stade`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `utilisateur`
--
ALTER TABLE `utilisateur`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
