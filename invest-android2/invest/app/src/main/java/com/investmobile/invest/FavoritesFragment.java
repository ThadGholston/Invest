package com.investmobile.invest;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FavoritesFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private final static String APP_AUTH_SHARED_PREFS = "auth_preferences";
    private ArrayList<Favorite> favoriteResults = new ArrayList<>();
    private String searchEndpoint = "http://ec2-52-2-91-221.compute-1.amazonaws.com/api/favorite";
    private ArrayAdapter<Favorite> adapter;
    private JSONArray responseArray;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        getActivity().setTitle("Favorites");
        ListView listView = (ListView) view.findViewById(R.id.favoriteListView);
        adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_2, android.R.id.text1, favoriteResults) {
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


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String type = "";
                String symbol = "";
                try {
                    type = responseArray.getJSONObject(position).getString("type");
                    symbol = responseArray.getJSONObject(position).getString("symbol");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final String acctType = type;
                final String acctSymbol = symbol;


                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getContext(), android.support.v7.appcompat.R.style.Base_Theme_AppCompat);
                alertDialogBuilder
                        .setTitle("Delete Favorite?")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Do something with parameter.
                                //doSomeStuff(someParameter);


                                RequestQueue queue = Volley.newRequestQueue(getContext());
                                StringRequest jsonObjectRequest = new StringRequest(Request.Method.PUT, "http://ec2-52-2-91-221.compute-1.amazonaws.com/api/favorite", new MyCustomResponseListener(), new MyCustomErrorListener()) {

                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        HashMap<String, String> map = new HashMap<>();
                                        map.put("type", acctType);
                                        map.put("symbol", acctSymbol);
                                        return map;
                                    }

                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        HashMap<String, String> map = new HashMap<>();
                                        SharedPreferences sharedPrefs = getContext().getSharedPreferences(APP_AUTH_SHARED_PREFS, Context.MODE_PRIVATE);
                                        String token = sharedPrefs.getString("authToken", null);
                                        map.put("x-auth-token", token);
                                        return map;
                                    }
                                };

                                queue.add(jsonObjectRequest);
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
        });

        requestFavorites();

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


    @Override
    public void onStart() {
        super.onStart();
        try {
            mListener = (OnFragmentInteractionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }


    private void requestFavorites() {
        ListView listView = (ListView) getActivity().findViewById(R.id.favoriteListView);
        listView.setVisibility(View.GONE);
        String url = searchEndpoint;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new MyResponseListener(), new MyErrorListener()) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                SharedPreferences sharedPrefs = getContext().getSharedPreferences(APP_AUTH_SHARED_PREFS, Context.MODE_PRIVATE);
                String token = sharedPrefs.getString("authToken", null);
                map.put("x-auth-token", token);
                System.out.println("TOKEN: " + token);
                return map;
            }

        };
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(jsonArrayRequest);
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
                ListView listView = (ListView) getActivity().findViewById(R.id.favoriteListView);
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


    private class MyCustomResponseListener implements Response.Listener<String> {

        @Override
        public void onResponse(String response) {
            //progressBar.setVisibility(View.GONE);
            String token = null;
//            try {
//                SharedPreferences sharedPrefs = getContext().getSharedPreferences(APP_AUTH_SHARED_PREFS, Context.MODE_PRIVATE);
//                JSONObject object = new JSONObject(response);
//                token = object.getString("token");
//                Log.v("INVEST", token);
//                sharedPrefs.edit().putString("authToken", token).apply();
//                sharedPrefs.edit().putBoolean("userLoggedInState", true).apply();

            //Intent intent = new Intent(SignUpPageActivity.this, MainActivity.class);
            // startActivity(intent);
//            } catch (JSONException e) {
//                setServerError();
//            }
            Toast.makeText(getContext(), "Favorite deleted succesfully", Toast.LENGTH_LONG).show();

        }
    }

    private class MyCustomErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            setServerError();
            System.out.println("ERROR: " + error);
        }
    }

    private void setServerError() {
        Toast.makeText(getContext(), "Failed to delete favorite", Toast.LENGTH_LONG).show();
        /*progressBar.setVisibility(View.GONE);
        signUpForm.setVisibility(View.GONE);
        errorTextView.setVisibility(View.VISIBLE);*/

    }


}
