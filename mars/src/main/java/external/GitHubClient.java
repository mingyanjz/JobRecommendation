package external;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	private static final String URL_TEMPLATE_TWO = "https://jobs.github.com/positions.json?lat=%s&long=%s";
	private static final String DEFAULT_KEYWORD = "developer";
	private static final String URL_TEMPLATE_EXTEND = "https://jobs.github.com/positions.json?description=%s&location=us";

	// provide latitude, longitude and job keywords, return search result as a
	// JSONArray
	public List<Item> search(double lat, double lon, String keyword) {
		String GitHubUrl;
		if (keyword == null) {
			keyword = DEFAULT_KEYWORD;
			GitHubUrl = String.format(URL_TEMPLATE_TWO, lat, lon);
		} else {
			try {
				keyword = URLEncoder.encode(keyword, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			// generating UTL for GitHub Job API
			GitHubUrl = String.format(URL_TEMPLATE, keyword, lat, lon);
		}
		

		CloseableHttpClient httpclient = HttpClients.createDefault();
		// http response handler
		ResponseHandler<List<Item>> responseHandler = new ResponseHandler<List<Item>>() {
			@Override
			public List<Item> handleResponse(final HttpResponse response) throws IOException {
				// handle wrong status code
				if (response.getStatusLine().getStatusCode() != 200) {
					return new ArrayList<>();
				}
				HttpEntity entity = response.getEntity();
				// handle null response
				if (entity == null) {
					return new ArrayList<>();
				}
				// convert entity to string
				String responseString = EntityUtils.toString(entity);
				// construct JSON array form the JSON format string
				return getItemList(new JSONArray(responseString));
			}
		};
		// send http get request and handle the response;
		List<Item> itemList = null;
		try {
			itemList = httpclient.execute(new HttpGet(GitHubUrl), responseHandler);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		if (itemList == null) {
			itemList = new ArrayList<>();
		}
		if (itemList.size() > 2) {
			return itemList;
		}
		
		//not enough result, extends searching to US
		GitHubUrl = String.format(URL_TEMPLATE_EXTEND, keyword);
		httpclient = HttpClients.createDefault();	
		List<Item> itemListEx = null;
		try {
			itemListEx = httpclient.execute(new HttpGet(GitHubUrl), responseHandler);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (itemListEx == null) {
			return itemList;
		}
		Set<String> ItemListId = new HashSet<>();
		for (Item item : itemList) {
			ItemListId.add(item.getItemId());
		}
		//add items from extended list to original result
		for (Item item : itemListEx) {
			if (!ItemListId.contains(item.getItemId())) {
				itemList.add(item);
			}
		}
		return itemList;

//		try {
//			return  httpclient.execute( new HttpGet(GitHubUrl), responseHandler);
//		} catch (ClientProtocolException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return new ArrayList<>();
	}

	private List<Item> getItemList(JSONArray array) {
		List<Item> itemList = new ArrayList<>();

		// gather descriptions for all Items
		String[] descriptions = new String[array.length()];
		for (int i = 0; i < descriptions.length; i++) {
			JSONObject obj = array.getJSONObject(i);
			String description = getStringFieldOrEmpty(obj, "description");
			// handle empty description
			if (description.equals("") || description.equals("\n")) {
				descriptions[i] = getStringFieldOrEmpty(obj, "title");
			} else {
				descriptions[i] = description;
			}
		}

		// sent descriptions together and get keywords from MonkeyLearnClient
		List<List<String>> keywordsList = MonkeyLearnClient.extractKeywords(descriptions);

		// set item content
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = array.getJSONObject(i);
			Item item = Item.builder().setImageUrl(getStringFieldOrEmpty(obj, "company_logo"))
					.setItemId(getStringFieldOrEmpty(obj, "id")).setName(getStringFieldOrEmpty(obj, "tiltle"))
					.setAddress(getStringFieldOrEmpty(obj, "location")).setUrl(getStringFieldOrEmpty(obj, "url"))
					.setKeywords(new HashSet<String>(keywordsList.get(i))).build();
			itemList.add(item);
		}
		return itemList;
	}

	private String getStringFieldOrEmpty(JSONObject obj, String field) {
		return obj.isNull(field) ? "" : obj.getString(field);
	}

}
