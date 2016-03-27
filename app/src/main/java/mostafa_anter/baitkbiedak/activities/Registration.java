package mostafa_anter.baitkbiedak.activities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import mostafa_anter.baitkbiedak.R;
import mostafa_anter.baitkbiedak.app.AppController;
import mostafa_anter.baitkbiedak.constants.Constants;
import mostafa_anter.baitkbiedak.parser.JsonParser;
import mostafa_anter.baitkbiedak.store.SpinnerItemStore;

public class Registration extends AppCompatActivity {
    // declare ui component
    @Bind(R.id.fullName) EditText fullName;
    @Bind(R.id.email) AutoCompleteTextView email;
    @Bind(R.id.phone) EditText phone;
    @Bind(R.id.address) EditText address;
    @Bind(R.id.nationalId) EditText nationalId;
    @Bind(R.id.choose_gov) Spinner government;
    @Bind(R.id.chose_reg) Spinner region;
    @Bind(R.id.chose_cost) Spinner cost;

    // for government spinner
    private static String[] governorates;
    ArrayAdapter<String> governoratesAdapter;

    // for region spinner
    private static String[] regions;
    ArrayAdapter<String> regionsAdapter;

    // for region cost
    private static String[] costs;
    ArrayAdapter<String> costsAdapter;

    // booking parameters
    private static String title,
            nId,
            addressApi,
            emailApi,
            tel,
            value,
            town;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // request cities data
                if (isOnline()) {
                    // do some thing
                    if (promptParams()){
                        makeRegisterRequest();
                    }else {
                        // show error message
                        new SweetAlertDialog(Registration.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("خطأ...")
                                .setContentText("أكمل ادخال البيانات")
                                .show();
                    }

                } else {
                    // show error message
                    new SweetAlertDialog(Registration.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("خطأ")
                            .setContentText("تأكد انك متصل بالانترنت")
                            .show();
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setToolbar();

        // request cities data
        if (isOnline()) {
            makeGovernoratesRequest();
            makeCostsRequest();
        } else {
            // show error message
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("خطأ")
                    .setContentText("تأكد انك متصل بالانترنت")
                    .show();
        }

    }

    // set toolbar
    private void setToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrowleft_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(Registration.this);
                overridePendingTransition(R.anim.push_left_enter, R.anim.push_left_exit);
            }
        });

        /*
        * hide title
        * */
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //toolbar.setNavigationIcon(R.drawable.ic_toolbar);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        //toolbar.setLogo(R.drawable.ic_toolbar);

        /*
        * change font of title
        * */
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        Typeface socialCapFont = Typeface.createFromAsset(getAssets(), "fonts/daisy.ttf");
        mTitle.setTypeface(socialCapFont);
    }

    private void setGovernoratesAdapter(){
        // Initialize and set Adapter
        governoratesAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, governorates);

        governoratesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        government.setAdapter(governoratesAdapter);

        government.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {
                // On selecting a spinner item
                String itemId = new SpinnerItemStore(Registration.this).
                        findItem(adapter.getItemAtPosition(position).toString());
                // make regions requests
                makeRegionsRequest(itemId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void setRegionsAdapter(){
        // Initialize and set Adapter
        regionsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, regions);

        regionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        region.setAdapter(regionsAdapter);
        region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {
                // On selecting a spinner item
                town = new SpinnerItemStore(Registration.this).
                        findItem(adapter.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void setCostsAdapter(){
        // Initialize and set Adapter
        costsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, costs);

        costsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        cost.setAdapter(costsAdapter);
        cost.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {
                // On selecting a spinner item
                value = new SpinnerItemStore(Registration.this).
                        findItem(adapter.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    // getGovernorates from server
    private void makeGovernoratesRequest(){


        // Tag used to cancel the request
        String  tag_string_req = "string_req";
        final String TAG = "Response";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Constants.CITIES, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                governorates = JsonParser.parseSpinnerItems(Registration.this, response);
                setGovernoratesAdapter();


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());

            }
        }){

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", Constants.TOKEN);

                return params;
            }

        };

        // disable cache
        strReq.setShouldCache(false);
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    // getRegions from server
    private void makeRegionsRequest(final String id){
        // Tag used to cancel the request
        String  tag_string_req = "string_req";
        final String TAG = "Response";

        // Set up a progress dialog
        final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("جارى تحميل مناطق المحافظه..");
        pDialog.setCancelable(false);
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Constants.REGIONS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                regions = JsonParser.parseSpinnerItems(Registration.this, response);
                setRegionsAdapter();

                pDialog.hide();


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                pDialog.hide();

            }
        }){

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", Constants.TOKEN);
                params.put("id", id);

                return params;
            }

        };

        // disable cache
        strReq.setShouldCache(false);
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    // getCosts from server
    private void makeCostsRequest(){
        // Tag used to cancel the request
        String  tag_string_req = "string_req";
        final String TAG = "Response";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Constants.COSTS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                costs = JsonParser.parseSpinnerItems(Registration.this, response);
                setCostsAdapter();


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());

            }
        }){

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", Constants.TOKEN);

                return params;
            }

        };

        // disable cache
        strReq.setShouldCache(false);
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    // Register
    private void makeRegisterRequest(){
        // Set up a progress dialog
        final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("انتظر قليلا..");
        pDialog.setCancelable(false);
        pDialog.show();

        // Tag used to cancel the request
        String  tag_string_req = "string_req";
        final String TAG = "Response";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Constants.BOOKING, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                pDialog.hide();

                new SweetAlertDialog(Registration.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("تم بنجاح")
                        .setContentText("انت الان قمت بالتسجيل")
                        .show();



            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                pDialog.hide();
                // show error message
                new SweetAlertDialog(Registration.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("خطأ")
                        .setContentText("الأتصال ضعيف اعد المحاوله")
                        .show();
            }
        }){

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", Constants.TOKEN);
                params.put("title", title);
                params.put("nId", nId);
                params.put("address", addressApi);
                params.put("email", emailApi);
                params.put("tel", tel);
                params.put("value", value);
                params.put("town", town);

                return params;
            }

        };


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private boolean promptParams(){
        title = fullName.getText().toString().trim();
        if (title.isEmpty())
            return false;

        nId = nationalId.getText().toString().trim();
        if (nId.isEmpty())
            return false;

        addressApi = address.getText().toString().trim();
        if (addressApi.isEmpty())
            return false;

        emailApi = email.getText().toString().trim();
        if (emailApi.isEmpty())
            return false;

        tel = phone.getText().toString().trim();
        if (nId.isEmpty())
            return false;

        if (value.trim().isEmpty())
            return false;

        if (town.trim().isEmpty())
            return false;

        return true;





    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_left_enter, R.anim.push_left_exit);
    }
}
