DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
                        `id` BIGINT AUTO_INCREMENT COMMENT 'ID;大于0' ,
                        `username` VARCHAR(64) NOT NULL  COMMENT '用户名' ,
                        `name` VARCHAR(32) NOT NULL  COMMENT '姓名' ,
                        `email` VARCHAR(64)   COMMENT '邮箱' ,
                        `phone` VARCHAR(32)   COMMENT '电话' ,
                        `enabled` TINYINT NOT NULL  COMMENT '是否启用' ,
                        `expires` DATETIME   COMMENT '过期时间' ,
                        `creation_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' ,
                        `modification_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间' ,
                        `created_by` BIGINT   COMMENT '创建人' ,
                        `modified_by` BIGINT   COMMENT '修改人' ,
                        `deleted` BIGINT NOT NULL DEFAULT 0 COMMENT '是否删除;删除时写入ID' ,
                        PRIMARY KEY (id)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT = '用户表';
DROP TABLE IF EXISTS `user_password`;
CREATE TABLE `user_password` (
                                 `id` BIGINT AUTO_INCREMENT COMMENT 'ID;大于0' ,
                                 `user_id` BIGINT NOT NULL  COMMENT '用户ID' ,
                                 `password` VARCHAR(128)   COMMENT '密码' ,
                                 `creation_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' ,
                                 `modification_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间' ,
                                 `created_by` BIGINT   COMMENT '创建人' ,
                                 `modified_by` BIGINT   COMMENT '修改人' ,
                                 `deleted` BIGINT NOT NULL DEFAULT 0 COMMENT '是否删除;删除时写入ID' ,
                                 PRIMARY KEY (id)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT = '用户密码表';
DROP TABLE IF EXISTS `organization`;
CREATE TABLE `organization` (
                                `id` BIGINT AUTO_INCREMENT COMMENT 'ID;大于0' ,
                                `code` VARCHAR(64) NOT NULL  COMMENT '组织机构码;a-zA-Z0-9' ,
                                `name` VARCHAR(255) NOT NULL  COMMENT '组织机构名称' ,
                                `parent_id` BIGINT NOT NULL  COMMENT '父组织机构ID;0表示当前节点为根节点' ,
                                `creation_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' ,
                                `modification_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间' ,
                                `created_by` BIGINT   COMMENT '创建人' ,
                                `modified_by` BIGINT   COMMENT '修改人' ,
                                `deleted` BIGINT NOT NULL DEFAULT 0 COMMENT '是否删除;删除时写入ID' ,
                                PRIMARY KEY (id)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT = '组织表';
DROP TABLE IF EXISTS `user_organization`;
CREATE TABLE `user_organization` (
                                     `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID;大于0',
                                     `user_id` bigint(20) NOT NULL COMMENT '用户ID',
                                     `organization_id` bigint(20) NOT NULL COMMENT '组织机构ID',
                                     `creation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `modification_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                     `created_by` bigint(20) DEFAULT NULL COMMENT '创建人',
                                     `modified_by` bigint(20) DEFAULT NULL COMMENT '修改人',
                                     `deleted` bigint(20) NOT NULL DEFAULT '0' COMMENT '是否删除;删除时写入ID',
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `uniq_user_organization` (`user_id`,`organization_id`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户组织结构表';
