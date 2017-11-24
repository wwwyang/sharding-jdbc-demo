package org.study.base;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import com.dangdang.ddframe.rdb.sharding.api.ShardingDataSource;
import com.dangdang.ddframe.rdb.sharding.api.rule.BindingTableRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.DataSourceRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.ShardingRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.TableRule;
import com.dangdang.ddframe.rdb.sharding.api.strategy.database.DatabaseShardingStrategy;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.TableShardingStrategy;

/**
 * 分库分表
 *
 */
public class ShardingJdbc {
	public static void main(String[] args) throws Exception {
		Map<String, DataSource> dataSourceMap = new HashMap<String, DataSource>();

		/*
		 * 根据两个库, 分别建两个数据库连接, 并把这两个链接放到dataSourceMap中.
		 */
		dataSourceMap.put("sharding_0", createDataSource("sharding_0"));
		dataSourceMap.put("sharding_1", createDataSource("sharding_1"));
		dataSourceMap.put("sharding_2", createDataSource("sharding_2"));
		dataSourceMap.put("sharding_3", createDataSource("sharding_3"));

		// dataSource的规则, 已含所有数据库链接的map集合为参数
		DataSourceRule dataSourceRule = new DataSourceRule(dataSourceMap);

		// 分库分表的表, 第一个参数是逻辑表名, 第二个是实际表名, 第三个是实际的库.
		// 建立两个表的规则.
		TableRule orderTableRule = new TableRule("t_order", Arrays.asList("t_order_0", "t_order_1"), dataSourceRule);
		TableRule orderItemTableRule = new TableRule("t_order_item", Arrays.asList("t_order_item_0", "t_order_item_1"), dataSourceRule);

		/**
		 * DatabaseShardingStrategy 分库策略 参数一:根据哪个字段分库 参数二:分库路由函数
		 * 
		 * TableShardingStrategy 分表策略 参数一:根据哪个字段分表 参数二:分表路由函数
		 * 
		 * 创建sharding规则, 按照user_id的值的奇偶 进行分库, 按照order_id的值进行分表
		 */
		ShardingRule shardingRule = new ShardingRule(dataSourceRule, Arrays.asList(orderTableRule, orderItemTableRule),
				Arrays.asList(new BindingTableRule(Arrays.asList(orderTableRule, orderItemTableRule))),
				new DatabaseShardingStrategy("user_id", new ModuloDatabaseShardingAlgorithm()),
				new TableShardingStrategy("order_id", new ModuloTableShardingAlgorithm()));

		/**
		 * clear data;
		 */
		DataSource dataSource3 = new ShardingDataSource(shardingRule);
		Connection conn3 = dataSource3.getConnection();
		conn3.prepareStatement("delete from t_order").executeUpdate();
		/**
		 * 插入数据试试
		 */
		DataSource dataSource2 = new ShardingDataSource(shardingRule);

		Connection conn2 = dataSource2.getConnection();

		conn2.prepareStatement("insert into t_order(user_id, order_id) values(0, 0)").executeUpdate();
		conn2.prepareStatement("insert into t_order(user_id, order_id) values(0, 1)").executeUpdate();
		conn2.prepareStatement("insert into t_order(user_id, order_id) values(0, 2)").executeUpdate();
		conn2.prepareStatement("insert into t_order(user_id, order_id) values(0, 3)").executeUpdate();
		conn2.prepareStatement("insert into t_order(user_id, order_id) values(1, 0)").executeUpdate();
		conn2.prepareStatement("insert into t_order(user_id, order_id) values(1, 1)").executeUpdate();
		conn2.prepareStatement("insert into t_order(user_id, order_id) values(1, 2)").executeUpdate();
		conn2.prepareStatement("insert into t_order(user_id, order_id) values(1, 3)").executeUpdate();
		conn2.prepareStatement("insert into t_order(user_id, order_id) values(2, 0)").executeUpdate();
		conn2.prepareStatement("insert into t_order(user_id, order_id) values(2, 1)").executeUpdate();
		conn2.prepareStatement("insert into t_order(user_id, order_id) values(2, 2)").executeUpdate();
		conn2.prepareStatement("insert into t_order(user_id, order_id) values(2, 3)").executeUpdate();
		conn2.prepareStatement("insert into t_order(user_id, order_id) values(3, 0)").executeUpdate();
		conn2.prepareStatement("insert into t_order(user_id, order_id) values(3, 1)").executeUpdate();
		conn2.prepareStatement("insert into t_order(user_id, order_id) values(3, 2)").executeUpdate();
		conn2.prepareStatement("insert into t_order(user_id, order_id) values(3, 3)").executeUpdate();
		conn2.prepareStatement("insert into t_order(user_id, order_id) values(10004, 11)").executeUpdate();
		conn2.prepareStatement("insert into t_order(user_id, order_id) values(10005, 11)").executeUpdate();
		conn2.prepareStatement("insert into t_order(user_id, order_id) values(10006, 11)").executeUpdate();
		conn2.prepareStatement("insert into t_order(user_id, order_id) values(10007, 11)").executeUpdate();

		/**
		 * 查询数据试试
		 */
		DataSource dataSource = new ShardingDataSource(shardingRule);
		String sql = "SELECT o.user_id, o.order_id FROM t_order o where  o.user_id in (0) order by o.user_id, o.order_id ";

		Connection conn = dataSource.getConnection();

		ResultSet rs = conn.prepareStatement(sql).executeQuery();
		System.err.println("user_id\torder_id");
		while (rs.next()) {
			System.err.println("" + rs.getInt(1) + "\t" + rs.getInt(2));
		}

	}

	/**
	 * 根据数据库的信息, 创建数据库连接.
	 * 
	 * @param dataSourceName
	 * @return
	 */
	private static DataSource createDataSource(String dataSourceName) {
		BasicDataSource result = new BasicDataSource();
		result.setDriverClassName(com.mysql.jdbc.Driver.class.getName());
		result.setUrl(String.format("jdbc:mysql://10.120.22.34/%s", dataSourceName));
		result.setUsername("sharding");
		result.setPassword("wjmmzw");
		return result;
	}
}
