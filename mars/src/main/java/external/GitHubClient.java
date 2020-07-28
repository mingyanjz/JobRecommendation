package external;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import entity.Item;


public class GitHubClient {
	private static final String URL_TEMPLATE = "https://jobs.github.com/positions.json?description=%s&lat=%s&long=%s";
	private static final String DEFAULT_KEYWORD = "developer";
	//provide latitude, longitude and job keywords, return search result as a JSONArray 
	public List<Item> search(double lat, double lon, String keyword) {
		if (keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		try {
			keyword = URLEncoder.encode(keyword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//generating UTL for GitHub Job API
		String GitHubUrl = String.format(URL_TEMPLATE, keyword, lat, lon);
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		//http response handler
		ResponseHandler<List<Item>> responseHandler = new ResponseHandler<List<Item>>() {
			@Override
			public List<Item> handleResponse(final HttpResponse response) throws IOException {
				//handle wrong status code
				if (response.getStatusLine().getStatusCode() != 200) {
					return new ArrayList<>();
				}
				HttpEntity entity = response.getEntity();
				//handle null response
				if (entity == null) {
					return new ArrayList<>();
				}
				//convert entity to string
				String responseString = EntityUtils.toString(entity);
				//construct  JSON array form the JSON format string
				return getItemList(new JSONArray(responseString));
			}
		};
		//send http get request and handle the response;
		try {
			return  httpclient.execute( new HttpGet(GitHubUrl), responseHandler);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	private List<Item> getItemList(JSONArray array) {
		List<Item> itemList = new ArrayList<>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = array.getJSONObject(i);
			Item item = Item.builder()
					.setImageUrl(getStringFieldOrEmpty(obj, "company_logo"))
					.setItemId(getStringFieldOrEmpty(obj, "id"))
					.setName(getStringFieldOrEmpty(obj, "tiltle"))
					.setAddress(getStringFieldOrEmpty(obj, "location"))
					.setUrl(getStringFieldOrEmpty(obj, "url"))
					.build();
			itemList.add(item);
		}
		return itemList;
	}
	private String getStringFieldOrEmpty(JSONObject obj, String field) {
		return obj.isNull(field) ? "" : obj.getString(field);
	}

}
