package mahmoudabdelshafy.baitkbiedak.parser;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mahmoudabdelshafy.baitkbiedak.constants.Constants;
import mahmoudabdelshafy.baitkbiedak.models.FeedPOJO;
import mahmoudabdelshafy.baitkbiedak.models.SpinnerItem;
import mahmoudabdelshafy.baitkbiedak.store.SpinnerItemStore;

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

    public static String parseForNotifecation(String feed){
        try {
            JSONObject  jsonRootObject = new JSONObject(feed);//done
            //Get the instance of JSONArray that contains JSONObjects
            JSONArray jsonNewsArray = jsonRootObject.optJSONArray("data");
            JSONObject jsonObject = jsonNewsArray.getJSONObject(0);
            String title = jsonObject.optString("title");
            return title;
        }catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String[] parseSpinnerItems(Context mContext, String feed){
        try {
            JSONObject  jsonRootObject = new JSONObject(feed);//done
            //Get the instance of JSONArray that contains JSONObjects
            JSONArray jsonNewsArray = jsonRootObject.optJSONArray("data");
            String[] newsList = new String[jsonNewsArray.length()];
            for (int i = 0; i < jsonNewsArray.length(); i++) {
                JSONObject jsonObject = jsonNewsArray.getJSONObject(i);

                // retrieve all metadata
                String id = jsonObject.optString("id");
                String title = jsonObject.optString("title");

                // save in shared
                SpinnerItem spinnerItem = new SpinnerItem();
                spinnerItem.setIdValue(id);
                spinnerItem.setTitleKey(title);
                new SpinnerItemStore(mContext).update(spinnerItem);


                newsList[i] = title;
            }
            return newsList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

}
