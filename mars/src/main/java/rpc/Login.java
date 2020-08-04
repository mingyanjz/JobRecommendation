package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import dataBase.MySQLDBConnection;

/**
 * Servlet implementation class Login
 */
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		JSONObject resObj = new JSONObject();
		if (session != null) {
			//already login
			MySQLDBConnection conn = new MySQLDBConnection();
			String userId = session.getAttribute("user_id").toString();
			resObj.put("status", "OK").put("user_id", userId).put("name", conn.getUserName(userId));
		} else {		
			//not login
			resObj.put("status", "Invalid Session");
			response.setStatus(403);
		}
		RpcHelper.writeJsonObjectToResponse(response, resObj);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject obj = new JSONObject(IOUtils.toString(request.getReader()));
		String userId = obj.getString("user_id");
		String password = obj.getString("password");
		MySQLDBConnection conn = new MySQLDBConnection();
		JSONObject resObj = new JSONObject();
		if (conn.verifyUser(userId, password)) {
			//login successfully
			HttpSession session = request.getSession();
			session.setAttribute("user_id", userId);
			resObj.put("status", "OK").put("user_id", userId).put("name", conn.getUserName(userId));
		} else {
			//Login failed
			resObj.put("status", "Login failed, user id and password do not exist");
			response.setStatus(401);
		}
		conn.close();
		RpcHelper.writeJsonObjectToResponse(response, resObj);
	}

}
