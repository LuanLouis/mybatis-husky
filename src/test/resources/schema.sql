drop table if exists t_sharding_table;
create table if not exists t_sharding_table
(
	SHARDING_ID int auto_increment primary key,
	SHARDING_NAME varchar(100) null comment '分表名称',
	TABLE_NAME varchar(100) null comment '表名',
	STRATEGY int null comment '替换策略，0：表达式匹配，返回静态分表值；1：表达式匹配，返回动态分表值，这种方式分表参数作为POSTFIX;2:完全自定义实现',
	EXPRESSION varchar(1024) null comment '正则表达式，按照逗号隔开，满足条件，则使用此分表策略',
	DELIMITER varchar(20) default '_' null comment '分隔符,默认下划线',
	POSTFIX varchar(100) null comment '后缀匹配值',
	CUSTOM_IMPL varchar(2048) null comment '自定义策略实现类，指向class完全类名，自动初始化和实例化，当strategy = 2 时，此处有值',
	PRECEDENCE int null comment '匹配优先级，值越小，标识优先级越大'
);

drop table if exists sale_order;
create table if not exists sale_order
(
	ORDER_ID varchar(20)  not null primary key ,
	TENANT_ID varchar(10) null comment '租户编号',
	SKU varchar(40) null comment '商品SKU编码',
	COMMODITY_NAME varchar(200) null comment '商品名称',
	BUYER varchar(100) null comment '购买者',
	ORDER_TIME datetime(6) null comment '下单时间',
	AMOUNT decimal(6,3) null comment '金额'
)
;

COMMENT ON TABLE sale_order IS '销售订单表';

CREATE INDEX idx_sale_order ON sale_order(ORDER_ID,BUYER);