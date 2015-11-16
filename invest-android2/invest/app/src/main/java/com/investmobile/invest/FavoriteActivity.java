package com.investmobile.invest;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FavoriteActivity extends AppCompatActivity {

    private final static String APP_AUTH_SHARED_PREFS = "auth_preferences";
    private ArrayList<Favorite> favoriteResults = new ArrayList<>();
    private String searchEndpoint = "http://ec2-52-2-91-221.compute-1.amazonaws.com/api/favorite";
    private ArrayAdapter<Favorite> adapter;
    private JSONArray responseArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        setTitle("Favorites");
        ListView listView = (ListView) findViewById(R.id.favoriteListView);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, favoriteResults) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                text1.setEllipsize(TextUtils.TruncateAt.END);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                text2.setLines(3);
                text2.setEllipsize(TextUtils.TruncateAt.END);

                Favorite favorite = favoriteResults.get(position);
                String firstLine = favorite.name;
                String secondLine = "Symbol: " + favorite.symbol + " | " + "Type: ";
                if (favorite.type.equals(FavoriteTypeEnum.INDEX)) {
                    secondLine += "Index";
                } else if (favorite.type.equals(FavoriteTypeEnum.MUTUAL_FUND)) {
                    secondLine += "Mutual Fund";
                } else {
                    secondLine += "Stock";
                }
                text1.setText(firstLine);
                text2.setText(secondLine);
                return view;
            }
        };
        listView.setAdapter(adapter);

        final Context context = this;

        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context, android.support.v7.appcompat.R.style.Base_Theme_AppCompat);
                alertDialogBuilder
                        .setTitle("Delete Favorite?")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Do something with parameter.
                                //doSomeStuff(someParameter);


                                RequestQueue queue = Volley.newRequestQueue(context);
                                StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, "http://ec2-52-2-91-221.compute-1.amazonaws.com/api/account", new MyCustomResponseListener(), new MyCustomErrorListener()) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {

                                        //Log.v("SignUpPage", "TEST");
                                        HashMap<String, String> map = new HashMap<>();
                                        String oldPassword = currentPassword.getText().toString();
                                        String newPassword = passwordField1.getText().toString();
                                        map.put("old_password", oldPassword);
                                        map.put("new_password", newPassword);
                                        return map;

                                    }

                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        HashMap<String, String> map = new HashMap<>();
                                        SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences(APP_AUTH_SHARED_PREFS, Context.MODE_PRIVATE);
                                        String token = sharedPrefs.getString("authToken", null);
                                        map.put("x-auth-token", token);
                                        return map;
                                    }
                                };




                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();

                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();


            }
        });*/

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }

        requestFavorites();

    }


    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }


    private void requestFavorites() {
        ListView listView = (ListView) findViewById(R.id.favoriteListView);
        listView.setVisibility(View.GONE);
        //TextView textView = (TextView) findViewById(R.id.search_empty);
        //textView.setVisibility(View.GONE);
        //ProgressBar progressBar = (ProgressBar) findViewById(R.id.search_results_progress_bar);
        //progressBar.setVisibility(View.VISIBLE);
        String url = searchEndpoint; // + URLEncoder.encode(query);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new MyResponseListener(), new MyErrorListener()) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences(APP_AUTH_SHARED_PREFS, Context.MODE_PRIVATE);
                String token = sharedPrefs.getString("authToken", null);
                map.put("x-auth-token", token);
                System.out.println("TOKEN: " + token);
                return map;
            }

        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonArrayRequest);
    }

    private void doMySearch(String query) {
        ListView listView = (ListView) findViewById(R.id.favoriteListView);
        listView.setVisibility(View.GONE);
        //TextView textView = (TextView) findViewById(R.id.search_empty);
        //textView.setVisibility(View.GONE);
        //ProgressBar progressBar = (ProgressBar) findViewById(R.id.search_results_progress_bar);
        //progressBar.setVisibility(View.VISIBLE);
        String url = searchEndpoint + URLEncoder.encode(query);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new MyResponseListener(), new MyErrorListener());
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonArrayRequest);
    }

    private void launchViewInfoActivity(SearchResult searchResult) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_favorite, menu);

        // Setting up search bar
        //SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.menu_search).getActionView();
        Log.v("MainFragment", getComponentName().toShortString());
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setSubmitButtonEnabled(true);
        //searchView.setIconified(false);
        /*searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchRequested();
            }
        });*/
        return true;
    }

    private class MyResponseListener implements Response.Listener<JSONArray> {

        @Override
        public void onResponse(JSONArray response) {
            //ProgressBar progressBar = (ProgressBar) findViewById(R.id.search_results_progress_bar);
            //progressBar.setVisibility(View.GONE);
            responseArray = response;
            System.out.println("--> response <--");
            System.out.println(response.length() + " <- response");
            try {
                System.out.println("RESPONSE LENGTH: " + response.length());
                favoriteResults.clear();
                for (int i = 0; i < response.length(); i++) {
                    JSONObject object = response.getJSONObject(i);
                    String name = object.getString("name");
                    String symbol = object.getString("symbol");
                    String stringType = object.getString("type");
                    FavoriteTypeEnum type;
                    if (stringType.equalsIgnoreCase("index")) {
                        type = FavoriteTypeEnum.INDEX;
                    } else if (stringType.equalsIgnoreCase("mutual_fund")) {
                        type = FavoriteTypeEnum.MUTUAL_FUND;
                    } else {
                        type = FavoriteTypeEnum.STOCK;
                    }
                   Favorite favoriteResult = new Favorite();
                    favoriteResult.type = type;
                    favoriteResult.symbol = symbol;
                    favoriteResult.name = name;
                    favoriteResults.add(favoriteResult);
                }
                adapter.notifyDataSetChanged();
                //TextView textView = (TextView) findViewById(R.id.search_empty);
                //textView.setVisibility(View.GONE);
                ListView listView = (ListView) findViewById(R.id.favoriteListView);
                listView.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                //TextView textView = (TextView) findViewById(R.id.search_empty);
                //textView.setText("Opps. There was an error. Please try again later");
                //textView.setVisibility(View.VISIBLE);
                System.out.println(e.toString());
            }
        }
    }

    private class MyErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            //ProgressBar progressBar = (ProgressBar) findViewById(R.id.search_results_progress_bar);
            //progressBar.setVisibility(View.GONE);
            //TextView textView = (TextView) findViewById(R.id.search_empty);
            //textView.setText("Opps. There was an error. Please try again later");
            //textView.setVisibility(View.VISIBLE);
            System.out.println(error.toString());
        }
    }

    private class CustomArrayAdapter extends ArrayAdapter {

        public CustomArrayAdapter(Context context, int simple_list_item_2, int resource, ArrayList<SearchResult> searchResults) {
            super(context, resource);

        }


    }

    private class SearchResult {
        public String name;
        public String symbol;
        public FavoriteTypeEnum type;
    }












}

