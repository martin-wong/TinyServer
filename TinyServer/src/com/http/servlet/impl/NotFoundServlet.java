package com.http.servlet.impl;

import org.apache.log4j.Logger;
import com.http.context.Request;
import com.http.context.Response;
import com.http.servlet.HttpServlet;

 
public class NotFoundServlet extends HttpServlet {
	
	private Logger logger = Logger.getLogger(NotFoundServlet.class);
	
	@Override
	public void doGet(Request request , Response response) {
		
		response.setStatus(404);
		response.setStatuCodeStr("Not Found");
		logger.info("找不到请求的资源 即将返回404.html");
		response.setHtmlFile("404.html"); 
	}
	
}
