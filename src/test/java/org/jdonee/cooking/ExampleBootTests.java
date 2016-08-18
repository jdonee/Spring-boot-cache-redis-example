package org.jdonee.cooking;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.Set;

import org.jdonee.cooking.domain.Address;
import org.jdonee.cooking.domain.User;
import org.jdonee.cooking.service.AddressService;
import org.jdonee.cooking.service.UserService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ExampleBootTests {

	@Rule
	public OutputCapture outputCapture = new OutputCapture();// 打印输出便于校验内容

	@Autowired
	UserService userService;

	@Autowired
	AddressService addressService;

	@Autowired
	StringRedisTemplate stringRedisTemplate;

	private final static Long ID = 1L;

	@Before
	public void setUp() {
		userService.deleteAll();
		addressService.deleteAll();
	}

	@Test
	public void testUser() throws Exception {
		// 1.1 created
		User user = new User(ID, "frank", "zeng");
		userService.save(user);
		String mapString = stringRedisTemplate.opsForValue().get("user:" + ID);
		assertThat(mapString).isEqualTo(
				"[\"org.jdonee.cooking.domain.User\",{\"id\":1,\"firstName\":\"frank\",\"lastName\":\"zeng\",\"nickName\":\"漫步金星\",\"married\":false}]");
		Optional<User> optional = userService.findById(ID);
		assertThat(optional.isPresent()).isTrue();
		Thread.sleep(2000);
		log.info("延迟两秒再查询列表1，保证异步更新完毕....");
		Set<User> results = userService.findAll();
		assertThat(results.size()).isEqualTo(1);
		assertThat(results.stream().filter(u -> u.getFirstName().equals("frank")).findFirst().isPresent())
				.isEqualTo(true);
		// 1.2 updated
		User changeUser = optional.get();
		changeUser.setFirstName("jdonee");
		userService.update(changeUser);
		mapString = stringRedisTemplate.opsForValue().get("user:" + ID);
		assertThat(mapString).isEqualTo(
				"[\"org.jdonee.cooking.domain.User\",{\"id\":1,\"firstName\":\"jdonee\",\"lastName\":\"zeng\",\"nickName\":\"漫步金星\",\"married\":false}]");
		optional = userService.findById(ID);
		assertThat(optional.isPresent()).isTrue();
		assertThat(optional.get().getFirstName()).isEqualTo("jdonee");
		results = userService.findAll();
		Thread.sleep(2000);
		log.info("延迟两秒再查询列表2，保证异步更新完毕....");
		assertThat(results.size()).isEqualTo(1);
		assertThat(results.stream().filter(u -> u.getFirstName().equals("jdonee")).findFirst().isPresent())
				.isEqualTo(true);
		// 1.3 removed
		userService.delete(ID);
		mapString = stringRedisTemplate.opsForValue().get("user:" + ID);
		assertThat(mapString).isNullOrEmpty();
		Thread.sleep(2000);
		log.info("延迟两秒再查询列表3，保证异步更新完毕....");
		results = userService.findAll();
		assertThat(results.size()).isEqualTo(0);
	}

	@Test
	public void testAddress() throws Exception {
		// 1.1 created
		Address address = new Address(ID, "hunan", "loudi");
		addressService.save(address);
		String mapString = stringRedisTemplate.opsForValue().get("address:" + ID);
		assertThat(mapString).isEqualTo(
				"[\"org.jdonee.cooking.domain.Address\",{\"id\":1,\"province\":\"hunan\",\"city\":\"loudi\",\"zipcode\":\"000000\"}]");
		Optional<Address> optional = addressService.findById(ID);
		String output = this.outputCapture.toString();
		assertThat(output).contains("无缓存的时候调用这里，id:1");
		assertThat(optional.isPresent()).isTrue();

		// 1.2 updated actual data but not remove cache if condition failed.
		address = new Address(ID, "hunan", "lianyuan");
		addressService.conditionUpdate(address);
		mapString = stringRedisTemplate.opsForValue().get("address:" + ID);
		assertThat(mapString).isEqualTo(
				"[\"org.jdonee.cooking.domain.Address\",{\"id\":1,\"province\":\"hunan\",\"city\":\"loudi\",\"zipcode\":\"000000\"}]");

		// 1.3 updated actual data and remove cache if condition successed.
		address = new Address(ID, "zhejiang", "jiaxing");
		addressService.conditionUpdate(address);
		mapString = stringRedisTemplate.opsForValue().get("address:" + ID);
		assertThat(mapString).isNullOrEmpty();

		// 1.4 common updated
		Address changeAddress = optional.get();
		changeAddress.setZipcode("417118");
		addressService.update(changeAddress);
		optional = addressService.findByProvince("hunan");
		output = this.outputCapture.toString();
		assertThat(output).contains("无缓存的时候调用这里，province:hunan");
		assertThat(optional.isPresent()).isTrue();
		assertThat(optional.get().getZipcode()).isEqualTo("417118");
		assertThat(addressService.findAll().size()).isEqualTo(1);

		// 1.5 removed
		addressService.delete(changeAddress);
		mapString = stringRedisTemplate.opsForValue().get("address:" + ID);
		assertThat(mapString).isNullOrEmpty();
		assertThat(userService.findAll().size()).isEqualTo(0);
	}

}
