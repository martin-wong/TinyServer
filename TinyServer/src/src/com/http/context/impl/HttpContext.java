package com.http.context.impl;

import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Map;
import com.http.context.Context;
import com.http.context.Request;
import com.http.context.Response;

/*
 * HttpContext 本服务器只允许部署一个web应用，所以只有一个Context 地位类似于ServletContext 
 */  
public class HttpContext implements Context {

	private static HttpContext instance ;
	private static ThreadLocal requestLocal = new ThreadLocal<Request>();
	private static ThreadLocal responseLocal = new ThreadLocal<Response>();
	private static Map attribute = new HashMap<String,Object>();
	
	@Override
	public void setContext(String requestHeader, SelectionKey key) {
		
		//初始化request
		HttpRequest request = new HttpRequest(requestHeader,key);
		//初始化response
		HttpResponse response = new HttpResponse(key);
	
		setRequest(request);
		setResponse(response);
	}

	private void setRequest(Request request) {
		requestLocal.set(request);
	}

	private void setResponse(Response response) {
		responseLocal.set(response);

	}

	public static Context getContext() {
		synchronized (HttpContext.class) {
			if(instance == null)
				instance =  new HttpContext();
			return instance; 
		}
	}

	@Override
	public Request getRequest() {
		return (Request) requestLocal.get();
	}

	@Override
	public Response getResponse() {
		return (Response) responseLocal.get();
	}

	@Override
	public void setAttribute(String attributeName,Object value) {
		attribute.put(attributeName, value);
	}

	@Override
	public Object getAttribute(String attributeName) {
		return attribute.get(attributeName);
	}
}
