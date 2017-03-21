package com.http.servlet;

import com.http.context.Context;
import com.http.context.Request;
import com.http.context.Response;


public interface GenericServlet {
	
	public void init(Context context);

	public void service(Context context);

	public void doGet(Request request , Response response);

	public void doPost(Request request , Response response);
	
	public void destory(Context context);
}
