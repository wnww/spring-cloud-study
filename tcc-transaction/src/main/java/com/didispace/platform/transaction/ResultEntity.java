package com.didispace.platform.transaction;

import java.util.Map;

public class ResultEntity implements java.io.Serializable {

	private static final long serialVersionUID = -6300961325301085366L;

	// 操作结果：T:成功，F:失败，E:异常
	private String flag; 

	private Map<String, Object> resultMap;

	private String message;

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public Map<String, Object> getResultMap() {
		return resultMap;
	}

	public void setResultMap(Map<String, Object> resultMap) {
		this.resultMap = resultMap;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
