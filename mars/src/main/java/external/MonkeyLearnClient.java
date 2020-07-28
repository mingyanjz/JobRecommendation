package external;
import com.monkeylearn.ExtraParam;
import com.monkeylearn.MonkeyLearn;
import com.monkeylearn.MonkeyLearnResponse;
import com.monkeylearn.MonkeyLearnException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import gitIgnore.PersonalData;

import java.util.ArrayList;
import java.util.List;

public class MonkeyLearnClient {
	public static void main( String[] args ){       
        //test Strings
        String[] textList = {
				"Elon Musk has shared a photo of the spacesuit designed by SpaceX. This is the second image shared of the new design and the first to feature the spacesuit’s full-body look.", 
				"Former Auburn University football coach Tommy Tuberville defeated ex-US Attorney General Jeff Sessions in Tuesday nights runoff for the Republican nomination for the U.S. Senate. ",
				"The NEOWISE comet has been delighting skygazers around the world this month – with photographers turning their lenses upward and capturing it above landmarks across the Northern Hemisphere."
		};
        
        //extract keywords
		List<List<String>> keywordsList = extractKeywords(textList);
		for (List<String> keywords : keywordsList) {
			for (String keyword : keywords) {
				System.out.println(keyword);
			}
			System.out.println();
		}
    }
	
	public static List<List<String>> extractKeywords(String[] input) {
		//sanity check
		if (input == null || input.length == 0) {
			return new ArrayList<>();
		}
		// Use the API key from your account
        MonkeyLearn ml = new MonkeyLearn(PersonalData.MONKEYLEARN_API_KEY);
        //set extract parameters
		ExtraParam[] extraParams = {new ExtraParam("max_keywords", "3")};
        MonkeyLearnResponse response;
        try {
        	//extract keywords
        	response = ml.extractors.extract(PersonalData.MONKEYLEARN_MODEL_ID, input, extraParams);  
        	return responseJSONArrayToKeywords(response.arrayResult);
        } catch  (MonkeyLearnException e) {
        	e.printStackTrace();
        }
        
        return new ArrayList<>();
	}
	//convert response json array to string
	private static List<List<String>> responseJSONArrayToKeywords(JSONArray input) {
		List<List<String>> keywordsList = new ArrayList<>();
		for (int i = 0; i < input.size(); i++) {
			List<String> keywords = new ArrayList<>();
			JSONArray array = (JSONArray) input.get(i);
			for (int j = 0; j < array.size(); j++) {
				JSONObject obj = (JSONObject) array.get(j);
				keywords.add((String) obj.get("keyword"));
			}
			keywordsList.add(keywords);
		}
		return keywordsList;
	}
}
