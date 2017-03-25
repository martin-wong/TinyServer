
import com.http.context.Request;
import com.http.context.Response;
import com.http.servlet.HttpServlet;

public class RedirectServlet extends HttpServlet {
	
	@Override
	public void doGet(Request request , Response response) {
                response.setStatus(200);
		response.sendRedirect("http://www.baidu.com");
	}
	
}
