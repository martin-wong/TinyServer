package servlet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.http.context.Request;
import com.http.context.Response;
import java.io.OutputStream;
import com.http.servlet.HttpServlet;

public class DownloadServlet extends HttpServlet{

	@Override
	public void doGet(Request request, Response response)  {
		response.setStatus(200);
		response.setStatuCodeStr("OK");
		response.setContentType("audio/mpeg");
		response.setContent_Disposition("attachment;filename=song.mp3");
		
		FileInputStream in = null;
		try {
			in = new FileInputStream("/home/wong/Music/CloudMusic/原来的我.mp3");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		OutputStream outputStream = response.getOutputStream();
		
		byte[] buffer = new byte[1024];
		int len = 0 ;
		try {
			while((len = in.read(buffer)) != -1){
					outputStream.write(buffer,0,len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
