CREATE TABLE `zstack`.`TokenBucket`
(
    `id`      int primary key AUTO_INCREMENT,
    `apiName` varchar(100) NOT NULL,
    `total`   double       NOT NULL,
    `rate`    double       NOT NULL,
    `time`    long         NOT NULL,
    `nowSize` double       NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;