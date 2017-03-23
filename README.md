# TinyServer
这是一个使用JavaNIO以及多线程技术实现的类似tomcat的服务器，目前仅支持linux系统，尚未针对windows进行测试。
## 启动服务器
release文件夹内的内容包含了服务器主程序和几个小小的使用例子，您可以下载后放在您计算机的任意位置，然后启动terminal，进入release文件夹后，输入：
```
java -jar tinyserver.jar
```
即可以启动服务器，默认它监听在8080端口，可以在server.xml中更改。启动浏览器，在地址栏输入下面的地址：
```
http://localhost:8080/webapps/matrix           //演示如何使用第三方jar包编写servlet
http://localhost:8080/webapps/download         //演示实现文件下载，您需要修改代码以明确一个存储在您电脑上的文件的位置
http://localhost:8080/webapps/welcome          //演示静态资源读取
http://localhost:8080/webapps/123456           //演示404错误
http://localhost:8080/webapps/dispatcher       //演示使用getRequestDispatcher.forward方法进行servlet转发
http://localhost:8080/webapps/session         //演示Session的使用

```
就能看到不同的效果
## 编写自己的Servlet并让服务器执行
1.您编写的Servlet应该继承tinyserver.jar中的HttpServlet类，并实现doGet方法（暂不支持doPost）</br>
2.编译后的class文件必须按包的层级放置在tinyserver.jar同级的文件夹webapps下的WEB-INF/classes下</br>
3.您编写的Servlet所需要的jar包必须放置在WEB-INF/lib文件夹下，TinyServer会自动加载该位置下的jar文件</br>
4.在WEB-INF/web.xml中配置您的servlet的映射路径</br>
5.遇到问题，请参考给出的几个例子中的代码</br>
## 预览
![preview](preview.png)
