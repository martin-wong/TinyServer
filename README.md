# TinyServer
这是一个使用JavaNIO技术实现的类似tomcat的服务器，但只实现了其一小部分的功能，包括发送HTML文件，发送二进制文件等。（暂仅支持linux操作系统）
## 启动服务器
release文件夹内的内容包含了服务器主程序和几个小小的使用例子，您可以下载后放在您计算机的任意位置，然后启动terminal，进入release文件夹后，输入：
```
java -jar tinyserver.jar
```
即可以启动服务器，默认它监听在8080端口，可以在server.xml中更改。启动浏览器，在地址栏输入：
```
http://localhost:8080/webapps/matrix
http://localhost:8080/webapps/welcome
http://localhost:8080/webapps/123456
```
就能看到不同的效果
## 编写自己的Servlet并让服务器执行
1.您编写的Servlet应该继承tinyserver.jar中的HttpServlet类，并实现doGet方法（暂不支持doPost）</br>
2.编译后的class文件必须按包的层级放置在tinyserver.jar同级的文件夹webapps下的WEB-INF/classes下</br>
3.您编写的Servlet所需要的jar包必须放置在tinyserver.jar同级的文件夹webapps下的WEB-INF/lib文件夹下</br>
4.在web.xml中配置您的servlet的映射路径</br>
5.遇到问题，请参考给出的几个例子中的代码</br>
## 预览
![preview](preview.png)
