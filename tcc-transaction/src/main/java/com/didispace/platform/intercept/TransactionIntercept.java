package com.didispace.platform.intercept;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.didispace.platform.annotation.TxTransaction;
import com.didispace.platform.transaction.Action;
import com.didispace.platform.transaction.Activity;
import com.didispace.platform.transaction.TxTransactionContext;
import com.didispace.platform.util.Constants;

// 即使加了 @Componet 也需要加入到自动扫描里
@Component
@Aspect
public class TransactionIntercept {
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	private String activityId;
	
	private ValueOperations<String, Activity> redis;
	
	// 定义通用的切入点，其它的通知可以共用
	@Pointcut("@annotation(com.didispace.platform.annotation.TxTransaction) || execution(public * *(com.didispace.platform.transaction.TxTransactionContext,..))")
	public void joinPointExpression() {
		System.out.println("aaaaa");
	}

	@Around("joinPointExpression()")
	public Object checkTransaction(ProceedingJoinPoint pjp) {
		
		Method classMethod = null;
		TxTransaction t = null;
		try {
			// redis操作对象
			redis = redisTemplate.opsForValue();
			// 获取方法签名，如果是接口调用，则这里是返回的接口里的方法
			MethodSignature ms = (MethodSignature)pjp.getSignature();
			// 获取执行的目标类
			Object target = pjp.getTarget();
			// 根据目标类，接口里获取的方法名，方法参数类型获取目标类里的方法
			classMethod = target.getClass().getMethod(ms.getName(), ms.getParameterTypes());
			// 如果方法注解了 TxTransaction则获取注解对象
			
			if (classMethod.isAnnotationPresent(TxTransaction.class)) {
				t = classMethod.getAnnotation(TxTransaction.class);
			}
			// 获取调用参数
			Object[] args = pjp.getArgs();
			// 存在TxTransaction注解，并且注解的值为 “M” 或  “F” 
			if (t != null && this.checkAnnotationValue(t.value())) {
				try {
					if(t.value().equals(Constants.Type.ACTIVITY)){
						System.out.println("activity里访问的URL地址是："+getRequestUrl());
						String className = target.getClass().getName();
						System.out.println("\r\n类名："+className+"\r\n");
						activityId = this.saveTxTransaction(t.value(), getRequestUrl(), className, null, classMethod.getName(), classMethod.getParameterTypes(), args);
						System.out.println("业务管理器，本地发起，生成activityId："+activityId);
						// 执行方法
						System.out.println("执行方法："+classMethod.getName());
						
						return pjp.proceed(args);
					}else if(t.value().equals(Constants.Type.ACTION)){
						System.out.println("action里访问的URL地址是："+getRequestUrl());
						String className = target.getClass().getName();
						System.out.println("\r\n类名："+className+"\r\n");
						this.saveTxTransaction(t.value(), getRequestUrl(), className, ((TxTransactionContext)args[0]).getActivityId(), classMethod.getName(), classMethod.getParameterTypes(), args);
						System.out.println("这里是服务端拦截器获取的activityId："+((TxTransactionContext)args[0]).getActivityId()+"   这个activityId="+activityId+"=是获取不到的");
						return pjp.proceed(args);
					}else{
						return pjp.proceed(args);
					}
				} catch (Throwable e) {
					e.printStackTrace();
					return null;
				}
				// 在业务发起方调用远程的时候
			}else if(t==null && checkTxTransactionContext(args[0]).equals(Constants.CallType.CONSUMER)){
				TxTransactionContext ctx = (TxTransactionContext)args[0];
				System.out.println("将activityId："+activityId+" 置入到本地调用远程方法的第一个参数里");
				ctx.setActivityId(activityId);
				try {
					return pjp.proceed(args);
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
			}else{
				try {
					return pjp.proceed(args);
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private String getRequestUrl(){
		RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        String url = request.getRequestURL().toString();
        return url;
	}
	
	private void getParam(){
		RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        request.getParameter("txTransactionContext");
	}
	
	/**
	 * 计算访问端类型
	 * @param obj
	 * @return
	 */
	private String checkTxTransactionContext(Object obj){
		if(obj==null){
			return Constants.CallType.ACTIVITY;
		}else if(!(obj instanceof TxTransactionContext)){
			return Constants.CallType.ACTIVITY;
		}else if(obj instanceof TxTransactionContext && StringUtils.isEmpty(((TxTransactionContext)obj).getActivityId())){
			return Constants.CallType.CONSUMER;
		}else if(obj instanceof TxTransactionContext && !StringUtils.isEmpty(((TxTransactionContext)obj).getActivityId())){
			return Constants.CallType.PROVIDER;
		}else{
			return Constants.CallType.GENERAL;
		}
		
	}
	
	/**
	 * 调用远程事务管理中心，存储事务相关数据，返回activityId
	 * @param type
	 * @param activityId
	 * @param methodName
	 * @param paramterTypes
	 * @param paramters
	 * @return
	 */
	private String saveTxTransaction(String type,String callUrl,String className, String activityId,String methodName,Class<?>[] paramterTypes,Object[] args) throws Exception{
		if(StringUtils.isEmpty(type) || type.equals(Constants.Type.ACTIVITY)){
			return this.saveActivity(methodName,callUrl, className, paramterTypes, args);
		}else{
			this.saveAction(activityId, methodName,callUrl, className, paramterTypes, args);
			return null;
		}
	}
	
	/**
	 * 向事务管理中心添加一条业务管理器（主事务）记录
	 * @param methodName
	 * @param paramterTypes
	 * @param paramters
	 */
	private String saveActivity(String methodName, String callUrl, String className, Class<?>[] paramterTypes,Object[] args){
		Activity activity = new Activity();
		activity.setActivityId(buildId());
		activity.setCallUrl(callUrl);
		activity.setClassName(className);
		Date curr = new Date();
		activity.setCreateTime(curr);
		activity.setModifyTime(curr);
		activity.setExecMethod(methodName);
		activity.setParameterTypes(paramterTypes);
		activity.setParameters(args);
		activity.setStatus(Constants.TransactionStatus.INIT);
		redis.set(activity.getActivityId(), activity);
		System.out.println("redis存储=============activity");
		return activity.getActivityId();
	}
	
	/**
	 * 向事务管理中心添加一条从事务记录
	 * @param activityId
	 * @param methodName
	 * @param parameterTypes
	 * @param parameters
	 */
	private void saveAction(String activityId,String methodName, String callUrl, String className, Class<?>[] paramterTypes,Object[] args) throws Exception{
		Action action = new Action();
		action.setActivityId(activityId);
		action.setActionId(buildId());
		action.setCallUrl(callUrl);
		action.setClassName(className);
		action.setExecMethod(methodName);
		Date curr = new Date();
		action.setCreateTime(curr);
		action.setModifyTime(curr);
		action.setParameterTypes(paramterTypes);
		action.setParameters(args);
		action.setStatus(Constants.TransactionStatus.INIT);
		boolean exists=redisTemplate.hasKey(activityId);
		if(exists){
			Activity activity = redis.get(activityId);
			activity.addAction(action);
			redis.set(activityId, activity);
			System.out.println("redis存储=============action");
			
		}else{
			throw new Exception("没有主业务Activity，不能添加从业务Action");
		}
	}
	
	/**
	 * 判断TxTransaction注解value的值是否合法
	 * @param type
	 * @return
	 */
	private boolean checkAnnotationValue(String type){
		System.out.println("1");
		if(StringUtils.isEmpty(type)){
			return false;
		}
		if(!type.equals(Constants.Type.ACTIVITY) && !type.equals(Constants.Type.ACTION)){
			return false;
		}
		return true;
	}
	
	private Object[] arrayTransform(Object[] src, Object[] target){
		for(int i=1; i<target.length; i++){
			target[i] = src[i-1];
		}
		return target;
	}
	
	/**
	 * ID生成器
	 * @return
	 */
	public static String buildId(){
		//IdBuillder idBuillder = new IdBuillder(Long.valueOf(ConfigUtils.getWorkerId()),Long.valueOf(ConfigUtils.getDataCenterId()));
		//return String.valueOf(idBuillder.nextId());
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
}
