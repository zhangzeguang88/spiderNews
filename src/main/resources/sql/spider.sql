/*
Navicat MySQL Data Transfer

Source Server         : con_zzg
Source Server Version : 50513
Source Host           : localhost:3306
Source Database       : spider

Target Server Type    : MYSQL
Target Server Version : 50513
File Encoding         : 65001

Date: 2018-01-30 09:02:38
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `appointment`
-- ----------------------------
DROP TABLE IF EXISTS `appointment`;
CREATE TABLE `appointment` (
  `book_id` bigint(20) NOT NULL COMMENT '图书ID',
  `student_id` bigint(20) NOT NULL COMMENT '学号',
  `appoint_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '预约时间',
  PRIMARY KEY (`book_id`,`student_id`),
  KEY `idx_appoint_time` (`appoint_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='预约图书表';

-- ----------------------------
-- Records of appointment
-- ----------------------------
INSERT INTO `appointment` VALUES ('1000', '12345678910', '2018-01-27 23:01:26');
INSERT INTO `appointment` VALUES ('1001', '12345678910', '2018-01-27 23:52:12');
INSERT INTO `appointment` VALUES ('1001', '12345678911', '2018-01-28 00:01:30');
INSERT INTO `appointment` VALUES ('1003', '1234567890', '2018-01-28 21:29:47');

-- ----------------------------
-- Table structure for `book`
-- ----------------------------
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book` (
  `book_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '图书ID',
  `name` varchar(100) NOT NULL COMMENT '图书名称',
  `number` int(11) NOT NULL COMMENT '馆藏数量',
  PRIMARY KEY (`book_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1004 DEFAULT CHARSET=utf8 COMMENT='图书表';

-- ----------------------------
-- Records of book
-- ----------------------------
INSERT INTO `book` VALUES ('1000', 'Java程序设计', '7');
INSERT INTO `book` VALUES ('1001', '数据结构', '7');
INSERT INTO `book` VALUES ('1002', '设计模式', '10');
INSERT INTO `book` VALUES ('1003', '编译原理', '9');

-- ----------------------------
-- Table structure for `crawl_html`
-- ----------------------------
DROP TABLE IF EXISTS `crawl_html`;
CREATE TABLE `crawl_html` (
  `id` bigint(20) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `url` varchar(50) DEFAULT NULL,
  `title` varchar(50) DEFAULT NULL COMMENT '标题',
  `content` text COMMENT '正文内容',
  `source` varchar(50) DEFAULT NULL COMMENT '来源',
  `publish_time` varchar(50) DEFAULT NULL,
  `html` text,
  `update_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=256 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of crawl_html
-- ----------------------------
INSERT INTO `crawl_html` VALUES ('00000000000000000255', 'url2', '123test', 'addr', '2017-11-19 11:32:00', null, null, null, null);
