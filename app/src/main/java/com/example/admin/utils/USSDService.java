package com.example.admin.utils;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Once app is installed, you have to enable the service in Accessibility Settings (Setting->Accessibility Setting -> YourAppName).
 */
public class USSDService extends AccessibilityService {

    public static String TAG = USSDService.class.getSimpleName();
    private int operation_id;
    RequestQueue queue;


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "onAccessibilityEvent");

        AccessibilityNodeInfo source = event.getSource();
        /* if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && !event.getClassName().equals("android.app.AlertDialog")) { // android.app.AlertDialog is the standard but not for all phones  */
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && !String.valueOf(event.getClassName()).contains("AlertDialog")) {
            return;
        }
        if(event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && (source == null || !source.getClassName().equals("android.widget.TextView"))) {
            return;
        }
        if(event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && TextUtils.isEmpty(source.getText())) {
            return;
        }

        List<CharSequence> eventText;

        if(event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            eventText = event.getText();
        } else {
            eventText = Collections.singletonList(source.getText());
        }

        String text = processUSSDText(eventText);

        if( TextUtils.isEmpty(text) ) return;

        // Close dialog
        performGlobalAction(GLOBAL_ACTION_BACK); // This works on 4.1+ only

        Log.d(TAG, text);
        // Handle USSD response here

    }

    private String processUSSDText(List<CharSequence> eventText) {
        for (CharSequence s : eventText) {
            String text = String.valueOf(s);
            // Return text if text is the expected ussd response
//            Toast.makeText(getApplicationContext(), "ussd text" + text, Toast.LENGTH_LONG).show();
            if( text.contains("بنجاح تم") ||  text.contains("تم تحويل")) {
                Toast.makeText(getApplicationContext(), "تم التحويل بنجاح" + text, Toast.LENGTH_LONG).show();
                 SharedPrefs.save(getApplicationContext(), SharedPrefs.KEY_OPERATIONAL_STATE_PROVIDER, true);
                 sendOperationTrue();
                return text;
            }
        }
        return null;
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "onServiceConnected");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.packageNames = new String[]{"com.android.phone"};
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);
    }
    public void sendOperationTrue(){


        if (SharedPrefs.getBoolean(getApplicationContext(), SharedPrefs.KEY_OPERATION_IN)){
            operation_id = SharedPrefs.getInt(getApplicationContext(), SharedPrefs.KEY_OPERATION_ID);
            Log.d("id",operation_id + "");
        }
        queue = Volley.newRequestQueue(getApplicationContext());
        // creating a variable for our json object request and passing our url to it.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, NetworkUtils.URL_OPERATIONS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");
                            String code = jsonObject.getString("code");
                            if(code.equals("200")) {
                                SharedPrefs.save(getApplicationContext(), SharedPrefs.KEY_OPERATIONAL_STATE_SERVER, true);
                                SharedPrefs.save(getApplicationContext(), SharedPrefs.KEY_OPERATION_IN, false);
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                System.out.println(message);
                            }else if (code.equals("400")){
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                System.out.println(message);
                            }

                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Fail to get data.." + e.toString()
                                    + "\nCause " + e.getCause()
                                    + "\nmessage" + e.getMessage(), Toast.LENGTH_LONG).show();
                            System.out.println("error1:::" + e.toString()
                                    + "\nCause " + e.getCause()
                                    + "\nmessage" + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Fail to get data from server.." + error.toString()
                        + "\nCause " + error.getCause()
                        + "\nmessage" + error.getMessage(), Toast.LENGTH_LONG).show();
                System.out.println("error2" + error.toString()

                        + "\nCause " + error.getCause()
                        + "\nmessage" + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("id", String.valueOf(operation_id));
                return parameters;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //adding our string request to queue
        queue.add(stringRequest);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Ussd Service has been Stopped", Toast.LENGTH_SHORT).show();
    }
}
