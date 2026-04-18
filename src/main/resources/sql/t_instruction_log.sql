/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 50744
 Source Host           : localhost:3306
 Source Schema         : scott

 Target Server Type    : MySQL
 Target Server Version : 50744
 File Encoding         : 65001

 Date: 18/04/2026 11:57:34
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_instruction_log
-- ----------------------------
DROP TABLE IF EXISTS `t_instruction_log`;
CREATE TABLE `t_instruction_log`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商户编号',
  `instruction_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '指令类型(枚举)',
  `upstream_request_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上游请求号',
  `channel_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道编号',
  `channel_biz_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道业务单号',
  `request_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求状态(枚举)',
  `biz_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '业务状态(枚举)',
  `request_time` datetime(6) NULL DEFAULT NULL COMMENT '请求时间',
  `response_time` datetime(6) NULL DEFAULT NULL COMMENT '响应时间',
  `duration` bigint(20) NULL DEFAULT NULL COMMENT '耗时(毫秒)',
  `create_time` datetime(6) NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
  `update_time` datetime(6) NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
  `channel_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道服务号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_upstream_request_no`(`upstream_request_no`) USING BTREE,
  INDEX `idx_channel_no`(`channel_no`) USING BTREE,
  INDEX `idx_channel_biz_no`(`channel_biz_no`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '渠道请求记录表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
