package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import external.GitHubClient;

/**
 * Servlet implementation class SearchItem
 */
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		
		//test, return a JSON object to frontend if name is not null
//		response.setContentType("application/json");
//		PrintWriter writer = response.getWriter();
//		if (request.getParameter("name") != null) {
//			JSONObject obj = new JSONObject();
//			obj.put("name", request.getParameter("name"));
//			writer.print(obj);
//		} else {
//			JSONObject obj = new JSONObject();
//			obj.put("name", "Invalid name");
//			writer.print(obj);
//		}
		
		//test, return a JSON array
//		PrintWriter writer = response.getWriter();
//		JSONArray array = new JSONArray();
//		array.put(new JSONObject().put("name","test user"));
//		array.put(new JSONObject().put("age", "25"));
//		writer.print(array);
		
		//test, return a JSON array using helper
//		RpcHelper.writeJsonArrayToResponse(response, array);
		
		//get the latitude and  and longitude from user's request 
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		GitHubClient gitHubClient = new GitHubClient();
		//use search function in GitHubClient and return the result as a JSON Array.
		RpcHelper.writeJsonArrayToResponse(response, gitHubClient.search(lat, lon, null));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}