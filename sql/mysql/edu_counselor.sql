-- ======================================================================
-- 导员管理模块（Edu Counselor）初始化 SQL
-- ----------------------------------------------------------------------
-- 适用数据库：MySQL 8.0+
-- 包含：
--   1. edu_counselor        导员档案表
--   2. edu_counselor_class  导员-班级关系表（class_id 唯一 => 一个班级一个主辅导员）
--   3. edu_class            班级共享参考数据表（正式管理归属「年级管理」模块，本模块仅引用校验）
-- 说明：
--   - 请先执行 ruoyi-vue-pro.sql 完成框架基础数据（system_menu 等）初始化
--   - 本脚本可重复执行（建表前先 DROP，菜单按 id 去重）
-- ======================================================================

-- ---------- 班级共享参考数据表（归属年级管理，导员模块仅引用） ----------
DROP TABLE IF EXISTS `edu_class`;
CREATE TABLE `edu_class` (
    `id`            bigint      NOT NULL AUTO_INCREMENT COMMENT '班级编号',
    `name`          varchar(50) NOT NULL COMMENT '班级名称',
    `grade_id`      bigint      DEFAULT NULL COMMENT '所属年级编号（关联 edu_grade.id）',
    `major_id`      bigint      DEFAULT NULL COMMENT '所属专业编号',
    `department_id` bigint      DEFAULT NULL COMMENT '所属院系编号（关联 system_dept.id）',
    `status`        tinyint     NOT NULL DEFAULT 0 COMMENT '状态：0 开启 / 1 关闭（CommonStatusEnum）',
    `creator`       varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time`   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`       varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time`   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       bit(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`     bigint      NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    KEY `idx_grade_id` (`grade_id`),
    KEY `idx_department_id` (`department_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学院管理-班级共享参考表';

-- 示例班级数据（用于联调；正式班级数据由年级管理模块维护）
INSERT INTO `edu_class` (`id`, `name`, `grade_id`, `department_id`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
VALUES (1, '2023级软件工程1班', 1, 100, 0, 'admin', NOW(), 'admin', NOW(), b'0', 1),
       (2, '2023级软件工程2班', 1, 100, 0, 'admin', NOW(), 'admin', NOW(), b'0', 1),
       (3, '2024级计算机科学1班', 2, 100, 0, 'admin', NOW(), 'admin', NOW(), b'0', 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- ---------- 导员档案表 ----------
DROP TABLE IF EXISTS `edu_counselor`;
CREATE TABLE `edu_counselor` (
    `id`            bigint       NOT NULL AUTO_INCREMENT COMMENT '导员档案编号',
    `code`          varchar(20)  NOT NULL COMMENT '工号',
    `name`          varchar(30)  NOT NULL COMMENT '姓名',
    `mobile`        varchar(20)  NOT NULL COMMENT '联系方式',
    `title`         varchar(30)  NOT NULL DEFAULT '' COMMENT '职称',
    `department_id` bigint       DEFAULT NULL COMMENT '所属院系编号（关联 system_dept.id）',
    `user_id`       bigint       DEFAULT NULL COMMENT '同步创建的系统登录账号编号（system_users.id）',
    `status`        tinyint      NOT NULL DEFAULT 0 COMMENT '状态：0 开启 / 1 关闭（CommonStatusEnum）',
    `remark`        varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
    `creator`       varchar(64)  DEFAULT '' COMMENT '创建者',
    `create_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`       varchar(64)  DEFAULT '' COMMENT '更新者',
    `update_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`     bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    UNIQUE KEY `uk_mobile` (`mobile`),
    KEY `idx_department_id` (`department_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学院管理-导员档案表';

-- ---------- 导员-班级关系表 ----------
DROP TABLE IF EXISTS `edu_counselor_class`;
CREATE TABLE `edu_counselor_class` (
    `id`            bigint    NOT NULL AUTO_INCREMENT COMMENT '关系编号',
    `counselor_id`  bigint    NOT NULL COMMENT '导员档案编号（关联 edu_counselor.id）',
    `class_id`      bigint    NOT NULL COMMENT '班级编号（关联 edu_class.id），唯一 => 主辅导员',
    `sort`          int       NOT NULL DEFAULT 0 COMMENT '排序',
    `creator`       varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time`   datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`       varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time`   datetime  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       bit(1)    NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`     bigint    NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_class_id` (`class_id`),
    KEY `idx_counselor_id` (`counselor_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学院管理-导员班级关系表';

-- ======================================================================
-- 菜单数据（edu 导员模块）
-- 目录 id 从 14000 开始（避免与既有模块冲突）
-- ======================================================================
-- 目录：学院管理
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (14000, '学院管理', '', 1, 70, 0, '/edu', 'ep:school', NULL, NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '1', NOW(), b'0');

-- 菜单：导员管理
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (14001, '导员管理', 'edu:counselor:query', 2, 1, 14000, 'counselor', 'ep:user-filled', 'edu/counselor/index', 'EduCounselor', 0, b'1', b'1', b'1', 'admin', NOW(), '1', NOW(), b'0');
-- 按钮：导员管理
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (140011, '导员查询', 'edu:counselor:query', 3, 1, 14001, '', '', NULL, NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '1', NOW(), b'0'),
       (140012, '导员新增', 'edu:counselor:create', 3, 2, 14001, '', '', NULL, NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '1', NOW(), b'0'),
       (140013, '导员修改', 'edu:counselor:update', 3, 3, 14001, '', '', NULL, NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '1', NOW(), b'0'),
       (140014, '导员分配班级', 'edu:counselor:update', 3, 4, 14001, '', '', NULL, NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '1', NOW(), b'0'),
       (140015, '导员删除', 'edu:counselor:delete', 3, 5, 14001, '', '', NULL, NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '1', NOW(), b'0');

-- ======================================================================
-- 字典数据（导员状态）
-- ======================================================================
INSERT INTO `system_dict_type` (`id`, `name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `deleted_time`)
VALUES (2100, '导员状态', 'edu_counselor_status', 0, '导员状态：开启 / 关闭', 'admin', NOW(), '1', NOW(), b'0', NULL);

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (3100, 0, '开启', '0', 'edu_counselor_status', 0, 'success', '', '导员开启', 'admin', NOW(), '1', NOW(), b'0'),
       (3101, 1, '关闭', '1', 'edu_counselor_status', 0, 'info', '', '导员关闭', 'admin', NOW(), '1', NOW(), b'0');