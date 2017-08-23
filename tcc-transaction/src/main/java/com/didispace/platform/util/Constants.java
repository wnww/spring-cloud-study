package com.didispace.platform.util;

public class Constants {

	public final static class Type {

		/**
		 * 业务主发起方
		 */
		public static final String ACTIVITY = "M";

		/**
		 * 业务从服务
		 */
		public static final String ACTION = "F";

		/**
		 * 发起方调用提供方
		 */
		public static final String CALL = "C";

	}

	public final static class TransactionStatus {
		/**
		 * 创建初始化
		 */
		public static final String INIT = "I";
		/**
		 * 提交
		 */
		public static final String COMMIT = "C";
		/**
		 * 回滚
		 */
		public static final String ROLLBACK = "R";
		/**
		 * 异常
		 */
		public static final String EXCEPTION = "E";
		
		/**
		 * 提交异常
		 */
		public static final String EXCEPTION_COMMIT = "EC";
		/**
		 * 回滚异常
		 */
		public static final String EXCEPTION_ROLLBACK = "ER";
	}

	public final static class CallType {

		public static final String ACTIVITY = "A";
		public static final String CONSUMER = "C";
		public static final String PROVIDER = "P";
		public static final String GENERAL = "G";
	}

	public final static class CallMethodName {
		/**
		 * 确认方法
		 */
		public static final String CONFIRM_METHOD = "confirmMethod";
		/**
		 * 取消方法
		 */
		public static final String CANCEL_METHOD = "cancelMethod";
	}

	public final static class ResultStatus {
		/**
		 * 成功
		 */
		public static final String TRUE = "T";
		/**
		 * 失败
		 */
		public static final String FALSE = "F";
		/**
		 * 异常
		 */
		public static final String EXCEPTION = "E";
	}
}
