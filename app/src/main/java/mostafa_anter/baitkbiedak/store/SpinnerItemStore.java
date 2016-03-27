package mostafa_anter.baitkbiedak.store;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import mostafa_anter.baitkbiedak.models.FavoriteModel;
import mostafa_anter.baitkbiedak.models.SpinnerItem;


/**
 * Created by mostafa on 22/03/16.
 */
public class SpinnerItemStore {
    private static final String PREFKEY = "spinners";
    private SharedPreferences favoritePrefs;

    public SpinnerItemStore(Context context) {
        favoritePrefs = context.getSharedPreferences(PREFKEY, Context.MODE_PRIVATE);
    }


    public String findItem(String key){
        return (favoritePrefs.getString(key, ""));
    }

    public boolean update(SpinnerItem note) {

        SharedPreferences.Editor editor = favoritePrefs.edit();
        editor.putString(note.getTitleKey(), note.getIdValue());
        editor.commit();
        return true;
    }

    public void clearPreferences(){
        SharedPreferences.Editor editor = favoritePrefs.edit();
        editor.clear();
        editor.commit();
    }
}
