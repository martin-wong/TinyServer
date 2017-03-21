package com.http.servlet;

import com.http.context.Context;
import com.http.context.Request;
import com.http.context.Response;
import com.http.context.impl.HttpRequest;
import com.http.context.impl.HttpResponse;
import com.http.handler.ResponseHandler;

 
public class HttpServlet implements GenericServlet {
	
	protected Context context;
	
	@Override
	public void init(Context context) {
		this.context = context;
		this.service(context);
	}
	
	@Override
	public void service(Context context) {
		//通过请求方式选择是doGET方法还是doPOST方法
		String method = context.getRequest().getMethod();
		if(method.equals(Request.GET)) {
			this.doGet(context.getRequest(),context.getResponse());
		} else if (method.equals(Request.POST)) {
			this.doPost(context.getRequest(),context.getResponse());
		}
		sendResponse(context);
	}

	@Override 
	public void doGet(Request request , Response response) {
		
	}

	@Override
	public void doPost(Request request , Response response) {
		
	}

	@Override
	public void destory(Context context) {
		context = null;
	}


	private void sendResponse(Context context) {
		new ResponseHandler().write(context);
	}
	
	public Context getHttpContext(){
		return context;
	}
}
