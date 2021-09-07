/******************************************/
/*   DatabaseName = immigrant   */
/*   TableName = subject   */
/******************************************/
CREATE TABLE `subject`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '自增长',
    `uid`         bigint       NOT NULL DEFAULT 0 COMMENT '账号唯一ID',
    `username`    varchar(128) NOT NULL DEFAULT '',
    `phone`       varchar(128) NOT NULL DEFAULT '',
    `email`       varchar(256) NOT NULL DEFAULT '',
    `valid`       smallint     NOT NULL DEFAULT 0,
    `available`   smallint     NOT NULL DEFAULT 0 COMMENT '0:unknown; 1:common; 2:freeze; 3:ban',
    `description` varchar(64)  NOT NULL DEFAULT '',
    `signup_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_uname` (`username`),
    KEY `idx_email` (`email`),
    KEY `idx_phone` (`phone`)
) ENGINE = InnoDB
    COMMENT ='账号主体'
;

CREATE TABLE `credentials`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '自增长',
    `uid`         bigint       NOT NULL DEFAULT 0 COMMENT '账号唯一ID',
    `credentials` varchar(512) NOT NULL DEFAULT '',
    `identifier`  varchar(64)  NOT NULL DEFAULT '',
    `alias`       varchar(32)  NOT NULL DEFAULT '',
    `type`        smallint     NOT NULL DEFAULT 0 COMMENT '0:UNKNOWN, 1:PASSWORD, 2:FIDO, 3:TOTP',
    `signup_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB
    COMMENT ='账号凭证'
;