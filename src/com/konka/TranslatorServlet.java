package com.konka;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * Servlet implementation class TranslatorServlet
 */
@MultipartConfig(location = "/home/apache-tomcat/apache-tomcat-7.0.54/webapps/Translator/temp")
@WebServlet(name = "TranslatorServlet", urlPatterns = {"/translator.do"})
public class TranslatorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TranslatorServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("iso-8859-1");
        Part part = request.getPart("fileToUpload");
        String fileName = getFileName(part);
        //将文件写入location指定的目录
        part.write(fileName);
        String cmdArray = "/bin/sh /home/apache-tomcat/apache-tomcat-7.0.54/webapps/Translator/WEB-INF/classes/com/konka/translator.sh /home/apache-tomcat/apache-tomcat-7.0.54/webapps/Translator/temp/" + fileName;
        try {
			Runtime.getRuntime().exec(cmdArray).waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        File file = new File("/home/apache-tomcat/apache-tomcat-7.0.54/webapps/Translator/temp/" + fileName);
        if(file.exists()) {
        	response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            int fileLength = (int) file.length();
            response.setContentLength(fileLength);
            /*如果文件长度大于0*/
            if (fileLength != 0) {
                /*创建输入流*/
            	FileInputStream is = new FileInputStream(file);
            	ServletOutputStream os = response.getOutputStream();
            	BufferedOutputStream bos = new BufferedOutputStream(os);
            	byte[] len = new byte[2048];
            	int read = 0;
            	while((read=is.read(len)) != -1){
            		bos.write(len, 0, read);
            	}
            	bos.flush();
            	bos.close();
            	is.close();
            }
            //file.delete();
        }
	}

	private String getFileName(Part part) {
        String header = part.getHeader("Content-Disposition");
        String fileName = header.substring(header.indexOf("filename=\"") + 10, header.lastIndexOf("\""));
        return fileName;
    }
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	} 
}
