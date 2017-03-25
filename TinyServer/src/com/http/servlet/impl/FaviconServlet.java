package com.http.servlet.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.http.context.Request;
import com.http.context.Response;
import com.http.servlet.HttpServlet;

//响应浏览器请求网站图标的servlet  
public class FaviconServlet extends HttpServlet {
	
	private Logger logger = Logger.getLogger(FaviconServlet.class);

	@Override
	public void doGet(Request request, Response response) {
	
		//网站图标应该放在conf文件夹下
		String path = response.getPath() + File.separator + "conf" + File.separator +"favicon.con";
		File file = new File(path);
		if(!file.exists() || file.isDirectory()){
			logger.info("浏览器请求发送网站图标，但图标文件找不到："+path);
			new NotFoundServlet().init(context);
			return ;
		}
		FileInputStream in = null ;
		OutputStream out = null ;
		try {
			 in = new FileInputStream(file);
			 out = response.getOutputStream();
			 byte[] b = new byte[1024];
			 int len = 0;
			 while((len=in.read(b)) != -1){
				 out.write(b, 0, len);
			 }
			 logger.info("浏览器请求发送网站图标，正在传送......");
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
