package com.kii.beehive.portal.jedis.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.kii.beehive.portal.jedis.helper.RedisHelper;

/**
 * Created by carlos.yang on 16/6/3.
 */

@Repository
public class MessageQueueDao {


	@Autowired
	private RedisHelper redisHelper;


	/**
	 *
	 * @param queue
	 * @param value
	 * @return
	 */
	public Long lpush(String queue, String... value) {

		return redisHelper.lpush(queue, value);
	}


	public String brpop(String queue, int timeout) {

		return redisHelper.brpop(timeout, queue);

	}

}
