package com.http.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ClassLoaderExpand extends ClassLoader {

	/*
	 *  传进来的name 是全限定名   在web.xml里定义
	 */
	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException {
		//载入类的二进制数据 
		byte[] datas = loadClassData(name);
		//第一个参数是全限定名（包名+类名）
		return defineClass(name, datas, 0, datas.length);
	}

	// 指定类文件目录   webapps/WEB-INF/classes
	private static String location;

	public static String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;  // webapps/
	}

	/*
	 * 返回webapps文件夹所在的目录路径
	 * 例子：file路径 /home/wong/Document/github/path
	 * 返回： /home/wong/Document/github
	 */
	protected String getPath(){
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
	
	protected byte[] loadClassData(String name) {
		name = name.replace('.', File.separator.charAt(0));
		FileInputStream fis = null;
		byte[] datas = null;
		try {
			fis = new FileInputStream(getPath() + File.separator+location + name + ".class");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int b;
			while ((b = fis.read()) != -1) {
				bos.write(b);
			}
			datas = bos.toByteArray();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return datas;

	}
}
