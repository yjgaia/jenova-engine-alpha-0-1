package co.hanul.jenova.web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import co.hanul.jenova.JenovaConfig;
import co.hanul.jenova.util.MySQLPassword;

/**
 * 임시 파일 업로드 서블릿
 * 
 * @author Mr. 하늘
 */
public class TempUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private File destinationDir;
	private File tmpDir;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		destinationDir = new File(getServletContext().getRealPath(JenovaConfig.TEMP_UPLOAD_DIR));
		tmpDir = new File(JenovaConfig.TEMP_UPLOAD_TMPDIR);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/plain");

		DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
		fileItemFactory.setSizeThreshold(1 * 1024 * 1024);
		fileItemFactory.setRepository(tmpDir);
		ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
		try {
			List<?> items = uploadHandler.parseRequest(request);
			Iterator<?> itr = items.iterator();
			while (itr.hasNext()) {
				FileItem item = (FileItem) itr.next();
				if (!item.isFormField()) {
					String fileName = MySQLPassword.md5(request.getRemoteAddr() + "\\" + System.currentTimeMillis());
					out.print(fileName);
					File file = new File(destinationDir, fileName);
					item.write(file);
				}
				out.close();
			}
		} catch (FileUploadException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}