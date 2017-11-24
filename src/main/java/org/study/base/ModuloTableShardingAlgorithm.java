package org.study.base;

import java.util.Collection;
import java.util.LinkedHashSet;

import com.dangdang.ddframe.rdb.sharding.api.ShardingValue;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.SingleKeyTableShardingAlgorithm;
import com.google.common.collect.Range;

/**
 * 实现分表算法规则接口 SingleKeyTableShardingAlgorithm
 * 
 * @author yangww
 *
 */
public class ModuloTableShardingAlgorithm implements SingleKeyTableShardingAlgorithm<Integer> {

	/**
	 * <p>
	 * select * from t_order from t_order where order_id = 11
	 * <p>
	 * select * from t_order_1 from t_order where order_id = 11
	 * <p>
	 * select * from t_order from t_order where order_id = 44
	 * <p>
	 * select * from t_order_0 from t_order where order_id = 44
	 */
	public String doEqualSharding(Collection<String> tableNames, ShardingValue<Integer> shardingValue) {
		for (String each : tableNames) {
			System.out.println("\n\n开始选择==表:\nstr = " + each + "\n选择依据字段: [" + shardingValue.getColumnName() + "]\n字段的值:" + shardingValue.getValue());
			System.out.println("开始比较:\n表值为:str= " + each);
			System.out.println("shardingValue值为:" + shardingValue.getValue());
			System.out.println("shardingValue%2值为:" + shardingValue.getValue() % 2);

			if (each.endsWith(shardingValue.getValue() % 2 + "")) {
				System.err.println("因为以" + shardingValue.getColumnName() + "值与2求余的值" + shardingValue.getValue() % 2 + "结尾, 故当前被选中的值为:" + each);
				return each;
			}
		}
		throw new IllegalArgumentException();
	}

	/**
	 * select * from t_order from t_order where order_id in (11,44)
	 * <p>
	 * ├────── SELECT * FROM t_order_0 WHERE order_id IN (11,44)
	 * <p>
	 * └────── SELECT * FROM t_order_1 WHERE order_id IN (11,44)
	 * <p>
	 * select * from t_order from t_order where order_id in (11,13,15)
	 * <p>
	 * └────── SELECT * FROM t_order_1 WHERE order_id IN (11,13,15)
	 * <p>
	 * select * from t_order from t_order where order_id in (22,24,26)
	 * <p>
	 * └────── SELECT * FROM t_order_0 WHERE order_id IN (22,24,26)
	 */
	public Collection<String> doInSharding(Collection<String> tableNames, ShardingValue<Integer> shardingValue) {
		Collection<String> result = new LinkedHashSet<>(tableNames.size());
		for (Integer value : shardingValue.getValues()) {
			for (String tableName : tableNames) {
				if (tableName.endsWith(value % 2 + "")) {
					result.add(tableName);
				}
			}
		}
		return result;
	}

	/**
	 * select * from t_order from t_order where order_id between 10 and 20
	 * <p>
	 * ├── SELECT * FROM t_order_0 WHERE order_id BETWEEN 10 AND 20
	 * <p>
	 * └── SELECT * FROM t_order_1 WHERE order_id BETWEEN 10 AND 20
	 */
	public Collection<String> doBetweenSharding(Collection<String> tableNames, ShardingValue<Integer> shardingValue) {
		Collection<String> result = new LinkedHashSet<>(tableNames.size());
		Range<Integer> range = (Range<Integer>) shardingValue.getValueRange();
		for (Integer i = range.lowerEndpoint(); i <= range.upperEndpoint(); i++) {
			for (String each : tableNames) {
				if (each.endsWith(i % 2 + "")) {
					result.add(each);
				}
			}
		}
		return result;
	}

}
