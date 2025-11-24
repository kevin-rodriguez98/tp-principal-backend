-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: mysql-173c349b-frozen-lacteos.d.aivencloud.com    Database: frozen_lacteos
-- ------------------------------------------------------
-- Server version	8.0.35

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '3289434d-a153-11f0-86c4-b2e7d01001a5:1-27,
46d1f303-b33d-11f0-9482-22e135f5cd4b:1-828,
5bac6438-a23d-11f0-9b44-7658ca95db27:1-631,
7106bdec-a15d-11f0-b90b-822db0a99212:1-18';

--
-- Table structure for table `empleados`
--

DROP TABLE IF EXISTS `empleados`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `empleados` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(45) NOT NULL,
  `apellido` varchar(45) NOT NULL,
  `legajo` varchar(45) NOT NULL,
  `area` varchar(45) DEFAULT NULL,
  `rol` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `isprimeringreso` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idempleados_UNIQUE` (`id`),
  UNIQUE KEY `legajo_UNIQUE` (`legajo`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `historial_etapa`
--

DROP TABLE IF EXISTS `historial_etapa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `historial_etapa` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `etapa` varchar(255) DEFAULT NULL,
  `fecha_cambio` datetime(6) DEFAULT NULL,
  `orden_id` int NOT NULL,
  `empleado_id` int NOT NULL,
  `usuario` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKkintkw5repaa68qdstklli7o8` (`empleado_id`),
  KEY `FKec7bssrnd2p7d1rvp45b0nlac` (`orden_id`),
  CONSTRAINT `FKec7bssrnd2p7d1rvp45b0nlac` FOREIGN KEY (`orden_id`) REFERENCES `orden_produccion` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FKkintkw5repaa68qdstklli7o8` FOREIGN KEY (`empleado_id`) REFERENCES `empleados` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=185 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `insumo`
--

DROP TABLE IF EXISTS `insumo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `insumo` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `categoria` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `marca` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `unidad` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `stock` decimal(38,2) NOT NULL,
  `umbral_minimo_stock` int DEFAULT '0',
  `destino` int DEFAULT NULL,
  `proveedor` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id_locacion` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `codigo` (`codigo`),
  UNIQUE KEY `UK_6acwuxhab2m1oecpsh00r8jrs` (`id_locacion`),
  CONSTRAINT `FKi3786udfmsep1j4egkgb2yvxn` FOREIGN KEY (`id_locacion`) REFERENCES `locacion` (`id`),
  CONSTRAINT `insumo_chk_1` CHECK ((`umbral_minimo_stock` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=116 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `insumo_por_producto`
--

DROP TABLE IF EXISTS `insumo_por_producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `insumo_por_producto` (
  `id` int NOT NULL AUTO_INCREMENT,
  `stock_necesario_insumo` decimal(38,2) NOT NULL,
  `id_insumo` int NOT NULL,
  `id_producto` int NOT NULL,
  `unidad` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKc9hpwu5lj1vwc7xu4yvymj049` (`id_insumo`),
  KEY `FKew3w6jjtexw7bejuydcjalkw0` (`id_producto`),
  CONSTRAINT `FKc9hpwu5lj1vwc7xu4yvymj049` FOREIGN KEY (`id_insumo`) REFERENCES `insumo` (`id`),
  CONSTRAINT `FKew3w6jjtexw7bejuydcjalkw0` FOREIGN KEY (`id_producto`) REFERENCES `producto` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `locacion`
--

DROP TABLE IF EXISTS `locacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `locacion` (
  `id` int NOT NULL AUTO_INCREMENT,
  `deposito` varchar(45) DEFAULT NULL,
  `sector` varchar(45) DEFAULT NULL,
  `estante` varchar(45) DEFAULT NULL,
  `posicion` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `movimiento_insumo`
--

DROP TABLE IF EXISTS `movimiento_insumo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movimiento_insumo` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(50) NOT NULL,
  `nombre` varchar(50) DEFAULT NULL,
  `categoria` varchar(50) DEFAULT NULL,
  `marca` varchar(50) DEFAULT NULL,
  `unidad` varchar(10) DEFAULT NULL,
  `stock` decimal(38,2) NOT NULL,
  `lote` varchar(20) DEFAULT NULL,
  `umbral_minimo_stock` int DEFAULT '0',
  `tipo` varchar(20) DEFAULT NULL,
  `impactado` tinyint(1) DEFAULT NULL,
  `destino` varchar(255) DEFAULT NULL,
  `proveedor` varchar(255) DEFAULT NULL,
  `legajo_empleado` varchar(45) NOT NULL,
  `fecha_hora` datetime(6) DEFAULT NULL,
  `creation_username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `movimiento_producto`
--

DROP TABLE IF EXISTS `movimiento_producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movimiento_producto` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cantidad` decimal(38,2) NOT NULL,
  `codigo_producto` varchar(255) NOT NULL,
  `creation_username` varchar(255) DEFAULT NULL,
  `destino` varchar(255) DEFAULT NULL,
  `fecha` datetime(6) DEFAULT NULL,
  `impactado` bit(1) DEFAULT NULL,
  `tipo` varchar(255) NOT NULL,
  `categoria` varchar(255) DEFAULT NULL,
  `lote` varchar(255) DEFAULT NULL,
  `marca` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `unidad` varchar(255) DEFAULT NULL,
  `legajo_empleado` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `orden_produccion`
--

DROP TABLE IF EXISTS `orden_produccion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orden_produccion` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo_producto` varchar(255) DEFAULT NULL,
  `creation_username` varchar(255) DEFAULT NULL,
  `estado` varchar(255) NOT NULL,
  `fecha_creacion` datetime(6) DEFAULT NULL,
  `fecha_entrega` date NOT NULL,
  `impactado` bit(1) DEFAULT NULL,
  `lote` varchar(255) DEFAULT NULL,
  `marca` varchar(255) NOT NULL,
  `producto_requerido` varchar(255) NOT NULL,
  `stock_producido_real` decimal(38,2) DEFAULT NULL,
  `stock_requerido` decimal(38,2) NOT NULL,
  `envasado` varchar(255) DEFAULT NULL,
  `etapa` varchar(255) DEFAULT NULL,
  `nota` varchar(255) DEFAULT NULL,
  `presentacion` varchar(255) DEFAULT NULL,
  `empleado_id` int NOT NULL,
  `tiempo_produccion` decimal(38,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKji408tq9ou842wvi2xtqwux4i` (`empleado_id`),
  CONSTRAINT `FKji408tq9ou842wvi2xtqwux4i` FOREIGN KEY (`empleado_id`) REFERENCES `empleados` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=158 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `producto`
--

DROP TABLE IF EXISTS `producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto` (
  `id` int NOT NULL AUTO_INCREMENT,
  `categoria` varchar(255) NOT NULL,
  `codigo` varchar(255) NOT NULL,
  `creation_username` varchar(255) DEFAULT NULL,
  `linea` varchar(255) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `stock` decimal(38,2) NOT NULL,
  `unidad` varchar(255) NOT NULL,
  `presentacion` varchar(255) DEFAULT NULL,
  `lote` varchar(255) DEFAULT NULL,
  `fecha_creacion` datetime(6) DEFAULT NULL,
  `legajo_empleado` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_kxdt4u9c4w6vveo7ylph4pd09` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sub_etapas`
--

DROP TABLE IF EXISTS `sub_etapas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sub_etapas` (
  `id` int NOT NULL,
  `nombre` varchar(45) DEFAULT NULL,
  `fecha` varchar(45) DEFAULT NULL,
  `empleado_id` varchar(45) DEFAULT NULL,
  `orden_id` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tiempo_produccion`
--

DROP TABLE IF EXISTS `tiempo_produccion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tiempo_produccion` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `producto_id` int NOT NULL,
  `tiempo_ciclo` decimal(38,2) DEFAULT NULL,
  `tiempo_preparacion` decimal(38,2) DEFAULT NULL,
  `cantidad_max_tanda` decimal(38,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_rjd23qppr5fq9k2sv760vm063` (`producto_id`),
  CONSTRAINT `FK73po1pl65vpmpngo6qqg6vre` FOREIGN KEY (`producto_id`) REFERENCES `producto` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-23 21:37:36
