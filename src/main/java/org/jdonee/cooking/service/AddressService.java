package org.jdonee.cooking.service;

import java.util.Optional;
import java.util.Set;

import org.jdonee.cooking.domain.Address;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@CacheConfig(cacheNames = "addressCache")
public class AddressService {

	Set<Address> addresss = Sets.newHashSet();

	@Caching(put = { @CachePut(key = "'address:'+#address.id"), @CachePut(key = "'address:'+#address.province") })
	public Address save(final Address address) {
		addresss.add(address);
		return address;
	}

	@Caching(put = { @CachePut(key = "'address:'+#address.id"), @CachePut(key = "'address:'+#address.province") })
	public Address update(final Address address) {
		addresss.removeIf(o -> o.getId().equals(address.getId()));
		addresss.add(address);
		return address;
	}

	/**
	 * 删除对象时所有相关缓存都删除
	 * 
	 * @param address
	 * @return
	 */
	@Caching(evict = { @CacheEvict(key = "'address:'+#address.id"), @CacheEvict(key = "'address:'+#address.province") })
	public Address delete(final Address address) {
		addresss.removeIf(o -> o.getId().equals(address.getId()));
		log.info("移除address缓存,id:" + address.getId());
		return address;
	}

	/**
	 * 清空缓存
	 */
	@CacheEvict(allEntries = true)
	public void deleteAll() {
		addresss.clear();
	}

	public Set<Address> findAll() {
		return addresss;
	}

	/**
	 * 当省份存在时更新省份缓存
	 * 
	 * @param id
	 * @return
	 */
	@Caching(cacheable = { @Cacheable(key = "'address:'+#id") }, put = {
			@CachePut(key = "'address:'+#result.province", condition = "#result != null") })
	public Optional<Address> findById(final Long id) {
		log.info("无缓存的时候调用这里，id:" + id);
		return addresss.stream().filter(o -> o.getId().equals(id)).findFirst();
	}

	/**
	 * 当Id存在时更新ID缓存
	 * 
	 * @param province
	 * @return
	 */
	@Caching(cacheable = { @Cacheable(key = "'address:'+#province") }, put = {
			@CachePut(key = "'address:'+#result.id", condition = "#result != null") })
	public Optional<Address> findByProvince(final String province) {
		log.info("无缓存的时候调用这里，province:" + province);
		return addresss.stream().filter(o -> o.getProvince().equals(province)).findFirst();
	}

	/**
	 * 按条件更新
	 * 
	 * @param address
	 */
	@Caching(evict = {
			@CacheEvict(key = "'address:'+#address.id", condition = "#root.target.canEvict(#root.caches[0], #address.id, #address.province)", beforeInvocation = true) })
	public void conditionUpdate(final Address address) {
		addresss.removeIf(o -> o.getId().equals(address.getId()));
		addresss.add(address);
	}

	/**
	 * 能销毁缓存的条件
	 * 
	 * @param addressCache
	 * @param id
	 * @param province
	 * @return
	 */
	@SuppressWarnings("null")
	public boolean canEvict(Cache addressCache, final Long id, final String province) {
		boolean can = false;
		Address cacheAddress = addressCache.get("address:" + id, Address.class);
		if (cacheAddress == null) {
			can = false;
		}
		can = !cacheAddress.getProvince().equals(province);
		return can;
	}

}
