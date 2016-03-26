package mostafa_anter.baitkbiedak.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mostafa_anter.baitkbiedak.constants.Constants;
import mostafa_anter.baitkbiedak.models.FeedPOJO;

/**
 * Created by mostafa on 20/03/16.
 */
public class JsonParser{
    public static List<FeedPOJO> parseJsonFeed(String feed){

        try {
            JSONObject  jsonRootObject = new JSONObject(feed);//done
            //Get the instance of JSONArray that contains JSONObjects
            JSONArray jsonNewsArray = jsonRootObject.optJSONArray("data");
            List<FeedPOJO> newsList = new ArrayList<>();
            for (int i = 0; i < jsonNewsArray.length(); i++) {
                JSONObject jsonObject = jsonNewsArray.getJSONObject(i);

                // retrieve all metadata
                String id = jsonObject.optString("id");
                String title = jsonObject.optString("title");
                String description = jsonObject.optString("description");
                String content = jsonObject.optString("content");
                String img = Constants.IMAGE_MAIN_URL + jsonObject.optString("img");
                String updated_at = jsonObject.optString("updated_at");

                // put all item inside one object
                FeedPOJO newsPojo = new FeedPOJO(id, title, updated_at,
                        description, content, null, img);
                newsList.add(newsPojo);
            }
            return newsList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }


    }

}
