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
		response.setStatuCode(200);
		response.setStatuCodeStr("OK");

 
	       double[][] array = {
				              {1.,2.},
				              {3.,4.},
				                           }; 
	        Matrix A = new Matrix(array); 
	        Double a = A.get(0, 1);
                try{
		     response.getOutputStream().write(String.valueOf(a).getBytes(),0,3);
                   }catch(IOException e){

                   }
	}
}
