package org.jdonee.cooking.monitor;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.jdonee.cooking.config.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * AOP服务监听
 * 
 * @author Frank.Zeng
 *
 */
@Aspect
@Component
public class ServiceMonitor {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@AfterReturning(pointcut = "execution(* org.jdonee..*Service.save(..)) || execution(* org.jdonee..*Service.update(..)) || execution(* org.jdonee..*Service.delete(..))", returning = "returnValue")
	public void logServiceAccess(JoinPoint joinPoint, Object returnValue) {
		String declaringTypeName = joinPoint.getSignature().getDeclaringTypeName();
		if (declaringTypeName.contains("UserService")) {
			System.out.println("@AfterReturning：模拟日志记录功能...");
			System.out.println("@AfterReturning：目标方法为：" + joinPoint.getSignature().getDeclaringTypeName() + "."
					+ joinPoint.getSignature().getName());
			System.out.println("@AfterReturning：参数为：" + Arrays.toString(joinPoint.getArgs()));
			System.out.println("@AfterReturning：返回值为：" + returnValue);
			System.out.println("@AfterReturning：被织入的目标对象为：" + joinPoint.getTarget());
			stringRedisTemplate.convertAndSend(Constants.LIST_TOPIC, "user");
		}

	}
}
