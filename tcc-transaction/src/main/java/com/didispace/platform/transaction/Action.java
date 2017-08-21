package com.didispace.platform.transaction;

import java.lang.reflect.Parameter;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonManagedReference;

public class Action implements java.io.Serializable{

	private static final long serialVersionUID = 3530919869440862146L;
	
	/**
	 * 主业务ID
	 */
	private String activityId;

	/**
	 * 从业务ID
	 */
	private String actionId;
	
	/**
	 * 访问的地址
	 */
	private String callUrl;
	
	/**
	 * 执行的类名
	 */
	private String className;
	
	/**
	 * 从业务执行方法名
	 */
	private String execMethod;
	
	/**
	 * 从业务执行方法参数类型
	 */
	private Class<?>[] parameterTypes;
	
	/**
	 * 从业务执行方法参数
	 */
	private Object[] parameters;
	
	/**
	 * 从业务记录创建时间
	 */
	private Date createTime;
	
	/**
	 * 从业务记录修改时间
	 */
	private Date modifyTime;
	
	/**
	 * 从业务执行状态
	 */
	private String status;
	
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

	public String getActionId() {
		return actionId;
	}

	public void setActionId(String actionId) {
		this.actionId = actionId;
	}

	public String getExecMethod() {
		return execMethod;
	}

	public void setExecMethod(String execMethod) {
		this.execMethod = execMethod;
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
	
}
