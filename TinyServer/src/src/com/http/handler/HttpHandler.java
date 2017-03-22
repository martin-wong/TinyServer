package com.http.handler;

import java.nio.channels.SelectionKey;

import org.apache.log4j.Logger;

import com.http.context.Context;
import com.http.context.impl.HttpContext;
import com.http.servlet.GenericServlet;
import com.http.servlet.impl.NotFoundServlet;
import com.http.servlet.impl.StaticResourceServlet;

/*
 * 处理一次Http请求 
 */  
public class HttpHandler implements Runnable {

	//就绪的键
	private SelectionKey key;
	
	private  Context context ;
	//http请求字符串
	private String requestHeader;
	//针对uri选择不同的处理器
	private GenericServlet servlet;
	private Logger logger = Logger.getLogger(HttpHandler.class);
	
	public HttpHandler(String requestHeader, SelectionKey key) {
		this.key = key;
		this.requestHeader = requestHeader;
	}

	@Override
	public void run() {
		//拿到Context对象 在这里是HttpContext
		context = HttpContext.getContext();
		//初始化request和response 以及把他们加入到context域
		context.initRequest(requestHeader, key);
		//得到uri
		String uri = context.getRequest().getUri();
		logger.info("得到了uri " + uri);
		//得到MapHandler集合(uri-->handler) 这里的handler就是一个servlet
		servlet = MapHandler.getContextMapInstance().getHandlerMap().get(uri);
		//找不到对应的servlet
		if(servlet == null) {
			//判断是不是访问静态资源 如果也不是 内部会创建NotFoundServlet并执行
			servlet = new StaticResourceServlet();
		} 
	    servlet.init(context); //初始化servlet并执行
	}
}
