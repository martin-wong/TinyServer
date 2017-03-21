
import com.http.context.Request;
import com.http.context.Response;
import com.http.servlet.HttpServlet;

public class WelcomeServlet extends HttpServlet {
	
	@Override
	public void doGet(Request request , Response response) {
 
		response.setStatuCode(200);
		response.setStatuCodeStr("OK");
		response.setHtmlFile("html/welcome.html"); 
	}
	
}
