package com.thinkgem.jeesite.common.aspect;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.thinkgem.jeesite.common.annotation.RedisCache;

@Component
@Aspect
public class CacheAspect {
	public static final Logger infoLog = Logger.getLogger(CacheAspect.class);

	// 注入RedisTemplate
	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 获取或者添加缓存切面
	 * 
	 * @param proceedingJoinPoint
	 * @return
	 * @throws Throwable
	 */
	public Object RedisCache(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		Method method = getMethod(proceedingJoinPoint);
		RedisCache redisCache = method.getAnnotation(RedisCache.class);// 获取方法上的注解
		// 得到类名,方法名,参数
		Object[] args = proceedingJoinPoint.getArgs();
		// 根据类名,方法名和参数生成key
		String key = generateKey(redisCache.fieldKey(), method, proceedingJoinPoint.getArgs());
		if (infoLog.isDebugEnabled()) {
			infoLog.debug("生成key:" + key);
		}
		Class modelClass = method.getAnnotation(RedisCache.class).type();
		// 检查redis是否存在缓存
		String cacheValue = (String) redisTemplate.opsForHash().get(modelClass.getName(), key);
		// result返回结果
		Object result = null;
		if (null == cacheValue) {
			// 缓存未命中
			if (infoLog.isDebugEnabled()) {
				infoLog.debug("缓存未命中");
			}
			// 调用数据库查询方法
			result = proceedingJoinPoint.proceed(args);
			// 序列化查询结果
			final String json = serialize(result);
			final String hashName = modelClass.getName();
			final int expire = redisCache.expire();
			// 存入redis中
			redisTemplate.opsForHash().putIfAbsent(hashName, key, json);
		} else {
			// 缓存命中
			if (infoLog.isDebugEnabled()) {
				infoLog.debug("缓存命中, value = " + cacheValue);
			}
			// 得到被代理方法的返回值类型
			Class returnType = ((MethodSignature) proceedingJoinPoint.getSignature()).getReturnType();

			// 反序列化从缓存中拿到的json
			result = deserialize(cacheValue, returnType, modelClass);

			if (infoLog.isDebugEnabled()) {
				infoLog.debug("反序列化结果 = {}" + result);
			}
		}
		return null;

	}

	/**
	 * 反序列化查询 结果
	 * 
	 * @param cacheValue
	 * @param returnType
	 * @param modelClass
	 * @return
	 */
	private Object deserialize(String cacheValue, Class returnType, Class modelClass) {
		// 序列化结果应该是List对象
		if (returnType.isAssignableFrom(List.class)) {
			return JSON.parseArray(cacheValue, modelClass);
		}

		// 序列化结果是普通对象
		return JSON.parseObject(cacheValue, modelClass);
	}

	/**
	 * 序列化查询结果
	 * 
	 * @param result
	 * @return
	 */
	private String serialize(Object result) {
		return JSON.toJSONString(result);
	}

	/**
	 * 根据类名,方法名和参数生成key
	 * 
	 * @param fieldKey
	 * @param method
	 * @param args
	 * @return
	 * @throws Exception
	 */
	private String generateKey(String fieldKey, Method method, Object[] args) throws Exception {
		// 获取缓存的key使用spring el表达式
		// 获取被拦截方法参数名列表(使用Spring支持类库)
		LocalVariableTableParameterNameDiscoverer localVariableTableParameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
		String[] paraNameArr = localVariableTableParameterNameDiscoverer.getParameterNames(method);
		// 使用spel进行key解析
		SpelExpressionParser parser = new SpelExpressionParser();
		// spel上下文
		StandardEvaluationContext context = new StandardEvaluationContext();
		// 把方法参数放入spel上下文中
		for (int i = 0; i < paraNameArr.length; i++) {
			context.setVariable(paraNameArr[i], args[i]);
		}

		return parser.parseExpression(fieldKey).getValue(context, String.class);
	}

	/**
	 * 获取目标类的方法
	 * 
	 * @param proceedingJoinPoint
	 * @return
	 */
	private Method getMethod(ProceedingJoinPoint proceedingJoinPoint) {
		// 获取参数类型
		Object[] args = proceedingJoinPoint.getArgs();
		Class[] argTypes = new Class[proceedingJoinPoint.getArgs().length - 1];
		for (int i = 0; i < args.length; i++) {
			argTypes[i] = args[i].getClass();
		}
		Method method = null;
		try {
			method = proceedingJoinPoint.getTarget().getClass().getMethod(proceedingJoinPoint.getSignature().getName(),
					argTypes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return method;
	}

}
