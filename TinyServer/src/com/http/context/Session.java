package com.http.context;

public abstract class Session {
	
	public abstract String getJSESSIONID() ;
	public abstract void setAttribute(String name , Object value);
	
    public abstract Object getAttribute(String name);

}
