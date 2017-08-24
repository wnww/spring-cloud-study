package com.didispace.platform.intercept;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.bag.SynchronizedBag;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.didispace.platform.annotation.TxTransaction;
import com.didispace.platform.rmi.RemoteCallService;
import com.didispace.platform.transaction.Action;
import com.didispace.platform.transaction.Activity;
import com.didispace.platform.transaction.ResultEntity;
import com.didispace.platform.transaction.TxTransactionContext;
import com.didispace.platform.util.Constants;

// 即使加了 @Componet 也需要加入到自动扫描里
@Component
@Aspect
public class TransactionIntercept {

	private final Logger logger = Logger.getLogger(TransactionIntercept.class);

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private RemoteCallService remoteCallService;

	private String activityId;

	private ValueOperations<String, Activity> redis;

	// 定义通用的切入点，其它的通知可以共用
	@Pointcut("@annotation(com.didispace.platform.annotation.TxTransaction) || execution(public * *(com.didispace.platform.transaction.TxTransactionContext,..))")
	public void joinPointExpression() {
	}

	@Around("joinPointExpression()")
	public Object checkTransaction(ProceedingJoinPoint pjp) {

		Method classMethod = null;
		TxTransaction t = null;
		// redis操作对象，将事务数据存储到redis里进行持久化
		redis = redisTemplate.opsForValue();
		// 获取方法签名，如果是接口调用，则这里是返回的接口里的方法
		MethodSignature ms = (MethodSignature) pjp.getSignature();
		// 获取执行的目标类
		Object target = pjp.getTarget();
		// 根据目标类，接口里获取的方法名，方法参数类型获取目标类里的方法
		try {
			classMethod = target.getClass().getMethod(ms.getName(), ms.getParameterTypes());
		} catch (NoSuchMethodException e1) {
			try {
				pjp.proceed(pjp.getArgs());
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			e1.printStackTrace();
		} catch (SecurityException e1) {
			e1.printStackTrace();
		}
		// 如果方法注解了 TxTransaction则获取注解对象
		if (classMethod.isAnnotationPresent(TxTransaction.class)) {
			t = classMethod.getAnnotation(TxTransaction.class);
		}
		// 获取调用参数
		Object[] args = pjp.getArgs();
		// 存在TxTransaction注解，并且注解的值为 “M” 或 “F”
		if (t != null && this.checkAnnotationValue(t.value())) {
			try {
				if (t.value().equals(Constants.Type.ACTIVITY)) {
					logger.debug("====================activity");
					String className = target.getClass().getName();
					activityId = this.saveTxTransaction(t, className, classMethod.getName(),
							classMethod.getParameterTypes(), args);
					Object callResult = null;
					try {
						callResult = pjp.proceed(args);
						ResultEntity re = (ResultEntity)callResult;
						if(re!=null && re.getFlag().equals(Constants.ResultStatus.TRUE)){
							this.callConfirm(t, pjp, activityId);
						}else{
							this.callCancel(t, pjp, activityId);
						}
					} catch (Exception e) {
						this.callCancel(t, pjp, activityId);
						e.printStackTrace();
					}
					return callResult;
				} else if (t.value().equals(Constants.Type.ACTION)) {
					logger.debug("====================action");
					String className = target.getClass().getName();
					this.saveTxTransaction(t, className, classMethod.getName(), classMethod.getParameterTypes(), args);
					return pjp.proceed(args);
				} else {
					return pjp.proceed(args);
				}
			} catch (Throwable e) {
				e.printStackTrace();
				return null;
			}
			// 在业务发起方调用远程的时候
		} else if (t == null && checkTxTransactionContext(args[0]).equals(Constants.CallType.CONSUMER)) {
			TxTransactionContext context = (TxTransactionContext) args[0];
			context.setActivityId(activityId);
			try {
				return pjp.proceed(args);
			} catch (Throwable e) {
				e.printStackTrace();
				return null;
			}
		} else {
			try {
				return pjp.proceed(args);
			} catch (Throwable e) {
				e.printStackTrace();
				return null;
			}
		}

	}

	/**
	 * 调用远程事务管理中心，存储事务相关数据，返回activityId
	 * 
	 * @param type
	 * @param activityId
	 * @param methodName
	 * @param paramterTypes
	 * @param paramters
	 * @return
	 */
	private String saveTxTransaction(TxTransaction t, String className, String methodName, Class<?>[] paramterTypes,
			Object[] args) throws Exception {
		String callUrl = getRequestUrl();
		if (StringUtils.isEmpty(t.value()) || Constants.Type.ACTIVITY.equals(t.value())) {
			return this.saveActivity(t, methodName, callUrl, className, paramterTypes, args);
		} else {
			this.saveAction(t, methodName, callUrl, className, paramterTypes, args);
			return null;
		}
	}

	/**
	 * 向事务管理中心添加一条业务管理器（主事务）记录
	 * 
	 * @param methodName
	 * @param paramterTypes
	 * @param paramters
	 */
	private String saveActivity(TxTransaction t, String methodName, String callUrl, String className,
			Class<?>[] paramterTypes, Object[] args) {
		Activity activity = new Activity();
		activity.setActivityId(buildId());
		activity.setCallUrl(callUrl);
		activity.setClassName(className);
		String callRemote = new String(callUrl);
		callRemote = callRemote.substring(0, callRemote.lastIndexOf("/") + 1);
		activity.setCallConfirmUrl(callRemote + t.confirmMethod());
		activity.setCallCancelUrl(callRemote + t.cancelMethod());
		Date curr = new Date();
		activity.setCreateTime(curr);
		activity.setModifyTime(curr);
		activity.setExecMethod(methodName);
		activity.setParameterTypes(paramterTypes);
		activity.setParameters(args);
		activity.setStatus(Constants.TransactionStatus.INIT);
		redis.set(activity.getActivityId(), activity);
		return activity.getActivityId();
	}

	/**
	 * 向事务管理中心添加一条从事务记录
	 * 
	 * @param activityId
	 * @param methodName
	 * @param parameterTypes
	 * @param parameters
	 */
	private void saveAction(TxTransaction t, String methodName, String callUrl, String className,
			Class<?>[] paramterTypes, Object[] args) throws Exception {
		Action action = new Action();
		action.setActivityId(((TxTransactionContext) args[0]).getActivityId());
		action.setActionId(buildId());
		action.setCallUrl(callUrl);
		action.setClassName(className);
		action.setExecMethod(methodName);
		// String callRemote = ((TxTransactionContext)args[0]).getRemoteHost();
		// callRemote = "http://"+callRemote+"/";
		String callRemote = new String(callUrl);
		callRemote = callRemote.substring(0, callRemote.lastIndexOf("/") + 1);
		action.setCallConfirmUrl(callRemote + t.confirmMethod());
		action.setCallCancelUrl(callRemote + t.cancelMethod());
		Date curr = new Date();
		action.setCreateTime(curr);
		action.setModifyTime(curr);
		action.setParameterTypes(paramterTypes);
		action.setParameters(args);
		action.setStatus(Constants.TransactionStatus.INIT);
		boolean exists = redisTemplate.hasKey(((TxTransactionContext) args[0]).getActivityId());
		if (exists) {
			Activity activity = redis.get(((TxTransactionContext) args[0]).getActivityId());
			activity.addAction(action);
			redis.set(((TxTransactionContext) args[0]).getActivityId(), activity);
		} else {
			throw new Exception("没有主业务Activity，不能添加从业务Action");
		}
	}

	private String getRequestUrl() {
		RequestAttributes ra = RequestContextHolder.getRequestAttributes();
		ServletRequestAttributes sra = (ServletRequestAttributes) ra;
		HttpServletRequest request = sra.getRequest();
		String url = request.getRequestURL().toString();
		return url;
	}

	private void getParam() {
		RequestAttributes ra = RequestContextHolder.getRequestAttributes();
		ServletRequestAttributes sra = (ServletRequestAttributes) ra;
		HttpServletRequest request = sra.getRequest();

		request.getParameter("txTransactionContext");
	}

	private void callConfirm(TxTransaction t, ProceedingJoinPoint pjp, String activityId) {
		synchronized (activityId) {
			boolean exists = redisTemplate.hasKey(activityId);
			if (exists) {
				Activity activity = redis.get(activityId);
				try {
					// 调用Activity的confirm方法
					ResultEntity re = remoteCallService.confirmService(activity.getCallConfirmUrl(),
							activity.getParameters());
					if (re != null && StringUtils.isNotEmpty(re.getFlag())) {
						// 如果成功更新事务日志状态为“C”
						if (Constants.ResultStatus.TRUE.equals(re.getFlag())) {
							activity.setStatus(Constants.TransactionStatus.COMMIT);
						}else { // 无论是失败或是异常，事务日志都是异常
							activity.setStatus(Constants.TransactionStatus.EXCEPTION_COMMIT);
						}
					}else{
						// 事务操作日志设置为异常
						activity.setStatus(Constants.TransactionStatus.EXCEPTION_COMMIT);
					}
					activity.setModifyTime(new Date());
					redis.set(activityId, activity);
					// 重新加载事务日志
					activity = redis.get(activityId);
					List<Action> actions = activity.getActions();
					for (Action action : actions) {
						try {
							ResultEntity re1 = remoteCallService.confirmService(action.getCallConfirmUrl(),
									action.getParameters());
							if(re1!=null && StringUtils.isNotEmpty(re1.getFlag())){
								if(re1.getFlag().equals(Constants.ResultStatus.TRUE)){
									action.setStatus(Constants.TransactionStatus.COMMIT);
								}else{
									action.setStatus(Constants.TransactionStatus.EXCEPTION_COMMIT);
								}
							}else{
								action.setStatus(Constants.TransactionStatus.EXCEPTION_COMMIT);
							}
							// 更新从业务日志
						} catch (Exception e) {
							e.printStackTrace();
							action.setStatus(Constants.TransactionStatus.EXCEPTION_COMMIT);
						}
						action.setModifyTime(new Date());
					}
					redis.set(activityId, activity);
				} catch (Exception e) {
					e.printStackTrace();
					activity.setStatus(Constants.TransactionStatus.EXCEPTION_COMMIT);
					activity.setModifyTime(new Date());
					redis.set(activityId, activity);
				}

			}
		}

	}

	private void callCancel(TxTransaction t, ProceedingJoinPoint pjp, String activityId) {
		synchronized (activityId) {
			boolean exists = redisTemplate.hasKey(activityId);
			if (exists) {
				Activity activity = redis.get(activityId);
				try{
					ResultEntity re = remoteCallService.cancelService(activity.getCallCancelUrl(),
							activity.getParameters());
					if (re != null && StringUtils.isNotEmpty(re.getFlag())) {
						// 如果成功更新事务日志状态为“C”
						if (Constants.ResultStatus.TRUE.equals(re.getFlag())) {
							activity.setStatus(Constants.TransactionStatus.ROLLBACK);
						}else { // 无论是失败或是异常，事务日志都是异常
							activity.setStatus(Constants.TransactionStatus.EXCEPTION_ROLLBACK);
						}
					}else{
						// 事务操作日志设置为异常
						activity.setStatus(Constants.TransactionStatus.EXCEPTION_ROLLBACK);
					}
					activity.setModifyTime(new Date());
					redis.set(activityId, activity);
					// 重新加载事务日志
					activity = redis.get(activityId);
					List<Action> actions = activity.getActions();
					for (Action action : actions) {
						try{
							ResultEntity re1 = remoteCallService.cancelService(action.getCallCancelUrl(), action.getParameters());
							if(re1!=null && StringUtils.isNotEmpty(re1.getFlag())){
								if(re1.getFlag().equals(Constants.ResultStatus.TRUE)){
									action.setStatus(Constants.TransactionStatus.ROLLBACK);
								}else{
									action.setStatus(Constants.TransactionStatus.EXCEPTION_ROLLBACK);
								}
							}else{
								action.setStatus(Constants.TransactionStatus.EXCEPTION_ROLLBACK);
							}
						}catch(Exception e){
							e.printStackTrace();
							action.setStatus(Constants.TransactionStatus.EXCEPTION_ROLLBACK);
						}
						action.setModifyTime(new Date());
					}
					// 更新从业务日志
					redis.set(activityId, activity);
				}catch(Exception e){
					e.printStackTrace();
					activity.setStatus(Constants.TransactionStatus.EXCEPTION_ROLLBACK);
					activity.setModifyTime(new Date());
					redis.set(activityId, activity);
				}
			}
		}
	}

	/**
	 * 计算访问端类型
	 * 
	 * @param obj
	 * @return
	 */
	private String checkTxTransactionContext(Object obj) {
		if (obj == null) {
			return Constants.CallType.ACTIVITY;
		} else if (!(obj instanceof TxTransactionContext)) {
			return Constants.CallType.ACTIVITY;
		} else if (obj instanceof TxTransactionContext
				&& StringUtils.isEmpty(((TxTransactionContext) obj).getActivityId())) {
			return Constants.CallType.CONSUMER;
		} else if (obj instanceof TxTransactionContext
				&& !StringUtils.isEmpty(((TxTransactionContext) obj).getActivityId())) {
			return Constants.CallType.PROVIDER;
		} else {
			return Constants.CallType.GENERAL;
		}

	}

	/**
	 * 判断TxTransaction注解value的值是否合法
	 * 
	 * @param type
	 * @return
	 */
	private boolean checkAnnotationValue(String type) {
		if (StringUtils.isEmpty(type)) {
			return false;
		}
		if (!type.equals(Constants.Type.ACTIVITY) && !type.equals(Constants.Type.ACTION)) {
			return false;
		}
		return true;
	}

	private Object[] arrayTransform(Object[] src, Object[] target) {
		for (int i = 1; i < target.length; i++) {
			target[i] = src[i - 1];
		}
		return target;
	}

	/**
	 * ID生成器
	 * 
	 * @return
	 */
	public static String buildId() {
		// IdBuillder idBuillder = new
		// IdBuillder(Long.valueOf(ConfigUtils.getWorkerId()),Long.valueOf(ConfigUtils.getDataCenterId()));
		// return String.valueOf(idBuillder.nextId());
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

}
