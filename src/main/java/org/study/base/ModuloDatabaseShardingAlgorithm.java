package org.study.base;

import java.util.Collection;
import java.util.LinkedHashSet;

import com.dangdang.ddframe.rdb.sharding.api.ShardingValue;
import com.dangdang.ddframe.rdb.sharding.api.strategy.database.SingleKeyDatabaseShardingAlgorithm;
import com.google.common.collect.Range;

/**
 * 实现分库规则算法接口SingleKeyDatabaseShardingAlgorithm
 * 
 * @author yangww
 *
 */
public class ModuloDatabaseShardingAlgorithm implements SingleKeyDatabaseShardingAlgorithm<Integer> {

	/**
	 * 判断相等
	 */
	public String doEqualSharding(Collection<String> availableTargetNames, ShardingValue<Integer> shardingValue) {
		System.out.println("\n\n开始选择数据库:\n选择依据字段: [" + shardingValue.getColumnName() + "]\n字段的值:" + shardingValue.getValue());
		for (String str : availableTargetNames) {
			System.out.print("=====================================\n开始比较:\t数据库值为:str= " + str);
			System.out.print("\tshardingValue[" + shardingValue.getColumnName() + "]值为:" + shardingValue.getValue());
			System.out.print("\tshardingValue%4 [" + shardingValue.getColumnName() + "]值为:" + shardingValue.getValue() % 4);
			if (str.endsWith(shardingValue.getValue() % 4 + "")) {
				System.err.print("\t因为以" + shardingValue.getColumnName() + "值与4求余的值" + shardingValue.getValue() % 4 + "结尾, 故当前被选中的值为:" + str
						+ "\n======================================");
				return str;
			}
		}
		throw new IllegalArgumentException();
	}

	public Collection<String> doInSharding(Collection<String> availableTargetNames, ShardingValue<Integer> shardingValue) {
		Collection<String> result = new LinkedHashSet<String>(availableTargetNames.size());
		for (Integer value : shardingValue.getValues()) {
			for (String tableName : availableTargetNames) {
				System.err.println("\nIn\n\nModuloDatabaseShardingAlgorithm.doInSharding():\n tableName=" + tableName + "\nvalue = " + value + "\nvalue % 2 = "
						+ value % 2);
				if (tableName.endsWith(value % 2 + "")) {
					result.add(tableName);
				}
			}
		}
		return result;
	}

	public Collection<String> doBetweenSharding(Collection<String> availableTargetNames, ShardingValue<Integer> shardingValue) {
		Collection<String> result = new LinkedHashSet<String>(availableTargetNames.size());
		Range<Integer> range = shardingValue.getValueRange();
		for (Integer i = range.lowerEndpoint(); i <= range.upperEndpoint(); i++) {
			for (String each : availableTargetNames) {
				System.err.println("\nBetween\n\nModuloDatabaseShardingAlgorithm.doBetweenSharding():\neach = " + each + "\ni=" + i + "\ni % 2 = " + i % 2);
				if (each.endsWith(i % 2 + "")) {
					result.add(each);
				}
			}
		}

		return result;
	}

}
