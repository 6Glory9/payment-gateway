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

 Date: 18/04/2026 11:57:44
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_instruction_log_ext
-- ----------------------------
DROP TABLE IF EXISTS `t_instruction_log_ext`;
CREATE TABLE `t_instruction_log_ext`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商户编号',
  `upstream_request_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上游请求单号',
  `instruction_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '指令类型(枚举)',
  `raw_req_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '原始请求报文',
  `raw_res_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '原始响应报文',
  `raw_channel_request_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '渠道原始请求报文',
  `raw_channel_response_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '渠道原始响应报文',
  `create_time` datetime(6) NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
  `update_time` datetime(6) NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
  `channel_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道服务号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_upstream_request_no`(`upstream_request_no`) USING BTREE,
  INDEX `idx_merchant_no`(`merchant_no`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '指令请求日志表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
