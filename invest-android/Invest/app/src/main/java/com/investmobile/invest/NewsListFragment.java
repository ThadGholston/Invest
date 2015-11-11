package com.investmobile.invest;

import android.content.Intent;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;


/**
 * Created by thad on 11/9/15.
 */
public class NewsListFragment extends ListFragment {
    private ArrayList<News> data = new ArrayList<News>();
    private ArrayAdapter adapter;
    private boolean isNotFirstTime = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_2, android.R.id.text1, data) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                text1.setEllipsize(TextUtils.TruncateAt.END);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                text2.setLines(3);
                text2.setEllipsize(TextUtils.TruncateAt.END);

                text1.setText(data.get(position).title);
                text2.setText(data.get(position).description);
                return view;
            }
        };

        // Bind to our new adapter.
        setListAdapter(adapter);
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        Log.v("ListFragment", "Visibility: " + menuVisible);
        if (menuVisible && !isNotFirstTime){
            fillNewsList();
            isNotFirstTime = true;
        }
    }

    public void fillNewsList() {
        ListView listView = getListView();
        listView.setVisibility(View.GONE);
        ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.progressBarNews);
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://ec2-52-2-91-221.compute-1.amazonaws.com/api/news";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new MyResponseListener(), new MyErrorListener());
        queue.add(jsonArrayRequest);
    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        super.onListItemClick(l, v, pos, id);
        News news = data.get(pos);
        Intent intent = new Intent(getActivity(), NewsActivity.class);
        intent.putExtra("title", news.title);
        intent.putExtra("link", news.link);
        getActivity().startActivity(intent);
    }

    private class MyResponseListener implements Response.Listener<JSONArray>{
        @Override
        public void onResponse(JSONArray response) {
            data.clear();
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject jo = response.getJSONObject(i);
                    News news = new News();
                    news.description = Html.fromHtml(jo.getString("desc")).toString() ;
                    news.link = jo.getString("link");
                    news.publicationDate = jo.getString("pubDate");
                    news.title = Html.fromHtml(jo.getString("title")).toString();
                    if (i > 0 && !response.getJSONObject(i - 1).get("link").equals(news.link)) {
                        data.add(news);
                    } else if (i == 0) {
                        data.add(news);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            adapter.notifyDataSetChanged();
            ListView listView = (ListView) getListView();
            listView.setVisibility(View.VISIBLE);
            ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.progressBarNews);
            progressBar.setVisibility(View.GONE);
        }
    }

    private class MyErrorListener implements Response.ErrorListener{

        @Override
        public void onErrorResponse(VolleyError error) {
            TextView textView = (TextView) getView().findViewById(android.R.id.empty);
            textView.setText("Oh snap. There was an error requesting the lastest news. Try again later.");
            ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.progressBarNews);
            progressBar.setVisibility(View.GONE);
        }
    }

}
