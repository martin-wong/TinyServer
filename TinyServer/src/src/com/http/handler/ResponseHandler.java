package com.http.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.http.context.Context;
import com.http.context.Request;
import com.http.context.Response;
import com.http.context.impl.ServletOutputStream;
import com.http.utils.ClassLoaderExpand;

public class ResponseHandler {
	
	private Request request;
	private Response response;
	private String protocol;
	private int statuCode;
	private String statuCodeStr;
	private ByteBuffer buffer = ByteBuffer.allocate(1024*1024*20);
	private String serverName;
	private String contentType;
	private String Content_Disposition;
	private SocketChannel channel;
	private Selector selector;
	private SelectionKey key;
	private Logger logger = Logger.getLogger(ResponseHandler.class);
	private BufferedReader reader;
	private String htmlFile;
	private List bufferList;
	private String path;
	
	public ResponseHandler() {
		super();
	}

	public void write(Context context) {
		//从context中得到相应的参数
		request = context.getRequest();
		response = context.getResponse();
		protocol = request.getProtocol();
		statuCode = response.getStatuCode();
		statuCodeStr = response.getStatuCodeStr();
		serverName = Response.SERVER_NAME;
		contentType = response.getContentType();
		Content_Disposition = response.getContent_Disposition();
		path = response.getPath();
		key = response.getKey();
		selector = key.selector();
		channel = (SocketChannel)key.channel();
		htmlFile = response.getHtmlFile();//要输出的html文件所在的路径
		bufferList = ((ServletOutputStream)response.getOutputStream()).getBufferList();
		
		//得到响应正文内容
		String html = setHtml(context);
		
		StringBuilder sb = new StringBuilder();
		//构造状态行
		sb.append(protocol + " " + statuCode + " " + statuCodeStr + "\r\n");
		//构造响应头
		sb.append("Server: " + serverName + "\r\n");
		sb.append("Content-Type: " + contentType + "\r\n");
		if(Content_Disposition != null)
		   sb.append("Content-Disposition: " + Content_Disposition + "\r\n");
		sb.append("Date: " + new Date() + "\r\n");
		
		
		
		//构造响应内容 使用了outputStream就不能再指定发送一个html文件
		int size = bufferList.size();
		if(size > 0){
			int length = 0;
			for (int i = 0; i < size; i++) {
				ByteBuffer b = (ByteBuffer) bufferList.get(i);
				length += b.position();
			}
			sb.append("Content-Length: " + length + "\r\n");
			//把上面的内容写到缓存区buffer
			buffer.put(sb.toString().getBytes());
			buffer.put("\r\n".getBytes()); //响应头和响应体之间有一个空行
			bufferList.add(buffer);
		}else{
			if(html != null){
				if(reader != null) {
					sb.append("Content-Length: " + html.getBytes().length + "\r\n");
				}
				sb.append("\r\n");
				sb.append(html);
				buffer.put(sb.toString().getBytes());
				bufferList.add(buffer);
			}else{
				sb.append("\r\n");
				buffer.put(sb.toString().getBytes());
				bufferList.add(buffer);
			}
		}
		
		
		try {
			logger.info("生成响应\r\n" + sb.toString());
			//注册写事件
			channel.register(selector, SelectionKey.OP_WRITE,this.bufferList);
			//恢复对读的感兴趣
			key.interestOps(key.interestOps() | SelectionKey.OP_READ);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String setHtml(Context context) {
		StringBuilder html = null;
		if(htmlFile != null && htmlFile.length() > 0) {
			
			html = new StringBuilder();
			
			try {
				if("404.html".equals(htmlFile))
					reader = new BufferedReader(new FileReader(new File(htmlFile)));
				else
				    reader = new BufferedReader(new FileReader(new File(path+File.separator+"webapps"+File.separator+htmlFile)));
				
				String htmlStr;
				htmlStr = reader.readLine();
				while(htmlStr != null) {
					html.append(htmlStr + "\r\n");
					htmlStr = reader.readLine();
				}
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return html==null?null:html.toString();
	}

	
}
