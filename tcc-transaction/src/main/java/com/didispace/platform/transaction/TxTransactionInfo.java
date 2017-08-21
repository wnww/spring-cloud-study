package com.didispace.platform.transaction;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonBackReference;

public class TxTransactionInfo {

	/**
	 * 主业务管理器
	 */
	private Activity activity;
	/**
	 * 从业务管理器
	 */
	private List<Action> actions;
	
	
	public Activity getActivity() {
		return activity;
	}
	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	public List<Action> getActions() {
		return actions;
	}
	public void setActions(List<Action> actions) {
		this.actions = actions;
	}
	
	public void setAction(Action action){
		if(CollectionUtils.isEmpty(this.actions)){
			this.actions = new ArrayList<Action>();
		}
		this.actions.add(action);
	}
}
