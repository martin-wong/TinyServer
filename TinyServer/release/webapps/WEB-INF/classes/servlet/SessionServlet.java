package servlet;


import com.http.context.Request;
import com.http.context.Response;
import java.io.OutputStream;
import java.io.IOException;
import com.http.servlet.HttpServlet;
import Jama.Matrix;

public class SessionServlet extends HttpServlet{

	@Override
	public void doGet(Request request, Response response)   {
		response.setStatuCode(200);
		response.setStatuCodeStr("OK");

                String message = (String)request.getSession().getAttribute("msg");
                request.getSession().setAttribute("msg","session");
                try{
                     if(message != null){
                        byte[] b =  message.getBytes();
                        response.getOutputStream().write(b,0,b.length);
                     }else{
                        byte[] b =  "no session".getBytes();
                        response.getOutputStream().write(b,0,b.length);
                     }
                   }catch(IOException e){

                   }
	}
}
