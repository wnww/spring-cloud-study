package com.didispace.platform.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectAndByteArrayConvert {

	public static Object ByteToObject(byte[] bytes) {
		Object obj = null;
		ByteArrayInputStream bi = null;
		ObjectInputStream oi = null;
		try {
			// bytearray to object
			bi = new ByteArrayInputStream(bytes);
			oi = new ObjectInputStream(bi);
			obj = oi.readObject();
		} catch (Exception e) {
			System.out.println("translation" + e.getMessage());
			e.printStackTrace();
		}finally{
			try {
				bi.close();
				oi.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return obj;
	}
	
	public static byte[] ObjectToByte(java.lang.Object obj) {
		if(obj==null){
			return new byte[1];
		}
	    byte[] bytes = null;
	    ByteArrayOutputStream bo = null;
	    ObjectOutputStream oo = null;
	    try {  
	        // object to bytearray  
	        bo = new ByteArrayOutputStream();  
	        oo = new ObjectOutputStream(bo);  
	        oo.writeObject(obj);  
	        bytes = bo.toByteArray();  
	    } catch (Exception e) {  
	        System.out.println("translation" + e.getMessage());  
	        e.printStackTrace();  
	    } finally{
	    	try {
				bo.close();  
				oo.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
	    }
	    return bytes;  
	}  
}
