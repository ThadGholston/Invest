package com.investmobile.invest;

import android.support.v4.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by thad on 11/9/15.
 */
public class NewsListFragment extends ListFragment {
    ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        SimpleAdapter adapter = new SimpleAdapter(getActivity(), data,
                android.R.layout.simple_list_item_2,
                new String[]{"title", "description"},
                new int[]{android.R.id.text1, android.R.id.text2});

        // Bind to our new adapter.
        setListAdapter(adapter);
        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        super.onListItemClick(l, v, pos, id);
        //TODO: Launch new activity with webview
    }

    private class GetNewsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ListView listView = (ListView) getListView();
            listView.setVisibility(View.GONE);
            ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.progressBarNews);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Make request and update list
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ListView listView = (ListView) getListView();
            listView.setVisibility(View.VISIBLE);
            ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.progressBarNews);
            progressBar.setVisibility(View.GONE);
        }

    }

}
