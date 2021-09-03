/******************************************/
/*   DatabaseName = immigrant   */
/*   TableName = subject   */
/******************************************/
CREATE TABLE `subject`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '自增长',
    `uid`         bigint       NOT NULL DEFAULT '0' COMMENT '账号唯一ID',
    `phone`       varchar(128) NOT NULL DEFAULT '',
    `email`       varchar(256) NOT NULL DEFAULT '',
    `status`      smallint     NOT NULL DEFAULT '0' COMMENT '0:common;1：freeze;2:ban',
    `description` varchar(64)  NOT NULL DEFAULT '',
    `signup_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
    COMMENT ='账号主体'
;
