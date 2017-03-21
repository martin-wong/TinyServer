package com.http.servlet.impl;

import com.http.context.Request;
import com.http.context.Response;
import com.http.servlet.HttpServlet;

 
public class NotFoundServlet extends HttpServlet {
	
	@Override
	public void doGet(Request request , Response response) {
		response.setStatuCode(404);
		response.setStatuCodeStr("Not Found");
		response.setHtmlFile("404.html"); //   webapps/404.html
	}
	
}
