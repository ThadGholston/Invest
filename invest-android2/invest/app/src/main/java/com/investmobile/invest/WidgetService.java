//package com.investmobile.invest;
//
//import android.app.Service;
//import android.appwidget.AppWidgetManager;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.IBinder;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonArrayRequest;
//import com.android.volley.toolbox.Volley;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class WidgetService extends Service {
//
//    @Override
//    public void onStart(Intent intent, int startId) {
//        super.onStart(intent, startId);
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        SharedPreferences sharedPrefs = this.getSharedPreferences(APP_AUTH_SHARED_PREFS, Context.MODE_PRIVATE);
//        String token = sharedPrefs.getString("authToken", null);
//        requestFavorites(token);
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
//
//    private void requestFavorites(final String token) {
//        String url = "http://ec2-52-2-91-221.compute-1.amazonaws.com/api/favorite";;
//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new MyResponseListener(), new MyErrorListener()) {
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> map = new HashMap<>();
//                map.put("x-auth-token", token);
//                System.out.println("TOKEN: " + token);
//                return map;
//            }
//
//        };
//        RequestQueue queue = Volley.newRequestQueue(this);
//        queue.add(jsonArrayRequest);
//    }
//
//
//    private class MyResponseListener implements Response.Listener<JSONArray> {
//        public MyResponseListener(int[] allWidgetIDs){
//
//        }
//
//        @Override
//        public void onResponse(JSONArray response) {
//            //ProgressBar progressBar = (ProgressBar) findViewById(R.id.search_results_progress_bar);
//            //progressBar.setVisibility(View.GONE);
//            System.out.println("--> response <--");
//            System.out.println(response.length() + " <- response");
//            try {
//                System.out.println("RESPONSE LENGTH: " + response.length());
//                mWidgetItems.clear();
//                for (int i = 0; i < response.length(); i++) {
//                    JSONObject object = response.getJSONObject(i);
//                    String symbol = object.getString("symbol");
//
//                    mWidgetItems.add(symbol);
//                }
//            } catch (JSONException e) {
//            }
//        }
//    }
//
//    private class MyErrorListener implements Response.ErrorListener {
//
//        @Override
//        public void onErrorResponse(VolleyError error) {
//        }
//    }
//}
