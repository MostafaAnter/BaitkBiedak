package mostafa_anter.baitkbiedak.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mostafa_anter.baitkbiedak.R;


/**
 * Created by mostafa on 08/03/16.
 */
public class DetailsFragment extends Fragment {
    public static final String ARG_ITEM_ID = "item_id";
    public DetailsFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        return view;
    }
}
