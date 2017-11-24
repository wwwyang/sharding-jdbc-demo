# sharding-jdbc-demo
Java-Mysql分库分表的实现 shardingJDBC

一.版本
1.jdk1.7.0_17
2.STS(Spring Tool Suite [Version: 3.9.1.RELEASE])
3.Tomcat8

二.拥有的功能
1.当当的Sharding-JDBC分库分表框架.
2.能够进行分库分表的增删改查操作
3.sql脚本
create database sharding_0;
create database sharding_1;
create database sharding_2;
create database sharding_3;

#每个库中分别执行下面建表语句, 总共执行四遍:

USE `sharding_0`;

CREATE TABLE `t_order_0` (
  `order_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `t_order_1` (
  `order_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `t_order_item_0` (
  `item_id` int(11) NOT NULL,
  `order_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `t_order_item_1` (
  `item_id` int(11) NOT NULL,
  `order_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

USE `sharding_1`;
USE `sharding_2`;
USE `sharding_3`;