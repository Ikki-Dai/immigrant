/******************************************/
/*   DatabaseName = immigrant   */
/*   TableName = subject   */
/******************************************/
CREATE TABLE IF NOT EXISTS `subject`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '自增长',
    `uid`         bigint       NOT NULL DEFAULT 0 COMMENT '账号唯一ID',
    `username`    varchar(128) NOT NULL DEFAULT '',
    `phone`       varchar(128) NOT NULL DEFAULT '',
    `email`       varchar(256) NOT NULL DEFAULT '',
    `valid`       varchar(16)  NOT NULL DEFAULT '',
    `available`   int          NOT NULL DEFAULT 0 COMMENT '0:unknown; 1:common; 2:freeze; 3:ban',
    `description` varchar(64)  NOT NULL DEFAULT '',
    `signup_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

CREATE INDEX IF NOT EXISTS `idx_subject_uname` ON `subject` (`username`);
CREATE INDEX IF NOT EXISTS `idx_subject_email` ON `subject` (`email`);
CREATE INDEX IF NOT EXISTS `idx_subject_phone` ON `subject` (`phone`);

CREATE TABLE IF NOT EXISTS `credentials`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '自增长',
    `uid`         bigint       NOT NULL DEFAULT 0 COMMENT '账号唯一ID',
    `credential`  varchar(512) NOT NULL DEFAULT '',
    `identifier`  varchar(64)  NOT NULL DEFAULT '',
    `alias`       varchar(32)  NOT NULL DEFAULT '',
    `type`        int          NOT NULL DEFAULT 0 COMMENT '0:UNKNOWN, 1:PASSWORD, 2:FIDO, 3:TOTP',
    `signup_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

CREATE INDEX IF NOT EXISTS `idx_credentials_uid` ON `credentials` (`uid`);

CREATE TABLE IF NOT EXISTS `customer`
(
    `id`            int         NOT NULL AUTO_INCREMENT,
    `customer_name` varchar(64) NOT NULL DEFAULT ''
);