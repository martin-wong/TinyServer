package com.http.context.impl;

import java.io.File;
import java.lang.reflect.Field;

import com.http.context.Request;
import com.http.context.Response;
import com.http.handler.MapHandler;
import com.http.servlet.GenericServlet;
import com.http.servlet.impl.NotFoundServlet;

//单例共享对象，注意线程安全性
public class RequestDispatcher {
	
	private ThreadLocal<String> local = new ThreadLocal<String>();
	private static RequestDispatcher instance ;
	private String base = File.separator+"webapps" ;
	
	private RequestDispatcher(){}

	/*
	 * 传入要转发的servlet的映射路径,这里是path应该是uri  比如/webapps/welcome
	 */
	protected synchronized static RequestDispatcher getRequestDispatcher(String path){
	    if(instance == null)
	    	instance = new RequestDispatcher();
	    instance.local.set(instance.base+path);
		return instance;
	}
	/*
	 * 转发
	 */
	public void forward(Request req , Response res){
		//转发之间不能往缓存区里写内容
		ServletOutputStream outputStream = (ServletOutputStream) res.getOutputStream();
		outputStream.getBufferList().clear();
		res.setHtmlFile("");
		
		/*
		 * 如果调用本方法就代表执行过转发，做上标记，这个标记用来保证Servlet的sendResponse只
		 * 被转发链的最后一个servlet调用
		 */
		try {
			Class<?> clazz = Class.forName("com.http.context.impl.HttpResponse");
			Field field = clazz.getDeclaredField("hasDispatchered");
			field.setAccessible(true);
			field.set(res, true);
			System.out.println(res.getHasDispatchered());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//查找对应的servlet并执行
		GenericServlet servlet = MapHandler.getContextMapInstance().getHandlerMap().get(local.get());
		if(servlet == null){
			servlet = new NotFoundServlet();
		}
		servlet.init(((HttpRequest)req).getContext());
	}
	
}
