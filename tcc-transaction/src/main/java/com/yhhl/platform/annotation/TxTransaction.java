package com.yhhl.platform.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yhhl.platform.util.Constants;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TxTransaction {
	
	/**
	 * 注解区分是业务主发起方还是从业务参与方
	 * @return
	 */
	public String value() default Constants.Type.ACTIVITY;
	
	/**
	 * TCC-confirm方法
	 * @return
	 */
	public String confirmMethod() default Constants.CallMethodName.CONFIRM_METHOD;
	
	/**
	 * TCC-cancel方法
	 * @return
	 */
	public String cancelMethod() default Constants.CallMethodName.CANCEL_METHOD;
	
}
