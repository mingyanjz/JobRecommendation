package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import dataBase.MySQLDBConnection;

/**
 * Servlet implementation class Register
 */
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Register() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MySQLDBConnection conn = new MySQLDBConnection();
		JSONObject obj = new JSONObject(IOUtils.toString(request.getReader()));
		JSONObject resObj = new JSONObject();
		if (conn.addUser(obj.getString("user_id"), obj.getString("password"), obj.getString("first_name"), obj.getString("last_name"))) {
			resObj.put("status", "OK");
		}  else {
			resObj.put("status", "User Already Exists");
		}
		conn.close();
		RpcHelper.writeJsonObjectToResponse(response, resObj);
	}

}
