package com.http.context;

import java.nio.channels.SelectionKey;

public interface Context {

	public abstract void initRequest(String requestHeader, SelectionKey key);
	
	public abstract Request getRequest() ;
	
	public abstract Response getResponse() ;
	
	public abstract void setAttribute(String attributeName,Object value);
	
	public abstract Object getAttribute(String attributeName);

}
