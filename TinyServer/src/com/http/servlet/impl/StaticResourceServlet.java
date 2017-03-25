package com.http.servlet.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.http.context.Request;
import com.http.context.Response;
import com.http.handler.HttpHandler;
import com.http.servlet.HttpServlet;

public class StaticResourceServlet extends HttpServlet {
	
	private Logger logger = Logger.getLogger(StaticResourceServlet.class);

	@Override
	public void doGet(Request request, Response response) {
		//   例如html里的<img src="/picture/1.jpg" alt="陈楚生"> 会传入/picture/1.jpg
		String URI = request.getUri();
		if(URI == null || "".equals(URI) || URI.contains("WEB-INF")){
			request.getRequestDispatcher(" ").forward(request, response); //404
			return ;
		}   
		String path = response.getPath()+File.separator+"webapps" + URI;
		File file = new File(path);
		if(!file.exists() || file.isDirectory() ){
			request.getRequestDispatcher(" ").forward(request, response);//404
			return ;
		}
		FileInputStream in = null ;
		OutputStream out = null ;
		try {
			logger.info("开始传送静态资源");
			 in = new FileInputStream(file);
			 out = response.getOutputStream();
			 byte[] b = new byte[1024];
			 int len = 0;
			 while((len=in.read(b)) != -1){
				 out.write(b, 0, len);
			 }
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
