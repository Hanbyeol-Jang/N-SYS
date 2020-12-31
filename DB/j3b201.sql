-- --------------------------------------------------------
-- 호스트:                          j3b201.p.ssafy.io
-- 서버 버전:                        10.5.5-MariaDB-1:10.5.5+maria~focal - mariadb.org binary distribution
-- 서버 OS:                        debian-linux-gnu
-- HeidiSQL 버전:                  11.0.0.5919
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- j3b201 데이터베이스 구조 내보내기
CREATE DATABASE IF NOT EXISTS `j3b201` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
USE `j3b201`;

-- 테이블 j3b201.Game 구조 내보내기
CREATE TABLE IF NOT EXISTS `Game` (		-- 게임 정보
  `g_id` int(11) NOT NULL AUTO_INCREMENT,
  `g_title` varchar(100) NOT NULL,
  PRIMARY KEY (`g_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 j3b201.Play 구조 내보내기
CREATE TABLE IF NOT EXISTS `Play` (			-- 승부 정보
  `u_pk` int(11) NOT NULL,
  `g_id` int(11) NOT NULL,
  `p_level` int(11) NOT NULL,
  `p_victory` int(11) NOT NULL,
  `p_score` int(11) NOT NULL,
  `p_pk` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`p_pk`),
  KEY `Play_FK` (`u_pk`),
  KEY `Play_FK_1` (`g_id`),
  CONSTRAINT `Play_FK` FOREIGN KEY (`u_pk`) REFERENCES `Userinfo` (`u_pk`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `Play_FK_1` FOREIGN KEY (`g_id`) REFERENCES `Game` (`g_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 j3b201.Userinfo 구조 내보내기
CREATE TABLE IF NOT EXISTS `Userinfo` (		-- 사용자 정보
  `u_pk` int(11) NOT NULL AUTO_INCREMENT,
  `u_id` varchar(100) NOT NULL,
  `u_pw` varchar(100) NOT NULL,
  PRIMARY KEY (`u_pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 내보낼 데이터가 선택되어 있지 않습니다.

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
