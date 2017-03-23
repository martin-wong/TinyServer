package com.http.servlet.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.http.context.Request;
import com.http.context.Response;
import com.http.servlet.HttpServlet;

//响应浏览器请求网站图标的servlet  
public class FaviconServlet extends HttpServlet {

	@Override
	public void doGet(Request request, Response response) {
	
		//网站图标应该放在根目录 也就是和webapps同级的目录
		String path = response.getPath() + File.separator +"favicon.con";
		File file = new File(path);
		if(!file.exists() || file.isDirectory()){
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
