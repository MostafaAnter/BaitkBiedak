package mostafa_anter.baitkbiedak.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import mostafa_anter.baitkbiedak.R;
import mostafa_anter.baitkbiedak.activities.MainActivity;
import mostafa_anter.baitkbiedak.app.AppController;
import mostafa_anter.baitkbiedak.constants.Constants;
import mostafa_anter.baitkbiedak.models.FeedPOJO;
import mostafa_anter.baitkbiedak.parser.JsonParser;
import mostafa_anter.baitkbiedak.utils.NetworkUtil;

/**
 * Created by mostafa on 25/03/16.
 */
public class BaitkBiedakService extends IntentService {
    private static String message  = "تصفح اخر اخبار بيتك بإيدك";
    // default constructor
    public BaitkBiedakService() {
        super("BaitkBiedakService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("service", "service started");
        makeNewsRequest();
    }


    static public class AlarmReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String status = NetworkUtil.getConnectivityStatusString(context);

            // this broadcast will receive signal when mobile connect with internet or call it manually from activity
            if (status.equalsIgnoreCase("Wifi enabled") ||
                    status.equalsIgnoreCase("Mobile data enabled")) {
                Intent sendIntent = new Intent(context, BaitkBiedakService.class);
                context.startService(sendIntent);
            }

        }
    }

    private void createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        //Create Intent to launch this Activity again if the notification is clicked.
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(this, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(intent);

        // Sets the ticker text
        builder.setTicker(getResources().getString(R.string.app_name));

        // Sets the small icon for the ticker
        builder.setSmallIcon(R.drawable.ic_notification);

        // Cancel the notification when clicked
        builder.setAutoCancel(true);

        // set sound
        Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sound);
        builder.setSound(sound);

        // Build the notification
        Notification notification = builder.build();

        // Inflate the notification layout as RemoteViews
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification);

        // Set text on a TextView in the RemoteViews programmatically.
        contentView.setTextViewText(R.id.textView, message);



        /* Workaround: Need to set the content view here directly on the notification.
         * NotificationCompatBuilder contains a bug that prevents this from working on platform
         * versions HoneyComb.
         * See https://code.google.com/p/android/issues/detail?id=30495
         */
        notification.contentView = contentView;


        // START_INCLUDE(notify)
        // Use the NotificationManager to show the notification
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(0, notification);
    }

    private void makeNewsRequest(){
        // Tag used to cancel the request
        String  tag_string_req = "string_req";
        final String TAG = "Response";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Constants.NEWS_API, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);

                message = JsonParser.parseForNotifecation(response);
                createNotification();




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


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }
}
