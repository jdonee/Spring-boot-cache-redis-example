package org.jdonee.cooking.service;

import java.util.Optional;
import java.util.Set;

import org.jdonee.cooking.domain.User;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@CacheConfig(cacheNames = "userCache")
public class UserService {

	Set<User> users = Sets.newHashSet();

	@CachePut(key = "'user:'+#user.id")
	public User save(User user) {
		users.add(user);
		return user;
	}

	@CachePut(key = "'user:'+#user.id")
	public User update(User user) {
		users.removeIf(u -> u.getId().equals(user.getId()));
		users.add(user);
		return user;
	}

	@CacheEvict(key = "'user:'+#id")
	public Boolean delete(final Long id) {
		log.info("移除user缓存,id:" + id);
		Boolean deleted = users.removeIf(u -> u.getId().equals(id));
		return deleted;
	}

	@CacheEvict(allEntries = true)
	public void deleteAll() {
		users.clear();
	}

	@Cacheable(key = "'user:'+#id")
	public Optional<User> findById(final Long id) {
		log.info("无缓存的时候调用这里，id:" + id);
		return users.stream().filter(u -> u.getId().equals(id)).findFirst();
	}

	@CachePut(key = "'user:list'")
	public Set<User> updateAll() {
		return users;
	}

	@Cacheable(key = "'user:list'", sync = true/* 保证同步 */)
	public Set<User> findAll() {
		log.info("无缓存的时候调用这里的列表");
		return users;
	}
}
