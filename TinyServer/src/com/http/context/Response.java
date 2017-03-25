package com.http.context;

import java.io.File;
import java.io.OutputStream;
import java.nio.channels.SelectionKey;
import java.util.List;

import com.http.context.impl.Cookie;
import com.http.context.impl.ServletOutputStream;
import com.http.utils.XMLUtil;

public interface Response {
	
	//服务器名字
	public static final String SERVER_NAME = XMLUtil.getRootElement("conf"+File.separator+"server.xml").element("serverName").getText();
	
	
	
	public boolean getFlag();
	public boolean getHasDispatchered();
	public OutputStream getOutputStream();
	public SelectionKey getKey();
	public void sendRedirect(String uri);
	
	//拿到服务器进程所在的执行路径
	public String getPath();
	//设置要发送给客户端的html文件所在路径
	public void setHtmlFile(String htmlFile);
	public String getHtmlFile();
	//设置ContentTyp响应头  audio/mpeg ...
	public void setContentType(String contentType);
	public String getContentType();
	//设置状态码  404 200...
	public void setStatus(int Status);
	//设置状态信息 NOT FOUND   OK  ...
	public void setStatuCodeStr(String statuCodeStr);
	public String getStatuCodeStr();
	public int getStatus();
	//设置Content-Disposition响应头  "attachment;filename=1.txt"
	public String setContent_Disposition(String Content_Disposition);
	public String getContent_Disposition();
    //Cookie
	public void setCookie(Cookie cookie);
	public List<Cookie> getCookies();
	
	public void setLocation(String uri);
	public String getLocation();

}
