package servlet;


import com.http.context.Request;
import com.http.context.Response;
import java.io.OutputStream;
import java.io.IOException;
import com.http.servlet.HttpServlet;
import Jama.Matrix;

public class DispatcherServlet extends HttpServlet{

	@Override
	public void doGet(Request request, Response response)   {
		request.setAttribute("message", "转发给MatrixServlet");
                response.setHtmlFile("html/welcome.html");
                
                byte[] a = "before".getBytes();
                byte[] b = "after".getBytes();
         
                try{
		        response.getOutputStream().write(a,0,a.length);
			request.getRequestDispatcher("/matrix").forward(request,response);
		        
                }catch(IOException e){

                }
	}
}
