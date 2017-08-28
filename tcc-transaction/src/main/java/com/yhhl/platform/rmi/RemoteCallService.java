package com.yhhl.platform.rmi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.yhhl.platform.transaction.ResultEntity;
import com.yhhl.platform.transaction.TxTransactionContext;
import com.yhhl.platform.util.Constants;
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
	public ResultEntity confirmService(String url, Object[] args) {
		try {
			HttpHeaders headers = new HttpHeaders();
	        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
	        headers.setContentType(type);
	        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
			ObjectMapper mapper = new ObjectMapper();
			if((args.length==1 && args[0] instanceof TxTransactionContext) || args.length==0){
				HttpEntity<TxTransactionContext> formEntity = null;
				if(args.length==1){
					TxTransactionContext context = (TxTransactionContext)args[0];
					formEntity = new HttpEntity<TxTransactionContext>(context,headers);
				}else{
					formEntity = new HttpEntity<TxTransactionContext>(new TxTransactionContext(),headers);
				}
				return restTemplate.postForEntity(url, formEntity, ResultEntity.class).getBody();
			}else{
				HttpEntity<Object[]> formEntity = new HttpEntity<Object[]>(args,headers);
				return restTemplate.postForEntity(url, formEntity, ResultEntity.class).getBody();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			ResultEntity re = new ResultEntity();
			re.setFlag(Constants.ResultStatus.EXCEPTION);
			re.setMessage("服务端异常或网络异常");
			return re;
		}
	}
	
	/**
	 * 取消操作
	 * @param url
	 * @param methodName
	 * @param args
	 * @return
	 */
	public ResultEntity cancelService(String url, Object[] args) {
		try {
			HttpHeaders headers = new HttpHeaders();
	        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
	        headers.setContentType(type);
	        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
			ObjectMapper mapper = new ObjectMapper();
			if(args.length==1 && args[0] instanceof TxTransactionContext || args.length==0){
				HttpEntity<TxTransactionContext> formEntity = null;
				if(args.length==1){
					TxTransactionContext context = (TxTransactionContext)args[0];
					formEntity = new HttpEntity<TxTransactionContext>(context,headers);
				}else{
					formEntity = new HttpEntity<TxTransactionContext>(new TxTransactionContext(),headers);
				}
				return restTemplate.postForEntity(url, formEntity, ResultEntity.class).getBody();
			}else{
				HttpEntity<Object[]> formEntity = new HttpEntity<Object[]>(args,headers);
				return restTemplate.postForEntity(url, formEntity, ResultEntity.class).getBody();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ResultEntity re = new ResultEntity();
			re.setFlag(Constants.ResultStatus.EXCEPTION);
			re.setMessage("服务端异常或网络异常");
			return re;
		} 
	}
}
