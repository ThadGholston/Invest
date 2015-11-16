package com.investmobile.invest;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    final String BASE_URL_GRAPH = "http://ec2-52-2-91-221.compute-1.amazonaws.com/api/graph/";
    private final static String APP_AUTH_SHARED_PREFS = "auth_preferences";
    //    String baseUrl = "https://www.quandl.com";
    private ArrayList<String> mWidgetItems = new ArrayList<String>();
    private Context mContext;
    private int mAppWidgetId;

    public ListRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    public void onCreate() {
        // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
//        for (int i = 0; i < mCount; i++) {
//            mWidgetItems.add(new WidgetItem(i + "!"));
//        }
    }

    public void onDestroy() {
        // In onDestroy() you should tear down anything that was setup for your data source,
        // eg. cursors, connections, etc.

    }

    public int getCount() {
        return mWidgetItems.size();
    }

    public RemoteViews getViewAt(final int position) {
        // position will always range from 0 to getCount() - 1.

        // We construct a remote views item based on our widget item xml file, and set the
        // text based on the position.
        final RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);

        //StockData stockData = getStockData(mWidgetItems[position]);

//        rv.setTextViewText(R.id.stock_symbol, mWidgetItems[position]);
//        rv.setTextViewText(R.id.cost, "000");

        RequestCreator requestCreator = Picasso.with(mContext).load(BASE_URL_GRAPH + mWidgetItems.get(position));
        try {
            rv.setImageViewBitmap(R.id.stock_graph, requestCreator.get());
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Next, we set a fill-intent which will be used to fill-in the pending intent template
        // which is set on the collection view in ListWidgetProvider.


        Bundle extras = new Bundle();
        extras.putInt(ListWidgetProvider.EXTRA_ITEM, position);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent);

        // Return the remote views object.
        return rv;
    }


    public RemoteViews getLoadingView() {
        // You can create a custom loading view (for instance when getViewAt() is slow.) If you
        // return null here, you will get the default loading view.
        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
        // This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
        // on the collection view corresponding to this factory. You can do heaving lifting in
        // here, synchronously. For example, if you need to process an image, fetch something
        // from the network, etc., it is ok to do it here, synchronously. The widget will remain
        // in its current state while work is being done here, so you don't need to worry about
        // locking up the widget.
        Log.v("ListWidgetService", "TEST");
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        RequestFuture<JSONArray> future = RequestFuture.newFuture();
        JsonArrayRequest request = new JsonArrayRequest("http://ec2-52-2-91-221.compute-1.amazonaws.com/api/favorite", future, future){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                SharedPreferences sharedPrefs = mContext.getSharedPreferences(APP_AUTH_SHARED_PREFS, Context.MODE_PRIVATE);
                String token = sharedPrefs.getString("authToken", null);
                map.put("x-auth-token", token);
                System.out.println("TOKEN: " + token);
                return map;
            }
        };
        requestQueue.add(request);
        try {
            mWidgetItems.clear();
            JSONArray response = future.get(); // this will block (forever)
            for (int i = 0; i < response.length(); i++){
                String symbol = response.getJSONObject(i).getString("symbol");
                mWidgetItems.add(symbol);
            }
//            for (int i = 0; i< response.length();i++){
//                getViewAt(i);
//            }
        } catch (InterruptedException e) {
            // exception handling
        } catch (ExecutionException e) {
            // exception handling
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}