package com.http.context.impl;

import java.util.HashMap;
import java.util.Map;

import com.http.context.Session;

public class HttpSession extends Session {

	private String JSESSIONID ;
	private Map attribute;
	
	protected HttpSession(String JSESSIONID) {
		super();
		this.JSESSIONID = JSESSIONID;
		attribute = new HashMap<String,Object>();
	}
	
	public void setAttribute(String name , Object value){
		attribute.put(name, value);
	}
	
    public Object getAttribute(String name){
    	return attribute.get(name);
	}

	public String getJSESSIONID() {
		return JSESSIONID;
	}
    
	
}
