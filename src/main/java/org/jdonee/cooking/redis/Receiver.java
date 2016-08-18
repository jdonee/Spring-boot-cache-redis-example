package org.jdonee.cooking.redis;

import org.jdonee.cooking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * 缓存事件监听
 * 
 * @author Frank.Zeng
 *
 */
@Service
@Slf4j
public class Receiver {

	@Autowired
	private UserService userService;

	public void receiveSetList(String redisType) {
		log.info("异步处理： <" + redisType + ">类型的全局数据。");
		if (redisType.equals("user")) {
			userService.updateAll();
		}
	}

}
