package com.http.context;

import java.nio.channels.SelectionKey;

public abstract class Context {

	public abstract Request getRequest() ;
	
	public abstract Response getResponse() ;
	
	public abstract void setAttribute(String attributeName,Object value);
	
	public abstract Object getAttribute(String attributeName);

	protected abstract Session getSession(String sessionId) ;

	protected abstract void setSession(String sessionId, Session session);

	public abstract void initRequestAndResponse(String requestHeader, SelectionKey key) ;


}
