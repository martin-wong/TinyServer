package com.http.server;


public class ServerStart {
	
	//启动方法
	public static void main(String[] args) {
		
		new Thread(new Server(false)).start();
	}
}
