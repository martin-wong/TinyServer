
import com.http.context.Request;
import com.http.context.Response;
import com.http.servlet.HttpServlet;
import com.http.context.impl.Cookie;
import java.io.IOException;

public class CookieServlet extends HttpServlet {
	
	@Override
	public void doGet(Request request , Response response) {

                String c = (String) request.getCookies().get("id");
		Cookie cookie = new Cookie("id", "wong");
                cookie.setMaxAge(10);
		response.setCookie(cookie);
 
		response.setStatus(200);
		response.setStatuCodeStr("OK");
               try{
                     if(c != null){
                        byte[] b =  c.getBytes();
                        response.getOutputStream().write(b,0,b.length);
                     }else{
                        byte[] b =  "no cookie named id".getBytes();
                        response.getOutputStream().write(b,0,b.length);
                     }
                   }catch(IOException e){

                   }
		
	}
	
}
