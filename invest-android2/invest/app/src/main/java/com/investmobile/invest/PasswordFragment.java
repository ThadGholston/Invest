package com.investmobile.invest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PasswordFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PasswordFragment extends Fragment {
    private final static String APP_AUTH_SHARED_PREFS = "auth_preferences";
    EditText currentPassword;
    EditText passwordField1;
    EditText passwordField2;
    Button changePasswordButton;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PasswordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PasswordFragment newInstance() {
        PasswordFragment fragment = new PasswordFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_password, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initComponents(view);
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

    private void initComponents(View view) {
        passwordField1 = (EditText) view.findViewById(R.id.passwordField1);
        passwordField2 = (EditText) view.findViewById(R.id.passwordField2);
        currentPassword = (EditText) view.findViewById(R.id.currentPassword);
        changePasswordButton = (Button) view.findViewById(R.id.changePasswordButton);

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPassword();
            }
        });
    }

    private boolean passwordValidates() {
        System.out.println("********PASSWORDS********");
        System.out.println(passwordField1.getText());
        System.out.println(passwordField2.getText());
        if (passwordField1.getText().toString().equals(passwordField2.getText().toString())) {
            return true;
        } else {
            return false;
        }
    }

    private void submitPassword() {
        if (passwordValidates()) {
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, "http://ec2-52-2-91-221.compute-1.amazonaws.com/api/account", new MyCustomResponseListener(), new MyCustomErrorListener()) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
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
                    SharedPreferences sharedPrefs = getContext().getApplicationContext().getSharedPreferences(APP_AUTH_SHARED_PREFS, Context.MODE_PRIVATE);
                    String token = sharedPrefs.getString("authToken", null);
                    map.put("x-auth-token", token);
                    return map;
                }
            };

            queue.add(jsonObjectRequest);

        } else {
            Toast.makeText(getContext().getApplicationContext(), "Password mismatch", Toast.LENGTH_LONG).show();
        }
    }


    private class MyCustomResponseListener implements Response.Listener<String> {

        @Override
        public void onResponse(String response) {
            //progressBar.setVisibility(View.GONE);
            String token = null;
            try {
                SharedPreferences sharedPrefs = getContext().getApplicationContext().getSharedPreferences(APP_AUTH_SHARED_PREFS, Context.MODE_PRIVATE);
                JSONObject obj = new JSONObject(response);
                token = obj.getString("token");
                Log.v("INVEST", token);
                sharedPrefs.edit().putString("authToken", token).apply();
                sharedPrefs.edit().putBoolean("userLoggedInState", true).apply();
                Toast.makeText(getContext().getApplicationContext(), "Password changed successfully", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            } catch (JSONException e) {
                setServerError("Password change failed");
            }

        }
    }

    private class MyCustomErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.networkResponse.statusCode == 401) {
                setServerError("Invalid password");
            } else {
                setServerError("Password change failed");
            }
        }
    }

    private void setServerError(String msg) {

        Toast.makeText(getContext().getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        /*progressBar.setVisibility(View.GONE);
        signUpForm.setVisibility(View.GONE);
        errorTextView.setVisibility(View.VISIBLE);*/
    }

}
