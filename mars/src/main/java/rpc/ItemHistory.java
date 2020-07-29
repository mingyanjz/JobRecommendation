package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
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
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MySQLDBConnection conn = new MySQLDBConnection();
		JSONObject obj = new JSONObject(IOUtils.toString(request.getReader()));
		String user_id = obj.getString("user_id");
		Item item = RpcHelper.JSONObjectToItem(obj.getJSONObject("favorite"));
		conn.addFavorite(user_id, item);
		conn.close();
	}
	
	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MySQLDBConnection conn = new MySQLDBConnection();
		JSONObject obj = new JSONObject(IOUtils.toString(request.getReader()));
		String user_id = obj.getString("user_id");
		Item item = RpcHelper.JSONObjectToItem(obj.getJSONObject("favorite"));
		conn.removeFavorite(user_id, item);
		conn.close();
	}
}
