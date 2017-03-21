package com.http.context;

import java.util.Map;
import java.util.Set;
 
public interface Request {
	
	public static final String POST = "POST";
	
	public static final String GET = "GET";
	/**
	 * 得到参数
	 */
	public Map<String, Object> getAttribute();
	public Object getAttribute(String attributeName);
	
	/**
	 * 往request域添加属性
	 */
	 public void setAttribute(String attributeName , Object value);
	 
	/**
	 * 得到请求方式
	 */
	public String getMethod();
	
	/**
	 * 得到URI
	 */
	public String getUri();

	/**
	 * 版本协议
	 */
	public String getProtocol();

	/**
	 * 得到请求头参数
	 */
	public Map<String, Object> getHeaders();
	public Set<String> getHeaderNames();
	public Object getHeader(String key);
}
