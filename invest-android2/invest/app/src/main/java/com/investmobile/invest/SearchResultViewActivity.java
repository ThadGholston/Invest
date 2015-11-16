package com.investmobile.invest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class SearchResultViewActivity extends AppCompatActivity {
    private final static String APP_AUTH_SHARED_PREFS = "auth_preferences";
    ImageView imageView;
    TextView symbolView;
    TextView nameView;
    TextView typeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        final String symbol = intent.getStringExtra("symbol");
        final String name = intent.getStringExtra("name");
        final String type = intent.getStringExtra("type");
        Log.v("TEST", type);

        symbolView = (TextView) findViewById(R.id.symbol);
        symbolView.setText(symbol);
        nameView = (TextView) findViewById(R.id.name);
        nameView.setText(name);
        imageView = (ImageView) findViewById(R.id.graph);
        imageView.setVisibility(View.GONE);
        final RequestQueue queue = Volley.newRequestQueue(SearchResultViewActivity.this);
        String url = "http://ec2-52-2-91-221.compute-1.amazonaws.com/api/graph/" + symbol;
        ImageRequest request = new ImageRequest(url, new CustomImageResponseListener(), 0, 0, null, Bitmap.Config.RGB_565, new CustomErrorListener());
        queue.add(request);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            String url = "http://ec2-52-2-91-221.compute-1.amazonaws.com/api/favorite";
            @Override
            public void onClick(View view) {
                StringRequest request1 = new StringRequest(Request.Method.POST, url, new CustomStringResponseListener(), new CustomErrorListener()){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("symbol", symbol);
                        map.put("type", type);
                        return map;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> map = new HashMap<String, String>();
                        SharedPreferences sharedPrefs = SearchResultViewActivity.this.getApplicationContext().getSharedPreferences(APP_AUTH_SHARED_PREFS, Context.MODE_PRIVATE);
                        String token = sharedPrefs.getString("authToken", null);
                        map.put("x-auth-token", token);
                        return map;
                    }
                };
                queue.add(request1);
            }

        });
    }

    private class CustomImageResponseListener implements Response.Listener<Bitmap> {


        @Override
        public void onResponse(Bitmap response) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(response);
        }
    }

    private class CustomErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
        }
    }

    private class CustomStringResponseListener implements Response.Listener<String>{

        @Override
        public void onResponse(String response) {
            Context context = getApplicationContext();
            CharSequence text = "Favorite added successfully";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    private class CustomStringErrorListener implements Response.ErrorListener{

        @Override
        public void onErrorResponse(VolleyError error) {
            Context context = getApplicationContext();
            CharSequence text = "Opps. We're having some technical difficulites. Try again later";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

}
