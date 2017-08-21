package com.didispace.platform.util;

import java.util.Properties;

import org.springframework.util.StringUtils;

public class ConfigUtils {

	private static Properties configPerties = new Properties();
	
	private static String propertiesName;

	static {
		try {
			if(StringUtils.isEmpty(propertiesName)){
				configPerties.load(ConfigUtils.class.getClassLoader().getResourceAsStream("sysconfig.properties"));
			}else{
				configPerties.load(ConfigUtils.class.getClassLoader().getResourceAsStream(propertiesName));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取本机机器ID
	 * @return
	 */
	public static String getWorkerId(){
		return getString("workerId");
	}
	
	/**
	 * 获取数据中心ID
	 * @return
	 */
	public static String getDataCenterId(){
		return getString("dataCenterId");
	}

	/**
	 * 获取字符串类型
	 * 
	 * @param key
	 * @return
	 */
	public static String getString(String key) {
		return (String) configPerties.get(key);
	}

	/**
	 * 数字类型
	 * 
	 * @param key
	 * @return
	 */
	public static int getInt(String key) {
		try {
			String i = (String) configPerties.get(key);
			return Integer.valueOf(i);
		} catch (Exception e) {
			return 0;
		}
	}
	
}
