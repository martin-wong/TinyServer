package com.http.context;

import java.io.OutputStream;
import java.nio.channels.SelectionKey;

import com.http.context.impl.ServletOutputStream;
import com.http.utils.XMLUtil;

public interface Response {
	
	//服务器名字
	public static final String SERVER_NAME = XMLUtil.getRootElement("server.xml").element("serverName").getText();
	
	public String getContentType();
	
	public boolean getFlag();
	
	public boolean getHasDispatchered();
	
	public int getStatuCode();
	
	public String getStatuCodeStr();
	
	public String getHtmlFile();
	
	public OutputStream getOutputStream();

	public String getContent_Disposition();
	
	public SelectionKey getKey();
	//拿到服务器进程所在的执行路径
	public String getPath();
	//设置要发送给客户端的html文件所在路径
	public void setHtmlFile(String htmlFile);
	//设置ContentTyp响应头  audio/mpeg ...
	public void setContentType(String contentType);
	//设置状态码  404 200...
	public void setStatuCode(int statuCode);
	//设置状态信息 NOT FOUND   OK  ...
	public void setStatuCodeStr(String statuCodeStr);
	//设置Content-Disposition响应头  "attachment;filename=1.txt"
	public String setContent_Disposition(String Content_Disposition);
	
}
