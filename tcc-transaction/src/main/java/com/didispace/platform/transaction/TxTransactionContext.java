package com.didispace.platform.transaction;

public class TxTransactionContext implements java.io.Serializable{
	
	private static final long serialVersionUID = -6071675874422081821L;
	
	private String activityId;

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	
	
}
