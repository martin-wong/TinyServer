package com.http.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.log4j.Logger;
import com.http.context.Context;
import com.http.context.Request;
import com.http.context.Response;
import com.http.context.impl.Cookie;
import com.http.context.impl.ServletOutputStream;

public class ResponseHandler {
	
	//这个字段表示用于存储响应头的字节数 暂时1024应该够了
	private static final int SIZE = 1024;
	private Request request;
	private Response response;
	private String protocol;
	private int status;
	private String statuCodeStr;
	private ByteBuffer buffer ;
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
	private List<Cookie> cookies;
	
	public ResponseHandler() {
		super();
	}

	public void write(Context context) {
		//初始化成员变量参数
		init(context);
		//如果是发送html，得到响应正文内容,否则返回null
		String html = setHtml();
		StringBuilder sb = new StringBuilder();
		//构造状态行
		makeResponseLine(sb);
		//构造响应头
		makeResponseHeader(sb);
		//构造响应体内容 使用了outputStream就不能再指定发送一个html文件
		makeResponseBody(html, sb);
		
		try {
			logger.info("生成响应头\r\n" + sb.toString());
			//注册写事件
			if(channel.isConnected()){
				channel.register(selector, SelectionKey.OP_WRITE,this.bufferList);
				//恢复对读的感兴趣
				key.interestOps(key.interestOps() | SelectionKey.OP_READ);
			}
		} catch (ClosedChannelException e) {
			e.printStackTrace();
			try {
				channel.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
			
	}

	private void makeResponseBody(String html, StringBuilder sb) {
		int size = bufferList.size();
		if(size > 0){
			int length = 0;
			for (int i = 0; i < size; i++) {
				ByteBuffer b = (ByteBuffer) bufferList.get(i);
				length += b.position();
			}
			sb.append("Content-Length: " + length + "\r\n");
			//把上面的内容写到缓存区buffer
			buffer = ByteBuffer.allocate(SIZE);
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
				buffer = ByteBuffer.allocate(html.getBytes().length+SIZE);
				buffer.put(sb.toString().getBytes());
				bufferList.add(buffer);
			}else{
				sb.append("\r\n");
				buffer = ByteBuffer.allocate(SIZE);
				buffer.put(sb.toString().getBytes());
				bufferList.add(buffer);
			}
		}
	}

	private void makeResponseHeader(StringBuilder sb) {
		sb.append("Server: " + serverName + "\r\n");
		sb.append("Content-Type: " + contentType + "\r\n");
		if(Content_Disposition != null)
		   sb.append("Content-Disposition: " + Content_Disposition + "\r\n");
		sb.append("Date: " + new Date() + "\r\n");
		
		//构造set-cookie响应头
		int len = cookies.size();
		for (int i = 0; i < len; i++) {
			sb.append("Set-Cookie: ");
			Cookie c = (Cookie)cookies.get(i);
			if(c.getMaxAge() > 0){
			   sb.append(c.getName()+"="+c.getValue()+";"+" Path="+c.getPath()+";"+" Max-Age="+c.getMaxAge()+";"+" Version=1"+"\r\n");
			}else{
			   sb.append(c.getName()+"="+c.getValue()+";"+" Path="+c.getPath()+";"+" Version=1"+"\r\n");
			}
		}
	}

	private void makeResponseLine(StringBuilder sb) {
		if(response.getLocation() != null){
			sb.append(protocol + " " + "302" + " " + "Found" + "\r\n");
			sb.append("Location: " + response.getLocation() + "\r\n");
		}else{
		    sb.append(protocol + " " + status + " " + statuCodeStr + "\r\n");
		}
	}

	private void init(Context context) {
		//从context中得到相应的参数
		request = context.getRequest();
		response = context.getResponse();
		protocol = request.getProtocol();
		status = response.getStatus();
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
		cookies = response.getCookies();
	}

	private String setHtml() {
		StringBuilder html = null;
		if(htmlFile != null && htmlFile.length() > 0) {
			
			html = new StringBuilder();
			
			try {
				if("404.html".equals(htmlFile)){
					String htmlContent = new String("<!doctype html>\r\n <html lang=\"en\"> \r\n"+
								 "<head>\r\n" +
								  "<meta charset=\"UTF-8\">\r\n"+
								 " <title>404错误</title>\r\n" +
								 "</head>\r\n" +
								" <body>\r\n" +
								 " <h1>404 NOT Found</h1>\r\n" +
								 " <strong style=\"color:red;\">来自Tiny Server</strong>\r\n" +
								" </body>\r\n" +
								"</html>"); 
					return htmlContent ;
				}else
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
