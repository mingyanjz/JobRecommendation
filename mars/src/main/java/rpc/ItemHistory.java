package rpc;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import dataBase.MySQLDBConnection;
import entity.Item;

/**
 * Servlet implementation class ItemHistory
 */
public class ItemHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ItemHistory() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null) {	
			//not logged in
			response.setStatus(403);
			return;
		}
		MySQLDBConnection conn = new MySQLDBConnection();
		String userId = request.getParameter("user_id");
		Set<Item> items = conn.getFavoriteItem(userId);
		JSONArray array = new JSONArray();
		for (Item item : items) {
			array.put(item.toJSONObject());
		}
		RpcHelper.writeJsonArrayToResponse(response, array);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null) {	
			//not logged in
			response.setStatus(403);
			return;
		}
		MySQLDBConnection conn = new MySQLDBConnection();
		JSONObject obj = new JSONObject(IOUtils.toString(request.getReader()));
		String userId = obj.getString("user_id");
		Item item = RpcHelper.JSONObjectToItem(obj.getJSONObject("favorite"));
		conn.addFavorite(userId, item);
		conn.close();
		RpcHelper.writeJsonObjectToResponse(response, new JSONObject().put("result", "SUCCESS"));
	}
	
	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null) {	
			//not logged in
			response.setStatus(403);
			return;
		}
		MySQLDBConnection conn = new MySQLDBConnection();
		JSONObject obj = new JSONObject(IOUtils.toString(request.getReader()));
		String userId = obj.getString("user_id");
		Item item = RpcHelper.JSONObjectToItem(obj.getJSONObject("favorite"));
		conn.removeFavorite(userId, item);
		conn.close();
		RpcHelper.writeJsonObjectToResponse(response, new JSONObject().put("result", "SUCCESS"));
	}
}
