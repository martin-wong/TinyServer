package com.http.handler;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import com.http.servlet.GenericServlet;
import com.http.servlet.impl.FaviconServlet;
import com.http.utils.ClassLoaderExpand;
import com.http.utils.XMLUtil;

/*
 *  HandlerMap(单例) 读取web.xml从而记住什么uri交给哪个类去处理
 */  
public class MapHandler {
	
	//访问路径对应控制类  map<uri,class>
	private static Map<String, GenericServlet> handlerMap = new HashMap<>();
	//web.xml配置映射/download 但地址栏需要输入/webapps/downlooad
	private static final String WEBAPPS = File.separator+"webapps";
	private static MapHandler instance = null; 
	
	//将构造器私有化
	private MapHandler(){}
	
	//得到HandlerMap对象实例
	public static MapHandler getContextMapInstance() {
		
		if(instance == null) {
			synchronized (MapHandler.class) {
				if(instance == null) {
					instance = new MapHandler();
					//得到web.xml的路径
					Element rootElement = XMLUtil.getRootElement("webapps"+File.separator+"WEB-INF"+File.separator+"web.xml");
					//得到handler的集合
					List<Element> handlers = XMLUtil.getElements(rootElement);
					for (Element element : handlers) {
						Element urlPattenEle = XMLUtil.getElement(element, "url-patten");
						//得到urlPatten(uri)和对应的处理类
						String urlPatten = WEBAPPS+XMLUtil.getElementText(urlPattenEle);
						Element handlerClazzEle = XMLUtil.getElement(element, "handler-class");
						//得到handler 的class权限类名
						String clazzPath = XMLUtil.getElementText(handlerClazzEle);
					    Class<?> clazz = null;
						try {
							//通过反射得到handler实例化对象，然后以键值对的形式存储
							ClassLoaderExpand clod = new ClassLoaderExpand();
							//指定java class 文件目录 webapps/WEB-INF/classes/
							clod.setLocation("webapps"+File.separator+"WEB-INF"+File.separator+"classes"+File.separator);
							//调用 通过字节流生产java类
							clazz = clod.findClass(clazzPath);
							GenericServlet handler = (GenericServlet)clazz.newInstance();
							instance.getHandlerMap().put(urlPatten, handler);
							Logger.getLogger(MapHandler.class).info("成功添加Handler " + clazzPath);
							//加载用户lib目录下的jar包
							addLibrary("webapps"+File.separator+"WEB-INF"+File.separator+"lib"+File.separator);
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						} catch (InstantiationException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
					//额外添加一个响应uri==/favicon.ico的servlet
					instance.getHandlerMap().put("/favicon.ico", new FaviconServlet());
					
				}
			}
		}
			
		return instance;
	}
	//加载用户WEB-INF/lib下的jar
	private static void addLibrary(String path) {
		
		File parent = new File(path);
		File[] jarFiles = null ;
		if(parent.exists() && parent.isDirectory()){
			//找出.jar结尾的jar包文件
			jarFiles = parent.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					if(!pathname.getName().endsWith(".jar"))
					   return false;
					else
					return true;
				}
			});
		}
		
		if (jarFiles != null) {  
		    // 从URLClassLoader类中获取类所在文件夹的方法  
			Method method = null;
			boolean accessible = false;
			URLClassLoader classLoader = null ;
			try {
				method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
				accessible = method.isAccessible();     // 获取方法的访问权限  
			    classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader(); 
			} catch (NoSuchMethodException e2) {
				e2.printStackTrace();
			} catch (SecurityException e2) {
				e2.printStackTrace();
			}  
		     
		    try {  
		        if (accessible == false) {  
		            method.setAccessible(true);     // 设置方法的访问权限  
		        }  
		        // 获取系统类加载器  
		        for (File file : jarFiles) {  
		            URL url = null;
					try {
						url = file.toURI().toURL();
					} catch (MalformedURLException e1) {
						e1.printStackTrace();
					}  
		            try {  
		                method.invoke(classLoader, url);    
		            } catch (Exception e) { 
		            	e.printStackTrace();
		            }  
		        }  
		    } finally {  
		        method.setAccessible(accessible);  
		    }  
		}
		
	}

	public Map<String, GenericServlet> getHandlerMap() {
		return handlerMap;
	}
}
