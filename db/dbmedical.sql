-- phpMyAdmin SQL Dump
-- version 3.3.9
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: May 07, 2017 at 02:38 PM
-- Server version: 5.0.67
-- PHP Version: 5.3.5

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `dbmedical`
--

-- --------------------------------------------------------

--
-- Table structure for table `tblmedicalkey`
--

CREATE TABLE IF NOT EXISTS `tblmedicalkey` (
  `ImagePath` text,
  `EncryptKey` varchar(50) default NULL,
  `DateCreated` varchar(50) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `tblmedicalkey`
--

INSERT INTO `tblmedicalkey` (`ImagePath`, `EncryptKey`, `DateCreated`) VALUES
('C:\\Users\\user\\Documents\\NetBeansProjects\\MedicalImage_Compression_Security\\MedicalImage_Compression_Security\\heartimage-compressed-encrypted.jpg', 'cfdale6d6jsc', 'Sun Apr 02 10:16:45 WAT 2017'),
('C:\\Users\\user\\Documents\\NetBeansProjects\\MedicalImage_Compression_Security\\MedicalImage_Compression_Security\\heartimage-compressed-encrypted.jpg', '8ppusbkl0b4g', 'Sun May 07 14:47:07 WAT 2017');
