CREATE TABLE `zstack`.`APIRateLimitVO`
(
    `id`    int primary key AUTO_INCREMENT,
    `api`   varchar(100) NOT NULL,
    `token` int          NOT NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8;
