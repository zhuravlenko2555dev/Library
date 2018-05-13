-- --------------------------------------------------------
-- Хост:                         127.0.0.1
-- Версия сервера:               5.7.21-log - MySQL Community Server (GPL)
-- Операционная система:         Win64
-- HeidiSQL Версия:              9.4.0.5174
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Дамп структуры базы данных library
CREATE DATABASE IF NOT EXISTS `library` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `library`;

-- Дамп структуры для таблица library.account
CREATE TABLE IF NOT EXISTS `account` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `login` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `name` varchar(50) NOT NULL,
  `surname` varchar(50) NOT NULL,
  `middle_name` varchar(50) NOT NULL,
  `date_of_birth` date NOT NULL,
  `number` varchar(50) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `id_address` int(10) unsigned NOT NULL,
  `id_group` int(10) unsigned DEFAULT NULL,
  `id_account_status` tinyint(3) unsigned NOT NULL,
  `id_gender` tinyint(3) unsigned NOT NULL,
  `id_course` tinyint(3) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `login` (`login`),
  UNIQUE KEY `number` (`number`),
  UNIQUE KEY `email` (`email`),
  KEY `FK_account_address` (`id_address`),
  KEY `FK_account_group` (`id_group`),
  KEY `FK_account_account_status` (`id_account_status`),
  KEY `FK_account_gender` (`id_gender`),
  KEY `FK_account_course` (`id_course`),
  CONSTRAINT `FK_account_account_status` FOREIGN KEY (`id_account_status`) REFERENCES `account_status` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_account_address` FOREIGN KEY (`id_address`) REFERENCES `address` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_account_course` FOREIGN KEY (`id_course`) REFERENCES `course` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_account_gender` FOREIGN KEY (`id_gender`) REFERENCES `gender` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_account_group` FOREIGN KEY (`id_group`) REFERENCES `group` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.account_photo_large
CREATE TABLE IF NOT EXISTS `account_photo_large` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_account` int(10) unsigned NOT NULL,
  `id_server` tinyint(3) unsigned NOT NULL,
  `path` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_account` (`id_account`),
  UNIQUE KEY `path` (`path`),
  KEY `FK_account_photo_large_server` (`id_server`),
  CONSTRAINT `FK_account_photo_large_account` FOREIGN KEY (`id_account`) REFERENCES `account` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_account_photo_large_server` FOREIGN KEY (`id_server`) REFERENCES `server` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.account_photo_medium
CREATE TABLE IF NOT EXISTS `account_photo_medium` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_account` int(10) unsigned NOT NULL,
  `id_server` tinyint(3) unsigned NOT NULL,
  `path` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_account` (`id_account`),
  UNIQUE KEY `path` (`path`),
  KEY `FK_account_photo_medium_server` (`id_server`),
  CONSTRAINT `FK_account_photo_medium_account` FOREIGN KEY (`id_account`) REFERENCES `account` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_account_photo_medium_server` FOREIGN KEY (`id_server`) REFERENCES `server` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.account_photo_small
CREATE TABLE IF NOT EXISTS `account_photo_small` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_account` int(10) unsigned NOT NULL,
  `id_server` tinyint(3) unsigned NOT NULL,
  `path` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_account` (`id_account`),
  UNIQUE KEY `path` (`path`),
  KEY `FK_account_photo_small_server` (`id_server`),
  CONSTRAINT `FK_account_photo_small_account` FOREIGN KEY (`id_account`) REFERENCES `account` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_account_photo_small_server` FOREIGN KEY (`id_server`) REFERENCES `server` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.account_role
CREATE TABLE IF NOT EXISTS `account_role` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_account` int(10) unsigned NOT NULL,
  `id_role` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_account_id_role` (`id_account`,`id_role`),
  KEY `FK_account_role_role` (`id_role`),
  CONSTRAINT `FK_account_role_account` FOREIGN KEY (`id_account`) REFERENCES `account` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_account_role_role` FOREIGN KEY (`id_role`) REFERENCES `role` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.account_status
CREATE TABLE IF NOT EXISTS `account_status` (
  `id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `comment` varchar(4096) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.account_status_log
CREATE TABLE IF NOT EXISTS `account_status_log` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_account` int(10) unsigned NOT NULL,
  `id_account_status` tinyint(3) unsigned NOT NULL,
  `date` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_account_status_log_account` (`id_account`),
  KEY `FK_account_status_log_account_status` (`id_account_status`),
  CONSTRAINT `FK_account_status_log_account` FOREIGN KEY (`id_account`) REFERENCES `account` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_account_status_log_account_status` FOREIGN KEY (`id_account_status`) REFERENCES `account_status` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.address
CREATE TABLE IF NOT EXISTS `address` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `id_city` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_id_city` (`name`,`id_city`),
  KEY `FK_address_city` (`id_city`),
  CONSTRAINT `FK_address_city` FOREIGN KEY (`id_city`) REFERENCES `city` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=100001 DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.author
CREATE TABLE IF NOT EXISTS `author` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(1024) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6202575 DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.book
CREATE TABLE IF NOT EXISTS `book` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `key` varchar(50) NOT NULL,
  `name` varchar(1024) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `key` (`key`),
  KEY `name` (`name`(10))
) ENGINE=InnoDB AUTO_INCREMENT=79922336 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.book_author
CREATE TABLE IF NOT EXISTS `book_author` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_book` int(10) unsigned NOT NULL,
  `id_author` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_book_id_author` (`id_book`,`id_author`),
  KEY `FK_book_author_author` (`id_author`),
  CONSTRAINT `FK_book_author_author` FOREIGN KEY (`id_author`) REFERENCES `author` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_book_author_book` FOREIGN KEY (`id_book`) REFERENCES `book` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=77473760 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.book_edition
CREATE TABLE IF NOT EXISTS `book_edition` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_book` int(10) unsigned NOT NULL,
  `id_language` int(10) unsigned NOT NULL,
  `publish_date` varchar(50) DEFAULT NULL,
  `id_publisher` int(10) unsigned NOT NULL,
  `number_of_pages` int(10) unsigned NOT NULL,
  `description` varchar(4096) DEFAULT NULL,
  `count` int(10) unsigned NOT NULL,
  `date_of_addition` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_book_id_language_id_publisher` (`id_book`,`id_language`,`id_publisher`),
  KEY `FK_book_edition_language` (`id_language`),
  KEY `FK_book_edition_publisher` (`id_publisher`),
  CONSTRAINT `FK_book_edition_book` FOREIGN KEY (`id_book`) REFERENCES `book` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_book_edition_language` FOREIGN KEY (`id_language`) REFERENCES `language` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_book_edition_publisher` FOREIGN KEY (`id_publisher`) REFERENCES `publisher` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=72734260 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.book_edition_page
CREATE TABLE IF NOT EXISTS `book_edition_page` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_book_edition` int(10) unsigned NOT NULL,
  `id_server` tinyint(3) unsigned NOT NULL,
  `number_of_page` int(10) unsigned NOT NULL,
  `path` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_book_edition_number_of_page` (`id_book_edition`,`number_of_page`),
  UNIQUE KEY `path` (`path`),
  KEY `FK_book_edition_page_server` (`id_server`),
  CONSTRAINT `FK_book_edition_page_book_edition` FOREIGN KEY (`id_book_edition`) REFERENCES `book_edition` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_book_edition_page_server` FOREIGN KEY (`id_server`) REFERENCES `server` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.book_edition_status
CREATE TABLE IF NOT EXISTS `book_edition_status` (
  `id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `comment` varchar(4096) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.book_genre
CREATE TABLE IF NOT EXISTS `book_genre` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_book` int(10) unsigned NOT NULL,
  `id_genre` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_book_id_genre` (`id_book`,`id_genre`),
  KEY `FK_book_genre_genre` (`id_genre`),
  CONSTRAINT `FK_book_genre_book` FOREIGN KEY (`id_book`) REFERENCES `book` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_book_genre_genre` FOREIGN KEY (`id_genre`) REFERENCES `genre` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4850008 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.book_image_large
CREATE TABLE IF NOT EXISTS `book_image_large` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_book` int(10) unsigned NOT NULL,
  `id_server` tinyint(3) unsigned NOT NULL,
  `path` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `path` (`path`),
  UNIQUE KEY `id_book` (`id_book`),
  KEY `FK_book_image_large_server` (`id_server`),
  CONSTRAINT `FK_book_image_large_book` FOREIGN KEY (`id_book`) REFERENCES `book` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_book_image_large_server` FOREIGN KEY (`id_server`) REFERENCES `server` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5358427 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.book_image_medium
CREATE TABLE IF NOT EXISTS `book_image_medium` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_book` int(10) unsigned NOT NULL,
  `id_server` tinyint(3) unsigned NOT NULL,
  `path` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `path` (`path`),
  UNIQUE KEY `id_book` (`id_book`),
  KEY `FK_book_image_medium_server` (`id_server`),
  CONSTRAINT `FK_book_image_medium_book` FOREIGN KEY (`id_book`) REFERENCES `book` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_book_image_medium_server` FOREIGN KEY (`id_server`) REFERENCES `server` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5358427 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.book_image_small
CREATE TABLE IF NOT EXISTS `book_image_small` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_book` int(10) unsigned NOT NULL,
  `id_server` tinyint(3) unsigned NOT NULL,
  `path` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `path` (`path`),
  UNIQUE KEY `id_book` (`id_book`),
  KEY `FK_book_image_small_server` (`id_server`),
  CONSTRAINT `FK_book_image_small_book` FOREIGN KEY (`id_book`) REFERENCES `book` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_book_image_small_server` FOREIGN KEY (`id_server`) REFERENCES `server` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5358427 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.city
CREATE TABLE IF NOT EXISTS `city` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `id_country` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_id_country` (`name`,`id_country`),
  KEY `FK_city_country` (`id_country`),
  CONSTRAINT `FK_city_country` FOREIGN KEY (`id_country`) REFERENCES `country` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=19469 DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.comment
CREATE TABLE IF NOT EXISTS `comment` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_account` int(10) unsigned NOT NULL,
  `id_book_edition` int(10) unsigned NOT NULL,
  `content` varchar(4096) NOT NULL,
  `date` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_account_id_book_edition` (`id_account`,`id_book_edition`),
  KEY `FK_comment_book_edition` (`id_book_edition`),
  CONSTRAINT `FK_comment_account` FOREIGN KEY (`id_account`) REFERENCES `account` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_comment_book_edition` FOREIGN KEY (`id_book_edition`) REFERENCES `book_edition` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.country
CREATE TABLE IF NOT EXISTS `country` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.course
CREATE TABLE IF NOT EXISTS `course` (
  `id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.faculty
CREATE TABLE IF NOT EXISTS `faculty` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `id_university` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_id_university` (`name`,`id_university`),
  KEY `FK_faculty_university` (`id_university`),
  CONSTRAINT `FK_faculty_university` FOREIGN KEY (`id_university`) REFERENCES `university` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.favorite
CREATE TABLE IF NOT EXISTS `favorite` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_account` int(10) unsigned NOT NULL,
  `id_book_edition` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_account_id_book_edition` (`id_account`,`id_book_edition`),
  KEY `FK_favorite_book_edition` (`id_book_edition`),
  CONSTRAINT `FK_favorite_account` FOREIGN KEY (`id_account`) REFERENCES `account` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_favorite_book_edition` FOREIGN KEY (`id_book_edition`) REFERENCES `book_edition` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.gender
CREATE TABLE IF NOT EXISTS `gender` (
  `id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.genre
CREATE TABLE IF NOT EXISTS `genre` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL,
  `comment` varchar(4096) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=7943 DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.given_out
CREATE TABLE IF NOT EXISTS `given_out` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_account` int(10) unsigned NOT NULL,
  `id_book_edition` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_account_id_book_edition` (`id_account`,`id_book_edition`),
  KEY `FK_given_out_book_edition` (`id_book_edition`),
  CONSTRAINT `FK_given_out_account` FOREIGN KEY (`id_account`) REFERENCES `account` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_given_out_book_edition` FOREIGN KEY (`id_book_edition`) REFERENCES `book_edition` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.given_out_log
CREATE TABLE IF NOT EXISTS `given_out_log` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_account` int(10) unsigned NOT NULL,
  `id_book_edition` int(10) unsigned NOT NULL,
  `id_book_edition_status` tinyint(3) unsigned NOT NULL,
  `date` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_given_out_log_account` (`id_account`),
  KEY `FK_given_out_log_book_edition` (`id_book_edition`),
  KEY `FK_given_out_log_book_edition_status` (`id_book_edition_status`),
  CONSTRAINT `FK_given_out_log_account` FOREIGN KEY (`id_account`) REFERENCES `account` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_given_out_log_book_edition` FOREIGN KEY (`id_book_edition`) REFERENCES `book_edition` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_given_out_log_book_edition_status` FOREIGN KEY (`id_book_edition_status`) REFERENCES `book_edition_status` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.group
CREATE TABLE IF NOT EXISTS `group` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `id_faculty` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_id_faculty` (`name`,`id_faculty`),
  KEY `FK_group_faculty` (`id_faculty`),
  CONSTRAINT `FK_group_faculty` FOREIGN KEY (`id_faculty`) REFERENCES `faculty` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.keyword
CREATE TABLE IF NOT EXISTS `keyword` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_account` int(10) unsigned NOT NULL,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_account_name` (`id_account`,`name`),
  CONSTRAINT `FK_keyword_account` FOREIGN KEY (`id_account`) REFERENCES `account` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.language
CREATE TABLE IF NOT EXISTS `language` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(3) NOT NULL,
  `full_name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `full_name` (`full_name`)
) ENGINE=InnoDB AUTO_INCREMENT=466 DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.last_read_page
CREATE TABLE IF NOT EXISTS `last_read_page` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_account` int(10) unsigned NOT NULL,
  `id_book_edition` int(10) unsigned NOT NULL,
  `number_of_page` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_account_id_book_edition` (`id_account`,`id_book_edition`),
  KEY `FK_last_read_page_book_edition` (`id_book_edition`),
  CONSTRAINT `FK_last_read_page_account` FOREIGN KEY (`id_account`) REFERENCES `account` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_last_read_page_book_edition` FOREIGN KEY (`id_book_edition`) REFERENCES `book_edition` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.notification_of_new_book_edition
CREATE TABLE IF NOT EXISTS `notification_of_new_book_edition` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_book_edition` int(10) unsigned NOT NULL,
  `id_keyword` int(10) unsigned NOT NULL,
  `is_seen` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_book_edition_id_keyword` (`id_book_edition`,`id_keyword`),
  KEY `FK_notification_of_new_book_edition_keyword` (`id_keyword`),
  CONSTRAINT `FK_notification_of_new_book_edition_book_edition` FOREIGN KEY (`id_book_edition`) REFERENCES `book_edition` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_notification_of_new_book_edition_keyword` FOREIGN KEY (`id_keyword`) REFERENCES `keyword` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.publisher
CREATE TABLE IF NOT EXISTS `publisher` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(1024) NOT NULL,
  `number` varchar(50) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `number` (`number`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=3220327 DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.rating
CREATE TABLE IF NOT EXISTS `rating` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_account` int(10) unsigned NOT NULL,
  `id_book_edition` int(10) unsigned NOT NULL,
  `value` enum('1','2','3','4','5') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_account_id_book_edition` (`id_account`,`id_book_edition`),
  KEY `FK_raiting_book_edition` (`id_book_edition`),
  CONSTRAINT `FK_raiting_account` FOREIGN KEY (`id_account`) REFERENCES `account` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_raiting_book_edition` FOREIGN KEY (`id_book_edition`) REFERENCES `book_edition` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.receipt_book
CREATE TABLE IF NOT EXISTS `receipt_book` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `key` varchar(50) NOT NULL,
  `name` varchar(1024) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `key` (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.receipt_book_author
CREATE TABLE IF NOT EXISTS `receipt_book_author` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_book` int(10) unsigned NOT NULL,
  `id_author` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_book_id_author` (`id_book`,`id_author`),
  KEY `FK_receipt_book_author_author` (`id_author`),
  CONSTRAINT `FK_receipt_book_author_author` FOREIGN KEY (`id_author`) REFERENCES `author` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_receipt_book_author_receipt_book` FOREIGN KEY (`id_book`) REFERENCES `receipt_book` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.receipt_book_edition
CREATE TABLE IF NOT EXISTS `receipt_book_edition` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_book` int(10) unsigned NOT NULL,
  `id_language` int(10) unsigned NOT NULL,
  `publish_date` varchar(50) DEFAULT NULL,
  `id_publisher` int(10) unsigned NOT NULL,
  `number_of_pages` int(10) unsigned NOT NULL,
  `description` varchar(4096) DEFAULT NULL,
  `count` int(10) unsigned NOT NULL,
  `date_of_addition` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_book_id_language_id_publisher` (`id_book`,`id_language`,`id_publisher`),
  KEY `FK_receipt_book_edition_language` (`id_language`),
  KEY `FK_receipt_book_edition_publisher` (`id_publisher`),
  CONSTRAINT `FK_receipt_book_edition_language` FOREIGN KEY (`id_language`) REFERENCES `language` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_receipt_book_edition_publisher` FOREIGN KEY (`id_publisher`) REFERENCES `publisher` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_receipt_book_edition_receipt_book` FOREIGN KEY (`id_book`) REFERENCES `receipt_book` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.receipt_book_edition_page
CREATE TABLE IF NOT EXISTS `receipt_book_edition_page` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_book_edition` int(10) unsigned NOT NULL,
  `id_server` tinyint(3) unsigned NOT NULL,
  `number_of_page` int(10) unsigned NOT NULL,
  `path` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_book_edition_number_of_page` (`id_book_edition`,`number_of_page`),
  UNIQUE KEY `path` (`path`),
  KEY `FK_receipt_book_edition_page_server` (`id_server`),
  CONSTRAINT `FK_receipt_book_edition_page_receipt_book_edition` FOREIGN KEY (`id_book_edition`) REFERENCES `receipt_book_edition` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_receipt_book_edition_page_server` FOREIGN KEY (`id_server`) REFERENCES `server` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.receipt_book_genre
CREATE TABLE IF NOT EXISTS `receipt_book_genre` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_book` int(10) unsigned NOT NULL,
  `id_genre` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_book_id_genre` (`id_book`,`id_genre`),
  KEY `FK_receipt_book_genre_genre` (`id_genre`),
  CONSTRAINT `FK_receipt_book_genre_genre` FOREIGN KEY (`id_genre`) REFERENCES `genre` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_receipt_book_genre_receipt_book` FOREIGN KEY (`id_book`) REFERENCES `receipt_book` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.receipt_book_image_large
CREATE TABLE IF NOT EXISTS `receipt_book_image_large` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_book` int(10) unsigned NOT NULL,
  `id_server` tinyint(3) unsigned NOT NULL,
  `path` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `path` (`path`),
  UNIQUE KEY `id_book` (`id_book`),
  KEY `FK_receipt_book_image_large_server` (`id_server`),
  CONSTRAINT `FK_receipt_book_image_large_receipt_book` FOREIGN KEY (`id_book`) REFERENCES `receipt_book` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_receipt_book_image_large_server` FOREIGN KEY (`id_server`) REFERENCES `server` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.receipt_book_image_medium
CREATE TABLE IF NOT EXISTS `receipt_book_image_medium` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_book` int(10) unsigned NOT NULL,
  `id_server` tinyint(3) unsigned NOT NULL,
  `path` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `path` (`path`),
  UNIQUE KEY `id_book` (`id_book`),
  KEY `FK_receipt_book_image_medium_server` (`id_server`),
  CONSTRAINT `FK_receipt_book_image_medium_receipt_book` FOREIGN KEY (`id_book`) REFERENCES `receipt_book` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_receipt_book_image_medium_server` FOREIGN KEY (`id_server`) REFERENCES `server` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.receipt_book_image_small
CREATE TABLE IF NOT EXISTS `receipt_book_image_small` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_book` int(10) unsigned NOT NULL,
  `id_server` tinyint(3) unsigned NOT NULL,
  `path` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `path` (`path`),
  UNIQUE KEY `id_book` (`id_book`),
  KEY `FK_receipt_book_image_small_server` (`id_server`),
  CONSTRAINT `FK_receipt_book_image_small_receipt_book` FOREIGN KEY (`id_book`) REFERENCES `receipt_book` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `FK_receipt_book_image_small_server` FOREIGN KEY (`id_server`) REFERENCES `server` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.role
CREATE TABLE IF NOT EXISTS `role` (
  `id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `comment` varchar(4096) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.server
CREATE TABLE IF NOT EXISTS `server` (
  `id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `ipv4` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `ipv4` (`ipv4`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для таблица library.university
CREATE TABLE IF NOT EXISTS `university` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Экспортируемые данные не выделены.
-- Дамп структуры для триггер library.account_after_insert
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `account_after_insert` AFTER INSERT ON `account` FOR EACH ROW BEGIN
	INSERT INTO `account_status_log` (`id_account`, `id_account_status`, `date`) VALUES (NEW.`id`, (SELECT `id` FROM `account_status` WHERE `name` LIKE 'registered'), NOW());
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- Дамп структуры для триггер library.account_after_update
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `account_after_update` AFTER UPDATE ON `account` FOR EACH ROW BEGIN
	INSERT INTO `account_status_log` (`id_account`, `id_account_status`, `date`) VALUES (NEW.`id`, NEW.`id_account_status`, NOW());
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- Дамп структуры для триггер library.given_out_after_delete
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `given_out_after_delete` AFTER DELETE ON `given_out` FOR EACH ROW BEGIN
	INSERT INTO `given_out_log` (`id_account`, `id_book_edition`, `id_book_edition_status`, `date`) VALUES (OLD.`id_account`, OLD.`id_book_edition`, (SELECT `id` FROM `book_edition_status` WHERE `name` LIKE 'returned by the user'), NOW());
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- Дамп структуры для триггер library.given_out_after_insert
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `given_out_after_insert` AFTER INSERT ON `given_out` FOR EACH ROW BEGIN
	INSERT INTO `given_out_log` (`id_account`, `id_book_edition`, `id_book_edition_status`, `date`) VALUES (NEW.`id_account`, NEW.`id_book_edition`, (SELECT `id` FROM `book_edition_status` WHERE `name` LIKE 'given to the user'), NOW());
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- Дамп структуры для триггер library.given_out_before_insert
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `given_out_before_insert` BEFORE INSERT ON `given_out` FOR EACH ROW BEGIN
	SET @used_count = (SELECT COUNT(*) FROM `given_out` WHERE `id_book_edition` = NEW.`id_book_edition`);
	SET @fixed_count = (SELECT `count` FROM `book_edition` WHERE `id` = NEW.`id_book_edition`);
	
	IF (@used_count = @fixed_count)
	THEN
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Books ran out';
	END IF;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
