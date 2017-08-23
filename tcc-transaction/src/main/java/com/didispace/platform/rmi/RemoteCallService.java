package com.didispace.platform.rmi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.didispace.platform.transaction.TxTransactionContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RemoteCallService {

	@Autowired
	RestTemplate restTemplate;

	/**
	 * 确认操作
	 * @param url
	 * @param methodName
	 * @param args
	 * @return
	 */
	public String confirmService(String url, Object[] args) {
		try {
			HttpHeaders headers = new HttpHeaders();
	        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
	        headers.setContentType(type);
	        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
			ObjectMapper mapper = new ObjectMapper();
			if(args.length==1 && args[0] instanceof TxTransactionContext){
				TxTransactionContext context = (TxTransactionContext)args[0];
				HttpEntity<TxTransactionContext> formEntity = new HttpEntity<TxTransactionContext>(context,headers);
				return restTemplate.postForEntity(url, formEntity, String.class).getBody();
			}else{
				HttpEntity<Object[]> formEntity = new HttpEntity<Object[]>(args,headers);
				return restTemplate.postForEntity(url, formEntity, String.class).getBody();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 取消操作
	 * @param url
	 * @param methodName
	 * @param args
	 * @return
	 */
	public String cancelService(String url, Object[] args) {
		try {
			HttpHeaders headers = new HttpHeaders();
	        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
	        headers.setContentType(type);
	        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
			ObjectMapper mapper = new ObjectMapper();
			HttpEntity<String> formEntity = new HttpEntity<String>(mapper.writeValueAsString(args),headers);
			System.out.println("url=============="+url);
			return restTemplate.postForObject(url, formEntity, String.class);
		} catch (RestClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
