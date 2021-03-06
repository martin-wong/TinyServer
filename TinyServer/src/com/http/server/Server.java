package com.http.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import com.http.handler.HttpHandler;
import com.http.utils.XMLUtil;

 
public class Server implements Runnable {

	//声明一个10240字节大小的缓冲区 用来存放http请求内容  
    private ByteBuffer buffer = ByteBuffer.allocate(10240); 
    
    //线程池
    ExecutorService threadPool ;  
    
    //默认监听8080端口
    private String PORT = "8080";
    
	private boolean interrupted = false;
	
	private Logger logger = Logger.getLogger(Server.class);
	
	public Server(boolean interrupted) {
		this.interrupted = interrupted;
	}

	@Override
	public void run() {
		try {
	
			Selector selector = Selector.open();
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			ServerSocket serverSocket = serverSocketChannel.socket();
			
			//ServerSocketChannel通道监听server.xml中设置的端口 默认端口8080
			serverSocket.setReuseAddress(true);  
			String ServerXmlPath = "conf"+File.separator+"server.xml" ;
			logger.info("指定的xml文件的路径是:" + ServerXmlPath );
		    Element ele = XMLUtil.getRootElement(ServerXmlPath).element("port");
		    if(ele != null)
			   PORT = ele.getText(); 
		    serverSocket.bind(new InetSocketAddress(Integer.parseInt(PORT)));
			
			logger.info("成功绑定端口" + PORT);
			
			//创建线程池
			String maxThreads = null;
			Element e1 = XMLUtil.getRootElement(ServerXmlPath).element("maxThreads");
			if(e1 != null)
				maxThreads = e1.getText(); 
			threadPool = Executors.newFixedThreadPool(Integer.parseInt(maxThreads));
			//将通道设置为非阻塞模式
			serverSocketChannel.configureBlocking(false);
			//serverSocketChannel注册ACCEPT事件
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
						//ACCEPT事件 ，说明有外界连接连入
						ServerSocketChannel server = (ServerSocketChannel) key.channel();
						//得到接收到的SocketChannel
						SocketChannel socketChannel = server.accept();
						if(socketChannel != null) {
							logger.info("收到了来自" + ((InetSocketAddress)socketChannel.getRemoteAddress()).getHostString()
									+ "的请求");
							socketChannel.configureBlocking(false);
							//将socketChannel注册读事件到选择器
							socketChannel.register(selector, SelectionKey.OP_READ);
						}
					} 
					if (key.isReadable()) {
						//该key有Read事件 意味着客户端发送的数据已经开始接收，这里的SocketChannel是上面注册了读事件的SocketChannel
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
							logger.info("本次HTTP请求头信息\r\n" + requestHeader);
							logger.info("线程池准备接受主线程分配任务..");
							threadPool.execute(new HttpHandler(requestHeader, key));
						}
						
					} 
					if (key.isWritable()) {
						//已经准备好可以向客户端写数据
						logger.info("写通道已经就绪，开始传送数据到客户端");
						key.interestOps(key.interestOps() & (~SelectionKey.OP_WRITE));
						SocketChannel socketChannel = (SocketChannel) key.channel();
						
						threadPool.execute(new Runnable() {
							@Override
							public void run() {
								try {
									writeToClient(key, socketChannel);
									logger.info("数据传送成功");
								} catch (IOException e) {
									e.printStackTrace();
								}finally{
									 try {
										socketChannel.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
						});
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

	private void writeToClient(SelectionKey key, SocketChannel socketChannel) throws IOException {
		List bufferList =  (List) key.attachment();
		//把buffer从写模式，切换到读模式，让channel读出去（写事件）
		Object[] buffers =  bufferList.toArray();
		int len = buffers.length;
		//bufferList的最后一个元素是响应头（或者响应头+HTML文件内容）
		if(len == 1){
			ByteBuffer b = (ByteBuffer) buffers[0] ;
			b.flip();
			while(b.hasRemaining())
			  socketChannel.write(b);
		}else{
			//如果len>1说明调用过outputStream的write方法写过数据
			ByteBuffer b = (ByteBuffer) buffers[len-1] ;
			b.flip();
			while(b.hasRemaining())
			  socketChannel.write(b);
			for (int i = 0; i < len-1; i++) {
				ByteBuffer temp = (ByteBuffer) buffers[i] ;
				temp.flip();
				while(temp.hasRemaining())
				  socketChannel.write(temp);
			}
		}
		bufferList.clear();
	}
	
	//将请求信息转为字符串
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
        return httpHeader;
    }
}
