-- phpMyAdmin SQL Dump
-- version 3.5.2.2
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generato il: Feb 03, 2014 alle 17:47
-- Versione del server: 5.5.27
-- Versione PHP: 5.4.7

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `oas`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `consumer`
--
-- Creazione: Gen 30, 2014 alle 09:41
--

DROP TABLE IF EXISTS `consumer`;
CREATE TABLE IF NOT EXISTS `consumer` (
  `c_id` int(11) NOT NULL AUTO_INCREMENT,
  `consumer_key` text NOT NULL,
  `consumer_secret` text NOT NULL,
  `name` text NOT NULL,
  PRIMARY KEY (`c_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=6 ;

--
-- Dump dei dati per la tabella `consumer`
--

INSERT INTO `consumer` (`c_id`, `consumer_key`, `consumer_secret`, `name`) VALUES
(1, 'abcde', 'zyxwv', 'FirstClient'),
(2, 'cons567', 'cons890', 'SecondClient'),
(3, 'kkk123', 'sss456', 'ThirdClient'),
(4, 'kkk2123', 'sss2456', 'ForthClient'),
(5, 'cons158', 'secr7897', 'FifthConsumer');

-- --------------------------------------------------------

--
-- Struttura della tabella `logon_user`
--
-- Creazione: Feb 03, 2014 alle 14:41
--

DROP TABLE IF EXISTS `logon_user`;
CREATE TABLE IF NOT EXISTS `logon_user` (
  `u_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(40) NOT NULL,
  `password` varchar(60) NOT NULL,
  `name` varchar(30) NOT NULL,
  `surname` varchar(30) NOT NULL,
  PRIMARY KEY (`u_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Dump dei dati per la tabella `logon_user`
--

INSERT INTO `logon_user` (`u_id`, `username`, `password`, `name`, `surname`) VALUES
(1, 'luca.davoli', '220fc4fa65a1db4988fc92b068d4482507e9f978', 'Luca', 'Davoli'),
(2, 'simone.cirani', 'b7e9a6e2e04ed3edbf8b4e21def4beccbb6eb6b1', 'Simone', 'Cirani');

-- --------------------------------------------------------

--
-- Struttura della tabella `resource`
--
-- Creazione: Gen 30, 2014 alle 09:47
--

DROP TABLE IF EXISTS `resource`;
CREATE TABLE IF NOT EXISTS `resource` (
  `r_id` int(11) NOT NULL AUTO_INCREMENT,
  `resource_uri` text NOT NULL,
  PRIMARY KEY (`r_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=8 ;

--
-- Dump dei dati per la tabella `resource`
--

INSERT INTO `resource` (`r_id`, `resource_uri`) VALUES
(1, 'http://10.27.0.5/print'),
(2, 'coap://10.27.1.15/temp'),
(3, 'http://10.27.0.95/light'),
(5, 'http://10.27.0.5/print2'),
(6, 'http://localhost/print3'),
(7, 'http://105.net/webradio');

-- --------------------------------------------------------

--
-- Struttura della tabella `resource_access`
--
-- Creazione: Gen 30, 2014 alle 09:47
--

DROP TABLE IF EXISTS `resource_access`;
CREATE TABLE IF NOT EXISTS `resource_access` (
  `resource_id` int(11) NOT NULL,
  `token_id` int(11) NOT NULL,
  `actions` varchar(20) NOT NULL,
  PRIMARY KEY (`resource_id`,`token_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `resource_access`
--

INSERT INTO `resource_access` (`resource_id`, `token_id`, `actions`) VALUES
(1, 1, 'GET,POST,PUT,DELETE'),
(2, 2, 'GET,POST'),
(3, 1, 'DELETE'),
(5, 2, 'POST,PUT');

-- --------------------------------------------------------

--
-- Struttura della tabella `token`
--
-- Creazione: Gen 30, 2014 alle 09:45
--

DROP TABLE IF EXISTS `token`;
CREATE TABLE IF NOT EXISTS `token` (
  `t_id` int(11) NOT NULL AUTO_INCREMENT,
  `token` text NOT NULL,
  `token_secret` text NOT NULL,
  `type` text NOT NULL,
  `user_id` int(11) NOT NULL,
  `consumer_id` int(11) NOT NULL,
  `expiration_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`t_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

--
-- Dump dei dati per la tabella `token`
--

INSERT INTO `token` (`t_id`, `token`, `token_secret`, `type`, `user_id`, `consumer_id`, `expiration_time`) VALUES
(1, 'act123', 'act456', 'AT', 1, 1, '0000-00-00 00:00:00'),
(2, 'bre456', 'bre789', 'AT', 2, 2, '0000-00-00 00:00:00'),
(4, 'ijr8eue8tue99fv89rtcj89rvj9', 'nifndvud9s8jdvjd90svdj9j98p9j9', 'RT', 4, 5, '0000-00-00 00:00:00');

-- --------------------------------------------------------

--
-- Struttura della tabella `user`
--
-- Creazione: Gen 30, 2014 alle 09:46
--

DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
  `u_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` text NOT NULL,
  PRIMARY KEY (`u_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=7 ;

--
-- Dump dei dati per la tabella `user`
--

INSERT INTO `user` (`u_id`, `name`) VALUES
(1, 'ResourceOwner1'),
(2, 'ResourceOwner2'),
(3, 'ResourceOwner3'),
(4, 'ResourceOwner4'),
(5, 'ResourceOwner5'),
(6, 'Luca');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
