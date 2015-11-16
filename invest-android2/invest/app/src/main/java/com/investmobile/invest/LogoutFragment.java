package com.investmobile.invest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LogoutFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LogoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogoutFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private final String APP_AUTH_SHARED_PREFS = "auth_preferences";
    ProgressBar progressBar;
    Button button;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LogoutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LogoutFragment newInstance() {
        LogoutFragment fragment = new LogoutFragment();

        return fragment;
    }

    public LogoutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button logoutButton = (Button) view.findViewById(R.id.logoutButton);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBarLogout);
        progressBar.setVisibility(View.GONE);
        button = (Button) getActivity().findViewById(R.id.logoutButton);
        button.setVisibility(View.VISIBLE);
        logoutButton.setOnClickListener(new LogoutButtonOnClickListener());
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    private class LogoutButtonOnClickListener implements View.OnClickListener {
        private String url = "http://ec2-52-2-91-221.compute-1.amazonaws.com/api/logout";

        @Override
        public void onClick(View v) {
//            progressBar.setVisibility(View.VISIBLE);
//            Button button = (Button) v.findViewById(R.id.logoutButton);
//            button.setVisibility(View.GONE);
//            RequestQueue queue = Volley.newRequestQueue(getContext());
//            StringRequest request = new StringRequest(Request.Method.POST, url, new LogoutResponseListener(), new LogoutResponseErrorHandler()) {
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    HashMap<String, String> map = new HashMap<>();
//                    SharedPreferences sharedPrefs = getActivity().getSharedPreferences(APP_AUTH_SHARED_PREFS, Context.MODE_PRIVATE);
//                    String token = sharedPrefs.getString("authToken", null);
//                    map.put("x-auth-token", token);
//                    return map;
//                }
//            };
//            queue.add(request);
            Context context = getContext();
            CharSequence text = "Logged out successfully";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            SharedPreferences sharedPrefs = getContext().getSharedPreferences(APP_AUTH_SHARED_PREFS, Context.MODE_PRIVATE);
            sharedPrefs.edit().putString("authToken", null).commit();
            sharedPrefs.edit().putBoolean("userLoggedInState", false).commit();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

//    private class LogoutResponseListener implements Response.Listener<String> {
//
//        @Override
//        public void onResponse(String response) {

//        }
//    }

//    private class LogoutResponseErrorHandler implements Response.ErrorListener {
//        @Override
//        public void onErrorResponse(VolleyError error) {
//            Context context = getContext();
//            progressBar.setVisibility(View.GONE);
//            button.setVisibility(View.VISIBLE);
//            CharSequence text = "Opps. We're having some technical difficulites. Try again later";
//            int duration = Toast.LENGTH_SHORT;
//            Toast toast = Toast.makeText(context, text, duration);
//            toast.show();
//        }
//    }

}
