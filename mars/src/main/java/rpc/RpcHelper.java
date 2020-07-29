package rpc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import entity.Item;

public class RpcHelper {
	//convert jason object to item object
	public static Item JSONObjectToItem(JSONObject obj) {
		JSONArray array = obj.getJSONArray("keywords");
		HashSet<String> keywords = new HashSet<>();
		for (int i = 0; i < array.length(); i++) {
			keywords.add(array.getString(i));
		}
		Item item = Item.builder()
				.setName(obj.getString("name"))
				.setAddress(obj.getString("address"))
				.setImageUrl(obj.getString("image_url"))
				.setItemId(obj.getString("item_id"))
				.setUrl(obj.getString("url"))
				.setKeywords(keywords)
				.build();
		return item;
	}
	// helper function to write a JSONArray to http response
	public static void writeJsonArrayToResponse(HttpServletResponse response, JSONArray array) throws IOException {
		response.setContentType("appliction/json");
		response.getWriter().print(array);
	}

	// helper function to write a JSONObject to http response
	public static void writeJsonObjectToResponse(HttpServletResponse response, JSONObject obj) throws IOException {
		response.setContentType("appliction/json");
		response.getWriter().print(obj);
	}
}
