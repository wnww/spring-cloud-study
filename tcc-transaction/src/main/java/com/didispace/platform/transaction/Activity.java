package com.didispace.platform.transaction;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonManagedReference;

public class Activity implements java.io.Serializable{
	
	private static final long serialVersionUID = -2069960949579094738L;

	/**
	 * 主业务管理器ID
	 */
	private String activityId;
	
	/**
	 * 访问的地址
	 */
	private String callUrl;
	
	/**
	 * 执行的类名
	 */
	private String className;
	
	/**
	 * 主业务执行方法名
	 */
	private String execMethod;
	
	/**
	 * confirm调用地址
	 */
	private String callConfirmUrl;
	/**
	 * cancel调用地址
	 */
	private String callCancelUrl;
	
	/**
	 * 主业务执行方法参数类型
	 */
	private Class<?>[] parameterTypes;
	
	/**
	 * 主业务执行方法参数
	 */
	private Object[] parameters;
	
	/**
	 * 主业务记录创建时间
	 */
	private Date createTime;
	
	/**
	 * 主业务记录修改时间
	 */
	private Date modifyTime;
	
	/**
	 * 主业务执行状态
	 */
	private String status;
	
	/**
	 * 从业务事务日志
	 */
	private List<Action> actions;
	
	public void addAction(Action action){
		if(CollectionUtils.isEmpty(actions)){
			actions = new ArrayList<Action>();
		}
		actions.add(action);
	}
	
	public String getActivityId() {
		return activityId;
	}
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	
	public String getCallUrl() {
		return callUrl;
	}

	public void setCallUrl(String callUrl) {
		this.callUrl = callUrl;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getExecMethod() {
		return execMethod;
	}
	public void setExecMethod(String execMethod) {
		this.execMethod = execMethod;
	}
	
	public String getCallConfirmUrl() {
		return callConfirmUrl;
	}

	public void setCallConfirmUrl(String callConfirmUrl) {
		this.callConfirmUrl = callConfirmUrl;
	}

	public String getCallCancelUrl() {
		return callCancelUrl;
	}

	public void setCallCancelUrl(String callCancelUrl) {
		this.callCancelUrl = callCancelUrl;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<Action> getActions() {
		return actions;
	}
	public void setActions(List<Action> actions) {
		this.actions = actions;
	}
}
