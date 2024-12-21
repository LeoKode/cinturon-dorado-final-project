-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Versión del servidor:         8.0.31 - MySQL Community Server - GPL
-- SO del servidor:              Win64
-- HeidiSQL Versión:             12.3.0.6589
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Volcando estructura de base de datos para cinturon_dorado_mini
CREATE DATABASE IF NOT EXISTS `cinturon_dorado_mini` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `cinturon_dorado_mini`;

-- Volcando estructura para tabla cinturon_dorado_mini.alumnos
CREATE TABLE IF NOT EXISTS `alumnos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `edad` int DEFAULT NULL,
  `nivel_cinturon` enum('BLANCO','AMARILLO','NARANJA','VERDE','AZUL','ROJO','NEGRO') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ultimo_pago` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla cinturon_dorado_mini.alumnos: ~7 rows (aproximadamente)

-- Volcando estructura para tabla cinturon_dorado_mini.alumno_clase
CREATE TABLE IF NOT EXISTS `alumno_clase` (
  `alumno_id` bigint NOT NULL,
  `clase_id` bigint NOT NULL,
  PRIMARY KEY (`alumno_id`,`clase_id`),
  KEY `clase_id` (`clase_id`),
  CONSTRAINT `alumno_clase_ibfk_1` FOREIGN KEY (`alumno_id`) REFERENCES `alumnos` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `alumno_clase_ibfk_2` FOREIGN KEY (`clase_id`) REFERENCES `clases` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla cinturon_dorado_mini.alumno_clase: ~0 rows (aproximadamente)

-- Volcando estructura para tabla cinturon_dorado_mini.clases
CREATE TABLE IF NOT EXISTS `clases` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `profesor_id` bigint NOT NULL,
  `fechaHora` datetime NOT NULL,
  `tipo` enum('PRINCIPIANTE','INTERMEDIO','AVANZADO','ESPECIAL') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_hora` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `profesor_id` (`profesor_id`),
  CONSTRAINT `clases_ibfk_1` FOREIGN KEY (`profesor_id`) REFERENCES `profesores` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla cinturon_dorado_mini.clases: ~0 rows (aproximadamente)

-- Volcando estructura para tabla cinturon_dorado_mini.inventario
CREATE TABLE IF NOT EXISTS `inventario` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `cantidad` int NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `tipo` enum('UNIFORME','PROTECCION','ENTRENAMIENTO','COMPETENCIA','OTROS') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `stockMinimo` int NOT NULL,
  `fechaUltimaActualizacion` date DEFAULT NULL,
  `fecha_ultima_actualizacion` date DEFAULT NULL,
  `stock_minimo` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla cinturon_dorado_mini.inventario: ~0 rows (aproximadamente)

-- Volcando estructura para tabla cinturon_dorado_mini.pagos
CREATE TABLE IF NOT EXISTS `pagos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `alumno_id` bigint NOT NULL,
  `fecha` date NOT NULL,
  `monto` decimal(6,2) NOT NULL,
  `pagado` tinyint(1) NOT NULL,
  `concepto` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `estado` enum('PENDIENTE','PAGADO','VENCIDO','CANCELADO') COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `alumno_id` (`alumno_id`),
  CONSTRAINT `pagos_ibfk_1` FOREIGN KEY (`alumno_id`) REFERENCES `alumnos` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla cinturon_dorado_mini.pagos: ~0 rows (aproximadamente)

-- Volcando estructura para tabla cinturon_dorado_mini.profesores
CREATE TABLE IF NOT EXISTS `profesores` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `especialidad` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nivelCinturon` enum('NEGRO') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `telefono` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nivel_cinturon` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla cinturon_dorado_mini.profesores: ~0 rows (aproximadamente)

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
