package com.investmobile.invest;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.IOException;

public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    final String BASE_URL_GRAPH = "http://ec2-52-2-91-221.compute-1.amazonaws.com/api/graph/";
    //    String baseUrl = "https://www.quandl.com";
    private String[] mWidgetItems = {"GOOG", "MSFT", "AAPL", "FB"};
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
        return mWidgetItems.length;
    }

    public RemoteViews getViewAt(final int position) {
        // position will always range from 0 to getCount() - 1.

        // We construct a remote views item based on our widget item xml file, and set the
        // text based on the position.
        final RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);

        //StockData stockData = getStockData(mWidgetItems[position]);

//        rv.setTextViewText(R.id.stock_symbol, mWidgetItems[position]);
//        rv.setTextViewText(R.id.cost, "000");

        RequestCreator requestCreator = Picasso.with(mContext).load(BASE_URL_GRAPH + mWidgetItems[position]);
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

//    public StockData getStockData(String symbol) {
//        final StockData[] stockData = new StockData[1];
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(baseUrl)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        ApiEndpointInterface apiService = retrofit.create(ApiEndpointInterface.class);
//        Call<StockData> call = apiService.getStock(symbol);
//        call.enqueue(new Callback<StockData>() {
//            @Override
//            public void onResponse(Response<StockData> response, Retrofit retrofit) {
//                stockData[0] = response.body();
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//
//            }
//        });
//        return stockData[0];
//    }

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

    }
}
