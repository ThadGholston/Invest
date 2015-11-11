package com.investmobile.invest;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;

/**
 * Created by thad on 11/9/15.
 */

public class StockCardsFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private CustomAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean hasBeenVisited = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stocks, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rv = (RecyclerView) getActivity().findViewById(R.id.recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        FavoritesLoader loader = new FavoritesLoader();
        rv.setAdapter(new CustomAdapter(loader.getFavorites()));
        Log.v("StockCardsFragment", "TEST");
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible & !hasBeenVisited) {

        }
    }

    private class CustomAdapter extends RecyclerView.Adapter<FavoritesViewHolder> {
        List<Favorite> favorites;

        public CustomAdapter(List<Favorite> favorites) {
            this.favorites = favorites;
        }

        @Override
        public FavoritesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.graph_card, parent, false);
            FavoritesViewHolder fvh = new FavoritesViewHolder(view);
            return fvh;
        }

        @Override
        public void onBindViewHolder(FavoritesViewHolder holder, int position) {
            String symbol = favorites.get(position).symbol;
            holder.symbolTextView.setText(symbol);
            loadViewHolderWithGraph(symbol, holder);
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public int getItemCount() {
            return favorites.size();
        }

        private void loadViewHolderWithGraph(String symbol, FavoritesViewHolder holder) {
            String url = "http://ec2-52-2-91-221.compute-1.amazonaws.com/api/graph/" + symbol;
            holder.graphLayoutContainer.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.VISIBLE);
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            ImageRequest request = new ImageRequest(url, new CustomImageResponseListener(holder), 0, 0, null, Bitmap.Config.RGB_565, new CustomErrorListener(holder));
            queue.add(request);
        }

        private class CustomImageResponseListener implements Response.Listener<Bitmap> {

            FavoritesViewHolder holder;

            public CustomImageResponseListener(FavoritesViewHolder holder) {
                this.holder = holder;
            }

            @Override
            public void onResponse(Bitmap response) {
                holder.graphImageView.setImageBitmap(response);
                holder.graphLayoutContainer.setVisibility(View.VISIBLE);
                holder.progressBar.setVisibility(View.GONE);
            }
        }

        private class CustomErrorListener implements Response.ErrorListener {
            FavoritesViewHolder holder;

            public CustomErrorListener(FavoritesViewHolder holder) {
                this.holder = holder;
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                holder.graphLayoutContainer.setVisibility(View.VISIBLE);
                holder.progressBar.setVisibility(View.GONE);
            }
        }
    }

    private class FavoritesViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView symbolTextView;
        ImageView graphImageView;
        LinearLayout graphLayoutContainer;
        ProgressBar progressBar;

        public FavoritesViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            symbolTextView = (TextView) itemView.findViewById(R.id.stockSymbolText);
            graphImageView = (ImageView) itemView.findViewById(R.id.graph);
            graphLayoutContainer = (LinearLayout) itemView.findViewById(R.id.stockView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBarStocks);
        }
    }

}
