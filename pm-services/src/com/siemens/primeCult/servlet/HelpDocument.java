package com.siemens.primeCult.servlet;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelpDocument extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream("pumpMonitoringHelp.pdf");
		ServletOutputStream stream = null;
		BufferedInputStream buf = null;
		try {
			stream = response.getOutputStream();
			response.setContentType("application/pdf");
			response.setDateHeader("Expires", 0);
			response.addHeader("Content-Disposition", "inline; filename=help.pdf");
			buf = new BufferedInputStream(inputStream);
			int readBytes = 0;
			while ((readBytes = buf.read()) != -1)
				stream.write(readBytes);
		} finally {
			if (stream != null)
				stream.flush();
			stream.close();
			if (buf != null)
				buf.close();
		}
	}
}
