package com.http.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import com.http.handler.HttpHandler;
import com.http.utils.XMLUtil;

 
public class Server implements Runnable {

	//声明一个10240大小的缓冲区 用来存放http请求内容
    private ByteBuffer buffer = ByteBuffer.allocate(10240);  
    
	private boolean interrupted = false;
	
	private Logger logger = Logger.getLogger(Server.class);
	
	public Server(boolean interrupted) {
		this.interrupted = interrupted;
	}

	@Override
	public void run() {
		try {
			//打开一个选择器
			Selector selector = Selector.open();
			//打开ServerSocketChannel通道
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			//得到ServerSocket对象
			ServerSocket serverSocket = serverSocketChannel.socket();
			//ServerSocketChannel通道监听server.xml中设置的端口 默认端口8080
			serverSocket.setReuseAddress(true);  
			String port = "8080";
			try {
				 Element e = XMLUtil.getRootElement("server.xml").element("port");
				 if(e != null)
					port = e.getText(); 
				serverSocket.bind(new InetSocketAddress(Integer.parseInt(port)));
				System.out.println("listening on port "+port);
			} catch (Exception e) {
				logger.error("绑定端口失败,请检查server.xml中port属性是否被正确设置");
				return;
			}
			logger.info("成功绑定端口" + port);
			//将通道设置为非阻塞模式
			serverSocketChannel.configureBlocking(false);
			//将serverSocketChannel注册给选择器,并绑定ACCEPT事件
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

			logger.info("服务器启动成功");
			while(!interrupted) {
				//查询就绪的通道数量
				int readyChannels = selector.select(10);
				//没有任何就绪的则继续进行循环
				if(readyChannels == 0)
					continue;
				//获得就绪的selectionKey的set集合
				Set<SelectionKey> keys = selector.selectedKeys();
				//获得set集合的迭代器
				Iterator<SelectionKey> iterator = keys.iterator();
				while(iterator.hasNext()) {
					SelectionKey key = iterator.next();
					if(key.isAcceptable()) {
						//该key有ACCEPT事件 ，说明有外界连接连入
						//拿到发生了accept事件的ServerSocketChannel
						ServerSocketChannel server = (ServerSocketChannel) key.channel();
						//得到接收到的SocketChannel
						SocketChannel socketChannel = server.accept();
						if(socketChannel != null) {
							logger.info("收到了来自" + ((InetSocketAddress)socketChannel.getRemoteAddress()).getHostString()
									+ "的请求");
							//将socketChannel设置为非阻塞模式
							socketChannel.configureBlocking(false);
							//将socketChannel注册读事件到选择器
							socketChannel.register(selector, SelectionKey.OP_READ);
						}
					} 
					if (key.isReadable()) {
						//该key有Read事件 意味着客户端开始发送数据，这里的SocketChannel是上面注册了读事件的SocketChannel
						key.interestOps(key.interestOps() & (~SelectionKey.OP_READ));
						SocketChannel socketChannel = (SocketChannel) key.channel();
						String requestHeader = "";
						//拿出通道中的Http头请求
						try {
							requestHeader = receive(socketChannel);
						} catch (Exception e) {
							logger.error("读取Http头出错");
							continue;
						}
						//启动线程处理该请求,if条件判断一下，防止心跳包
						if(requestHeader.length() > 0) {
							logger.info("该请求的头格式为\r\n" + requestHeader);
							logger.info("启动了子线程..");
							new Thread(new HttpHandler(requestHeader, key)).start();
						}
						
					} 
					if (key.isWritable()) {
						//该key有Write事件
						key.interestOps(key.interestOps() & (~SelectionKey.OP_WRITE));
						logger.info("有流写出!");
						SocketChannel socketChannel = (SocketChannel) key.channel();
						ByteBuffer buffer =  (ByteBuffer) key.attachment();
						try{
							//从写模式，切换到读模式，让channel读出去（写事件）
							buffer.flip();
							while(buffer.hasRemaining())
								socketChannel.write(buffer);
						}catch(Exception e){
							
						}finally{
					       socketChannel.close();
						}
					}
					//这行不能少
					iterator.remove();
				}
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    private String receive(SocketChannel socketChannel) throws Exception {
    
        byte[] bytes = null;  
        int size = 0;
        //定义一个字节数组输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
        //将socketChannel中的数据写入到buffer中  size为写了多少个字节
        while ((size = socketChannel.read(buffer)) > 0) {
        	buffer.flip();
            bytes = new byte[size];
            //将Buffer本次读入的数据写入到字节数组中
            buffer.get(bytes);
            //将字节数组写入到字节缓冲流中
            baos.write(bytes);
            //清空缓冲区
            buffer.clear();
        }
        //将流转回字节数组
        bytes = baos.toByteArray();
        String httpHeader = new String(bytes);
        System.out.println(httpHeader);
        return httpHeader;
    }
}
