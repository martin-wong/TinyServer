package com.http.servlet;

import java.lang.reflect.Field;

import com.http.context.Context;
import com.http.context.Request;
import com.http.context.Response;
import com.http.context.impl.HttpResponse;
import com.http.context.impl.RequestDispatcher;
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
		/*
		 * 只要调用过getRequestDispatcher ， hasDispatcher就是true
		 * 所以配合辅助标记flag 转发链的最后一个servlet会最先抵达这里它会执行sendResponse
		 * 而转发链的其他servlet都不能执行输出操作，也就是不能调用sendResponse
		 */
		boolean hasDispatcher = context.getResponse().getHasDispatchered();
		boolean flag =  context.getResponse().getFlag();
		if( ( !hasDispatcher ) || ( hasDispatcher && (!flag)) ) {
			sendResponse(context);
			System.out.println("进来啦"+"hasDispatcher"+hasDispatcher+"flag"+flag);
			HttpResponse res = (HttpResponse) context.getResponse();
			try {
				Class<?> clazz = Class.forName("com.http.context.impl.HttpResponse");
				Field field = clazz.getDeclaredField("flag");
				field.setAccessible(true);
				field.set(res, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
