package com.investmobile.invest;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class SearchActivity extends AppCompatActivity {
    private ArrayList<SearchResult> searchResults = new ArrayList<>();
    private String searchEndpoint = "http://ec2-52-2-91-221.compute-1.amazonaws.com/api/search/";
    private ArrayAdapter<SearchResult> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Search Results");
        ListView listView = (ListView) findViewById(R.id.search_list_view);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, searchResults) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                text1.setEllipsize(TextUtils.TruncateAt.END);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                text2.setLines(3);
                text2.setEllipsize(TextUtils.TruncateAt.END);

                SearchResult searchResult = searchResults.get(position);
                String firstLine = searchResult.name;
                String secondLine = "Symbol: " + searchResult.symbol + " | " + "Type: ";
                if (searchResult.type.equals(FavoriteTypeEnum.INDEX)) {
                    secondLine += "Index";
                } else if (searchResult.type.equals(FavoriteTypeEnum.MUTUAL_FUND)) {
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                launchViewInfoActivity(searchResults.get(position));
            }
        });

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }

    }


    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }


    private void doMySearch(String query) {
        ListView listView = (ListView) findViewById(R.id.search_list_view);
        listView.setVisibility(View.GONE);
        TextView textView = (TextView) findViewById(R.id.search_empty);
        textView.setVisibility(View.GONE);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.search_results_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        String url = searchEndpoint + URLEncoder.encode(query);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new MyResponseListener(), new MyErrorListener());
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonArrayRequest);
    }


    private void launchViewInfoActivity(SearchResult searchResult) {
        Intent intent = new Intent(this, SearchResultViewActivity.class);
        intent.putExtra("symbol", searchResult.symbol);
        intent.putExtra("name", searchResult.name);
        if (searchResult.type.equals(FavoriteTypeEnum.MUTUAL_FUND)) {
            intent.putExtra("type", "mutual_fund");
        } else if (searchResult.type.equals(FavoriteTypeEnum.INDEX.INDEX)) {
            intent.putExtra("type", "index");
        } else {
            intent.putExtra("type", "stock");
        }

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_table_query, menu);

        // Setting up search bar
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.menu_search).getActionView();
        Log.v("MainFragment", getComponentName().toShortString());
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setIconified(false);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchRequested();
            }
        });
        searchView.clearFocus();
        return true;
    }

    private class MyResponseListener implements Response.Listener<JSONArray> {

        @Override
        public void onResponse(JSONArray response) {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.search_results_progress_bar);
            progressBar.setVisibility(View.GONE);
            try {
                searchResults.clear();
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
                    SearchResult searchResult = new SearchResult();
                    searchResult.type = type;
                    searchResult.symbol = symbol;
                    searchResult.name = name;
                    searchResults.add(searchResult);
                }
                adapter.notifyDataSetChanged();
                TextView textView = (TextView) findViewById(R.id.search_empty);
                textView.setVisibility(View.GONE);
                ListView listView = (ListView) findViewById(R.id.search_list_view);
                listView.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                TextView textView = (TextView) findViewById(R.id.search_empty);
                textView.setText("Opps. There was an error. Please try again later");
                textView.setVisibility(View.VISIBLE);
            }
        }
    }

    private class MyErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.search_results_progress_bar);
            progressBar.setVisibility(View.GONE);
            TextView textView = (TextView) findViewById(R.id.search_empty);
            textView.setText("Opps. There was an error. Please try again later");
            textView.setVisibility(View.VISIBLE);
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
