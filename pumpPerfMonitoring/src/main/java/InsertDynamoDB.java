package main.java;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.samples.AmazonDynamoDBSample;

/**
 * Servlet implementation class InsertDynamoDB
 */
@WebServlet("/InsertDynamoDB")
public class InsertDynamoDB extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public InsertDynamoDB() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String result = "Table created and data inserted";
		try {
			AmazonDynamoDBSample.main(Integer.valueOf(request.getParameter("diff"))); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			result = e.getMessage();
		}
		response.getWriter().append(result);
		;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
