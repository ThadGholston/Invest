package com.investmobile.invest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
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

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by thad on 11/9/15.
 */

public class StockCardsFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private CustomAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean hasBeenVisited = false;
    private final static String APP_AUTH_SHARED_PREFS = "auth_preferences";
    private ArrayList<Favorite> favorites = new ArrayList<>();
    private RecyclerView rv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stocks, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setVisibility(View.GONE);
        ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.latest_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        mAdapter = new CustomAdapter(favorites);
        mRecyclerView.setAdapter(mAdapter);
        RequestQueue queue = new Volley().newRequestQueue(getContext());
        JsonArrayRequest request = new JsonArrayRequest("http://ec2-52-2-91-221.compute-1.amazonaws.com/api/favorite", new CustomResponseListener(), new CustomErrorListener()) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                SharedPreferences sharedPrefs = StockCardsFragment.this.getContext().getSharedPreferences(APP_AUTH_SHARED_PREFS, Context.MODE_PRIVATE);
                String token = sharedPrefs.getString("authToken", null);
                map.put("x-auth-token", token);
                return map;
            }
        };
        queue.add(request);
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible & !hasBeenVisited) {

        }
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
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
    @Override
    public void onStart() {
        super.onStart();
        baseTest();
    }

    @Override
    public void onResume() {
        super.onResume();
        baseTest();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        baseTest();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        baseTest();
    }

    public void baseTest() {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        boolean isUserLoggedIn = sharedPrefs.getBoolean("userLoggedInState", false);
        if (!isUserLoggedIn) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            getActivity().finish();
        }
    }


    private class CustomResponseListener implements Response.Listener<JSONArray> {

        @Override
        public void onResponse(JSONArray response) {
            favorites.clear();
            for (int i = 0; i < response.length(); i++) {
                Favorite favorite = new Favorite();
                try {
                    JSONObject object = (JSONObject) response.get(i);
                    favorite.symbol = object.getString("symbol");
                    String type = object.getString("type");
                    switch (type) {
                        case "mutual_fund":
                            favorite.type = FavoriteTypeEnum.MUTUAL_FUND;
                            break;
                        case "index":
                            favorite.type = FavoriteTypeEnum.INDEX;
                            break;
                        default:
                            favorite.type = FavoriteTypeEnum.STOCK;
                            break;
                    }
                    favorites.add(favorite);
                } catch (JSONException e) {
                    ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.latest_progress_bar);
                    progressBar.setVisibility(View.GONE);
                    TextView emptyTextView = (TextView) getActivity().findViewById(R.id.latest_empty_display);
                    emptyTextView.setVisibility(View.VISIBLE);
                    emptyTextView.setText("Opps. We're experiencing some technical difficulites right now. Try again later");
                    mRecyclerView.setVisibility(View.GONE);
                }
            }
            if (favorites.size() == 0){
                ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.latest_progress_bar);
                progressBar.setVisibility(View.GONE);
                TextView emptyTextView = (TextView) getActivity().findViewById(R.id.latest_empty_display);
                emptyTextView.setText("Add some favorites by searching");
                emptyTextView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            } else {
                ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.latest_progress_bar);
                progressBar.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private class CustomErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.latest_progress_bar);
            progressBar.setVisibility(View.GONE);
            TextView emptyTextView = (TextView) getActivity().findViewById(R.id.latest_empty_display);
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText("Opps. We're experiencing some technical difficulites right now. Try again later");
            mRecyclerView.setVisibility(View.GONE);
        }
    }

}
