package com.example.admin.utils;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.admin.MainActivity;
import com.example.admin.Models.OperationModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ScheduledService extends Service {
    private static ArrayList<OperationModel> operationsList;
    private static OperationModel operationModelObject;
    private Timer timer = new Timer();
    SimCardutil simCardutil = new SimCardutil();
    // creating a new variable for our request queue
    RequestQueue queue;
    private int operation_id;


    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        queue = Volley.newRequestQueue(getApplicationContext());

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                doOperation();   //Your code here
            }
        }, 0, 1 * 30 * 1000);//2 Minute
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        Toast.makeText(getApplicationContext(), "Scheduled Service has been stopped", Toast.LENGTH_SHORT).show();

    }

    public void doOperation() {

        // creating a variable for our json object request and passing our url to it.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, NetworkUtils.URL_OPERATIONS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        operationsList = new ArrayList<>();
                        operationsList.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            System.out.println(message);
                            String code = jsonObject.getString("code");
                            JSONArray operations = jsonObject.getJSONArray("data");
                            if (code.equals("200")) {
                                for (int i = 0; i < operations.length(); i++) {
                                    JSONObject object = operations.getJSONObject(i);
                                    boolean status = object.getString("status").equals("1");
                                    // on below line we are extracting data from our json object.
                                    operationsList.add(new OperationModel(
                                            object.getInt("id"),
                                            object.getString("mobile"),
                                            object.getString("category_id"),
                                            status,
                                            object.getString("sim_type"),
                                            object.getString("created_at")));

                                    System.out.println("jsonObject" + object.toString());

                                    Log.e("Operation Id 0 0 0 0 :", operationsList.get(0).getId() + "");
                                }
                                if (SharedPrefs.getBoolean(getApplicationContext(), SharedPrefs.KEY_OPERATION_IN)) {
                                    if (SharedPrefs.getBoolean(getApplicationContext(), SharedPrefs.KEY_OPERATIONAL_STATE_PROVIDER) == false) {
                                        simCardutil.tt1(operationsList.get(0), getApplicationContext());

                                    } else if (SharedPrefs.getBoolean(getApplicationContext(), SharedPrefs.KEY_OPERATIONAL_STATE_SERVER) == false) {
                                        sendOperationTrue();
                                    }
                                  Log.e("shared",
                                            "Operation in: "+SharedPrefs.getBoolean(getApplicationContext(), SharedPrefs.KEY_OPERATION_IN)+
                                                    "\n state provider: "+ SharedPrefs.getBoolean(getApplicationContext(), SharedPrefs.KEY_OPERATIONAL_STATE_PROVIDER)+
                                                    "\n state server: "+ SharedPrefs.getBoolean(getApplicationContext(), SharedPrefs.KEY_OPERATIONAL_STATE_SERVER)+
                                                    "\n operation id: "+ SharedPrefs.getInt(getApplicationContext(), SharedPrefs.KEY_OPERATION_ID));
                                    Toast.makeText(ScheduledService.this, "Operation in: "+SharedPrefs.getBoolean(getApplicationContext(), SharedPrefs.KEY_OPERATION_IN)+
                                                    "\n state provider: "+ SharedPrefs.getBoolean(getApplicationContext(), SharedPrefs.KEY_OPERATIONAL_STATE_PROVIDER)+
                                                    "\n state server: "+ SharedPrefs.getBoolean(getApplicationContext(), SharedPrefs.KEY_OPERATIONAL_STATE_SERVER)+
                                                    "\n operation id: "+ SharedPrefs.getInt(getApplicationContext(), SharedPrefs.KEY_OPERATION_ID)
                                            , Toast.LENGTH_LONG).show();

                                } else {

                                    SharedPrefs.save(getApplicationContext(), SharedPrefs.KEY_OPERATION_ID, operationsList.get(0).getId());
                                    SharedPrefs.save(getApplicationContext(), SharedPrefs.KEY_OPERATION_IN, true);
                                    SharedPrefs.save(getApplicationContext(), SharedPrefs.KEY_OPERATIONAL_STATE_SERVER, false);
                                    SharedPrefs.save(getApplicationContext(), SharedPrefs.KEY_OPERATIONAL_STATE_PROVIDER, false);
                                    simCardutil.tt1(operationsList.get(0), getApplicationContext());

                                }

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

                Log.e("shared",
                        "Operation in: "+SharedPrefs.getBoolean(getApplicationContext(), SharedPrefs.KEY_OPERATION_IN)+
                                "\n state provider: "+ SharedPrefs.getBoolean(getApplicationContext(), SharedPrefs.KEY_OPERATIONAL_STATE_PROVIDER)+
                                "\n state server: "+ SharedPrefs.getBoolean(getApplicationContext(), SharedPrefs.KEY_OPERATIONAL_STATE_SERVER)+
                                "\n operation id: "+ SharedPrefs.getInt(getApplicationContext(), SharedPrefs.KEY_OPERATION_ID));
                Toast.makeText(ScheduledService.this, "Operation in: "+SharedPrefs.getBoolean(getApplicationContext(), SharedPrefs.KEY_OPERATION_IN)+
                                "\n state provider: "+ SharedPrefs.getBoolean(getApplicationContext(), SharedPrefs.KEY_OPERATIONAL_STATE_PROVIDER)+
                                "\n state server: "+ SharedPrefs.getBoolean(getApplicationContext(), SharedPrefs.KEY_OPERATIONAL_STATE_SERVER)+
                                "\n operation id: "+ SharedPrefs.getInt(getApplicationContext(), SharedPrefs.KEY_OPERATION_ID)
                        , Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
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

    public void sendOperationTrue() {

        if (SharedPrefs.getBoolean(getApplicationContext(), SharedPrefs.KEY_OPERATION_IN)) {
            operation_id = SharedPrefs.getInt(getApplicationContext(), SharedPrefs.KEY_OPERATION_ID);
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
                            if (code.equals("200")) {
                                SharedPrefs.save(getApplicationContext(), SharedPrefs.KEY_OPERATIONAL_STATE_SERVER, true);
                                SharedPrefs.save(getApplicationContext(), SharedPrefs.KEY_OPERATION_IN, false);
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                System.out.println(message);
                            } else if (code.equals("400")) {
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


}
