package org.jdonee.cooking;

import org.jdonee.cooking.config.Constants;
import org.jdonee.cooking.redis.Receiver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import lombok.extern.slf4j.Slf4j;

/**
 * 1、Spring boot + Spring Cache +Spring Data Redis+Spring AOP。
 * 2、接受Spring Cache的普通用法。
 * 3、使用Jackson2存储Redis缓存对象。
 * 4、使用AOP处理当对象保存、更新或删除时对象列表的更新。
 * 5、对象列表更新使用Redis的订阅异步处理。
 * 
 * @author Frank.Zeng
 *
 */
@SpringBootApplication
@Slf4j
public class ExampleBoot {

	// Redis消息监听容器
	@Bean
	RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter) {
		log.info("开始加载Redis消息监听容器...");
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(listenerAdapter, new PatternTopic(Constants.LIST_TOPIC));
		return container;
	}

	// Redis消息监听代理
	@Bean
	MessageListenerAdapter listenerAdapter(Receiver receiver) {
		return new MessageListenerAdapter(receiver, "receiveSetList");
	}

	public static void main(String[] args) {
		SpringApplication.run(ExampleBoot.class, args);
	}
}
