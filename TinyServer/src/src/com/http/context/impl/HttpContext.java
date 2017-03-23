package com.http.context.impl;

import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Map;

import com.http.context.Context;
import com.http.context.Request;
import com.http.context.Response;
import com.http.context.Session;

/*
 * HttpContext 本服务器只允许部署一个web应用，所以只有一个Context 地位类似于ServletContext 
 */  
public class HttpContext extends Context {

	//本对象是共享且单例的，注意线程安全问题
	private static HttpContext instance ;
	/*因为每次请求，都是单独的一条线程来处理，所以在当次请求内get拿到的都是当次的request/response
      对于不同的请求是不同的线程处理，所以数据不会乱
     */
	private static ThreadLocal requestLocal = new ThreadLocal<Request>();
	private static ThreadLocal responseLocal = new ThreadLocal<Response>();
	//Context域的集合，这里的对象都是整个运行周期内共享的
	private static Map attribute = new HashMap<String,Object>();
	//保存Session的集合
	private static Map sessions = new HashMap<String,Session>();
	
	private HttpContext(){}
	
	@Override
	public void initRequest(String requestHeader, SelectionKey key) {
		//初始化request
		HttpRequest request = new HttpRequest(instance,requestHeader,key);
		setRequest(request);
		//初始化response
		HttpResponse response = new HttpResponse(instance,key);
		setResponse(response);
	}
	
	public static Context getContext() {
		synchronized (HttpContext.class) {
			if(instance == null)
				instance =  new HttpContext();
			return instance; 
		}
	}
	
	private void setRequest(Request request) {
		requestLocal.set(request);
	}

	private void setResponse(Response response) {
		responseLocal.set(response);

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
	public synchronized void setAttribute(String attributeName,Object value) {
		attribute.put(attributeName, value);
	}

	@Override
	public Object getAttribute(String attributeName) {
		return attribute.get(attributeName);
	}
	
	@Override
	protected Session getSession(String sessionId) {
		return (Session) sessions.get(sessionId);
	}
	
	@Override
	protected void setSession(String sessionId , Session session) {
		sessions.put(sessionId, session);
	}
	
	
}
