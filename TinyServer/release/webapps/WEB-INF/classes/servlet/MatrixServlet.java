package servlet;


import com.http.context.Request;
import com.http.context.Response;
import java.io.OutputStream;
import java.io.IOException;
import com.http.servlet.HttpServlet;
import Jama.Matrix;

public class MatrixServlet extends HttpServlet{

	@Override
	public void doGet(Request request, Response response)   {
		response.setStatus(200);
		response.setStatuCodeStr("OK");

                String message = (String)request.getAttribute("message");
	       double[][] array = {
				              {1.,2.},
				              {3.,4.},
				                           }; 
	        Matrix A = new Matrix(array); 
	        Double a = A.get(0, 1);
                try{
                     byte[] b1 =  String.valueOf(a).getBytes();
		     response.getOutputStream().write(b1,0,b1.length);
                     if(message != null){
                        byte[] b2 =  message.getBytes();
                        response.getOutputStream().write(b2,0,b2.length);
                     }
                   }catch(IOException e){

                   }
	}
}
