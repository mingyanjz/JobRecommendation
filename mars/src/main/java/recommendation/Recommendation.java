package recommendation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dataBase.MySQLDBConnection;
import entity.Item;
import external.GitHubClient;

public class Recommendation {
	public List<Item> recommend(String userId, double lat, double lon) {
		List<Item> items = new ArrayList<>();
		MySQLDBConnection conn = new MySQLDBConnection();
		Set<String> favoriteItemIds = conn.getFavoriteItemId(userId);
		//keywords map
		Map<String, Integer> freq = new HashMap<>();
		for (String itemId : favoriteItemIds) {
			Set<String> keywords = conn.getKeywords(itemId);
			for (String keyword : keywords) {
				Integer ct = freq.get(keyword);
				if (ct == null) {
					freq.put(keyword, 1);
				} else {
					freq.put(keyword, ct + 1);
				}
			}
		}
		conn.close();
		List<Map.Entry<String, Integer>> keywordsList = new ArrayList<>(freq.entrySet());
		//find keywords with top 3 frequency
		keywordsList.sort(new MyComparator());		
		if (keywordsList.size() > 3) {
			keywordsList = keywordsList.subList(0, 3);
		}
		//search based on keywords
		GitHubClient client = new GitHubClient();
		Set<String> visitedItemId = new HashSet<>();
		for (Map.Entry<String, Integer> entry : keywordsList) {
			List<Item> searchResult = client.search(lat, lon, entry.getKey());
			for (Item item : searchResult) {
				if (!visitedItemId.contains(item.getItemId()) && !favoriteItemIds.contains(item.getItemId())) {
					items.add(item);
					visitedItemId.add(item.getItemId());
				}		
			}
		}		
		return items;
	}
	static class MyComparator implements Comparator<Map.Entry<String, Integer>> {
		@Override
		public int compare(Map.Entry<String, Integer> entryOne, Map.Entry<String, Integer> entryTwo) {
			if (entryOne.getValue().equals(entryTwo.getValue())) {
				return 0;
			}
			return entryOne.getValue() > entryTwo.getValue() ? -1 : 1;
		}
	}
}
