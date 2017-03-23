package com.http.context.impl;

import java.net.URLEncoder;
import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.http.context.Context;
import com.http.context.Request;
import com.http.context.Session;

public class HttpRequest implements Request {
	
	//请求方法 GET
	private String method;
	
	//uri  /webapps/login
	private String uri;
	
	//HTTP协议版本  HTTP/1.1
	private String protocol;
	
	private Context context;

	//保存请求头的集合 Content-Length: 25
	private Map<String, String> headers = new HashMap<>();
	
	//保存Cookie
	private Map<String,String> Cookies ;
	
	//Request域集合
	private Map<String, Object> attribute = new HashMap<>();

	private Session session;
	
	public HttpRequest(HttpContext instance, String requestHeader, SelectionKey key) {
		init(instance,requestHeader,key); //HttpRequest被创建后需要初始化一些参数
	}

	private void init(HttpContext instance, String httpHeader,SelectionKey key) {
		//将请求分行
		String[] headers = httpHeader.split("\r\n");
		//设置请求方式
		initMethod(headers[0]);
		//设置URI
		initURI(headers[0]);
		//设置版本协议 
		initProtocol(headers[0]);
		//设置请求头 
		initRequestHeaders(headers);
		//解析Cookie
		parseCookie();
		//设置Context
		initContext(instance);
		//建立Session
		initSession();
	}

	//目前的方法会造成内存泄漏，因为创建的Session加入Context域后没有做销毁，连接数一大会内存泄露
	private void initSession() {
		String jsessionid = Cookies.get("JSESSIONID");
		if(jsessionid == null || "".equals(jsessionid)){
			jsessionid = URLEncoder.encode(((Double)Math.random()).toString());
			 session = new HttpSession(jsessionid);
			((HttpContext)context).setSession(jsessionid,session );
		}else{
			session = ((HttpContext)context).getSession(jsessionid);
		}
	}

	private void parseCookie() {
		Cookies = new HashMap<String,String>(); 
		String cookie = headers.get("Cookie");
		if(cookie!=null){
			cookie = cookie.trim();
			String[] cookies = cookie.split(";");
			int size = cookies.length;
			for (int i = 0; i < size; i++) {
				String[] nameAndValue = cookies[i].split("=");
				Cookies.put(nameAndValue[0].trim(), nameAndValue[1].trim());
	 		}
		}
		
		
	}

	private void initContext(HttpContext instance) {
		context = instance ;
	}

	/*
	 * 设置请求方法
	 */
	private void initMethod(String str) {
		method = str.substring(0, str.indexOf(" ")); //GET /login HTTP/1.1
	}
	
	/*
	 * 设置request参数
	 */
	private void initAttribute(String attr) {
		String[] attrs = attr.split("&");
		for (String string : attrs) {
			String key = string.substring(0, string.indexOf("="));
			String value = string.substring(string.indexOf("=") + 1);
			attribute.put(key, value);
		}
	}

	/*
	 * 设置uri
	 */
	private void initURI(String str) {
		//GET /login HTTP/1.1   拿到/login
		uri = str.substring(str.indexOf(" ") + 1, str.indexOf(" ", str.indexOf(" ") + 1));
		//如果是get方法，则后面跟着参数   /login?a=1&b=2
		if("GET".equals(method.toUpperCase())) {
			//有问号表示后面跟有参数
			if(uri.contains("?")) {
				String attr = uri.substring(uri.indexOf("?") + 1, uri.length());
				uri = uri.substring(0, uri.indexOf("?"));
				initAttribute(attr);
			}
		}else if("POST".equals(method.toUpperCase())){
			
			//todo 解析post请求的参数  并存入attribute
		
		}
	}
	
	/*
	 * 把请求头参数存入集合存起来
	 */
	private void initRequestHeaders(String[] strs) {
		//去掉第一行
		for(int i = 1; i < strs.length; i++) {
			String key = strs[i].substring(0, strs[i].indexOf(":"));
			String value = strs[i].substring(strs[i].indexOf(":") + 1);
			headers.put(key, value);
		}
	}
	
	/*
	 * 设置协议版本 HTTP/1.1
	 */
	private void initProtocol(String str) {
		//POST /login HTTP/1.1
		protocol = str.substring(str.lastIndexOf(" ") + 1, str.length());
	}
	
	@Override
	public RequestDispatcher getRequestDispatcher(String path){
		return RequestDispatcher.getRequestDispatcher(path);
	}
	
	@Override
	public Map<String, Object> getAttribute() {
		return attribute;
	}

	@Override
	public Object getAttribute(String attributeName) {
		return attributeName == null?null:attribute.get(attributeName);
	}

	@Override
	public void setAttribute(String attributeName, Object value) {
		attribute.put(attributeName, value);
	}
	
	@Override
	public Map<String, String> getCookies() {
		return Cookies;
	}
	
	public String getCookie(String CookieName) {
		return Cookies.get(CookieName);
	}

	@Override
	public Session getSession() {
		return this.session;
	}
	
	@Override
	public String getMethod() {
		return method;
	}

	@Override
	public String getUri() {
		return uri;
	}

	@Override
	public String getProtocol() {
		return protocol;
	}

	@Override
	public Map<String, String> getHeaders() {
		return headers;
	}

	@Override
	public Set<String> getHeaderNames() {
		return headers.keySet();
	}

	@Override
	public Object getHeader(String key) {
		return headers.get(key);
	}
	
	@Override
	public Context getContext() {
		return context;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attribute == null) ? 0 : attribute.hashCode());
		result = prime * result + ((headers == null) ? 0 : headers.hashCode());
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result
				+ ((protocol == null) ? 0 : protocol.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HttpRequest other = (HttpRequest) obj;
		if (attribute == null) {
			if (other.attribute != null)
				return false;
		} else if (!attribute.equals(other.attribute))
			return false;
		if (headers == null) {
			if (other.headers != null)
				return false;
		} else if (!headers.equals(other.headers))
			return false;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		if (protocol == null) {
			if (other.protocol != null)
				return false;
		} else if (!protocol.equals(other.protocol))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}
}
