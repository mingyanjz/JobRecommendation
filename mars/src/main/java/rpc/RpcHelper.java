package rpc;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public class RpcHelper {
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
