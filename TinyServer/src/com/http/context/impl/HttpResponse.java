package com.http.context.impl;

import java.io.File;
import java.io.OutputStream;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.List;

import com.http.context.Context;
import com.http.context.Response;
import com.http.context.Session;

public class HttpResponse implements Response {
	
	private SelectionKey key;
	private List<Cookie> Cookies = new ArrayList<Cookie>();
	//内容类型  defalut 为text/html
	private String contentType = "text/html";
	private String Content_Disposition ;
	//响应码  defalut 为200
	private int Status = 200;
	private String statuCodeStr = "OK";
	private String htmlFile = "";
	private Context context;
	private OutputStream outputStream = new ServletOutputStream(this);
	private boolean hasDispatchered = false; //默认是没有进行转发
	private boolean flag = false;
	private String location;

	public HttpResponse(HttpContext instance, SelectionKey key) {
		this.key = key;
		this.context = instance;
		initCookies();
	}
	
	private void initCookies() {
		Session session = context.getRequest().getSession();
		if(session!=null){
			String jsessionid = session.getJSESSIONID();
			Cookie c = new Cookie("JSESSIONID", jsessionid);
			c.setMaxAge(-1);
			c.setPath("/");
			Cookies.add(c);
		}
	}
	
	@Override
	public void setCookie(Cookie cookie){
		Cookies.add(cookie);
	}
	
	@Override
	public List<Cookie> getCookies(){
		return Cookies;
	}

	@Override
	public boolean getHasDispatchered(){
		return hasDispatchered;
	}
	
	@Override
	public boolean getFlag() {
		return flag;
	}	
	
	@Override
	public OutputStream getOutputStream() {
		return outputStream;
	}

	@Override
	public String getContentType() {
		return contentType;
	}
	
	@Override
	public String getContent_Disposition() {
		return Content_Disposition;
	}
	 

	@Override
	public int getStatus() {
		return Status;
	}
	
	@Override
	public void setStatus(int Status) {
		this.Status =  Status;
	}

	@Override
	public SelectionKey getKey() {
		return key;
	}

	@Override
	public String getStatuCodeStr() {
		return statuCodeStr;
	}

	@Override
	public String getHtmlFile() {
		return htmlFile;
	}

	@Override
	public void setHtmlFile(String htmlFile) {
		this.htmlFile = htmlFile;
	}

	@Override
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}


	@Override
	public void setStatuCodeStr(String statuCodeStr) {
		this.statuCodeStr = statuCodeStr;
	}


	@Override
	public String setContent_Disposition(String Content_Disposition) {
		return this.Content_Disposition = Content_Disposition;
	}

	//返回服务器运行的路径
	public String getPath(){
		File file = new File("path");
	    StringBuilder ABSOLUTEPATH = new StringBuilder(file.getAbsolutePath());
	    int size = ABSOLUTEPATH.length();
	    String path = null ;
	    int count = 0 ;
	    for (int i = size-1; i > -1; i--) {
	    	if(ABSOLUTEPATH.charAt(i) == File.separator.charAt(0)){
	    	  count++;
	    	}
	    	if(count == 1){
	    		path = ABSOLUTEPATH.substring(0, i);
	    		break;
	    	}
		}
	   return path; 
	}
	
	@Override
	public void sendRedirect(String uri) {
		 this.Status = 302;
         this.location = uri;		
	}
	
	@Override
	public void setLocation(String uri) {
         this.location = uri;		
	}
	
	@Override
	public String getLocation() {
         return this.location;		
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((Content_Disposition == null) ? 0 : Content_Disposition
						.hashCode());
		result = prime * result + Status;
		result = prime * result
				+ ((contentType == null) ? 0 : contentType.hashCode());
		result = prime * result
				+ ((htmlFile == null) ? 0 : htmlFile.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result
				+ ((outputStream == null) ? 0 : outputStream.hashCode());
		result = prime * result
				+ ((statuCodeStr == null) ? 0 : statuCodeStr.hashCode());
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
		HttpResponse other = (HttpResponse) obj;
		if (Content_Disposition == null) {
			if (other.Content_Disposition != null)
				return false;
		} else if (!Content_Disposition.equals(other.Content_Disposition))
			return false;
		if (Status != other.Status)
			return false;
		if (contentType == null) {
			if (other.contentType != null)
				return false;
		} else if (!contentType.equals(other.contentType))
			return false;
		if (htmlFile == null) {
			if (other.htmlFile != null)
				return false;
		} else if (!htmlFile.equals(other.htmlFile))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (outputStream == null) {
			if (other.outputStream != null)
				return false;
		} else if (!outputStream.equals(other.outputStream))
			return false;
		if (statuCodeStr == null) {
			if (other.statuCodeStr != null)
				return false;
		} else if (!statuCodeStr.equals(other.statuCodeStr))
			return false;
		return true;
	}


}
