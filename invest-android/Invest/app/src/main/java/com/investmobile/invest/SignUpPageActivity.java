package com.investmobile.invest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpPageActivity extends AppCompatActivity {

    private final static String APP_AUTH_SHARED_PREFS = "auth_preferences";
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private ProgressBar progressBar;
    private Button signUpButton;
    private LinearLayout signUpForm;
    private TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firstNameEditText = (EditText) findViewById(R.id.first_name_edit_text);
        lastNameEditText = (EditText) findViewById(R.id.last_name_edit_text);
        usernameEditText = (EditText) findViewById(R.id.username_edit_text);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);
        progressBar = (ProgressBar) findViewById(R.id.progressBarSignUp);
        signUpButton = (Button) findViewById(R.id.signup_button_2);
        signUpForm = (LinearLayout) findViewById(R.id.signUpForm);
        errorTextView = (TextView) findViewById(R.id.errorTextView);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formIsValid()) {
                    signUpForm.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    RequestQueue queue = Volley.newRequestQueue(SignUpPageActivity.this);
                    JsonObjectRequest checkUsernameOriginalResponse = new JsonObjectRequest(Request.Method.POST, "http://ec2-52-2-91-221.compute-1.amazonaws.com/api/original", new UsernameResponseListener(), new UsernameErrorHandler()) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> map = new HashMap<String, String>();
                            String username = usernameEditText.getText().toString();
                            map.put("username", username);
                            return super.getParams();
                        }
                    };
                    queue.add(checkUsernameOriginalResponse);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent originalIntent = getIntent();
        boolean cameFromLoginActivity = originalIntent.getBooleanExtra("login", false);
        Intent intent = null;
        if (cameFromLoginActivity) {
            intent = new Intent(this, LoginActivity.class);
        } else {
            intent = new Intent(this, SettingsActivity.class);
        }
        startActivity(intent);
    }

    private boolean formIsValid() {
        boolean success = true;
        if (firstNameEditText.getText().toString().replaceAll("\\s+", "").equals("")) {
            firstNameEditText.setError("First name cannot be empty");
            success = false;
        }

        if (lastNameEditText.getText().toString().replaceAll("\\s+", "").equals("")) {
            lastNameEditText.setError("Last name cannot be empty");
            success = false;
        }

        if (usernameEditText.getText().toString().replaceAll("\\s+", "").equals("")) {
            usernameEditText.setError("Username cannot be empty");
            success = false;
        }

        if (passwordEditText.getText().toString().length() < 8) {
            passwordEditText.setError("Password must have at least 8 characters");
            success = false;
        }

        return success;

    }

    private class UsernameResponseListener implements Response.Listener<JSONObject> {

        @Override
        public void onResponse(JSONObject response) {
            try {
                boolean isValid = response.getBoolean("valid");
                // return value needs to be flipped issue with backend. will fix later
                if (isValid) {
                    RequestQueue queue = Volley.newRequestQueue(SignUpPageActivity.this);
                    StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, "http://ec2-52-2-91-221.compute-1.amazonaws.com/api/login", new MyCustomResponseListener(), new MyCustomErrorListener()) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Log.v("SignUpPage", "TEST");
                            HashMap<String, String> map = new HashMap<>();
                            String firstName = firstNameEditText.getText().toString();
                            String lastName = lastNameEditText.getText().toString();
                            String username = usernameEditText.getText().toString();
                            String password = passwordEditText.getText().toString();
                            map.put("first_name", firstName);
                            map.put("last_name", lastName);
                            map.put("username", username);
                            map.put("password", password);
                            return map;
                        }


                    };
                    queue.add(jsonObjectRequest);
                } else {
                    signUpForm.setVisibility(View.VISIBLE);
                    usernameEditText.setError("Username is already taken");
                    progressBar.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                setServerError();
            }
        }
    }

    private class UsernameErrorHandler implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            setServerError();
        }
    }

    private class MyCustomResponseListener implements Response.Listener<String> {

        @Override
        public void onResponse(String response) {
            progressBar.setVisibility(View.GONE);
            String token = null;
            try {
                SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences(APP_AUTH_SHARED_PREFS, Context.MODE_PRIVATE);
                JSONObject obj = new JSONObject(response);
                token = obj.getString("token");
                sharedPrefs.edit().putString("authToken", token).apply();
                sharedPrefs.edit().putBoolean("userLoggedInState", true).apply();
                Intent intent = new Intent(SignUpPageActivity.this, MainFragment.class);
                startActivity(intent);
            } catch (JSONException e) {
                setServerError();
            }

        }
    }

    private class MyCustomErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            setServerError();
        }
    }

    private void setServerError() {
        progressBar.setVisibility(View.GONE);
        signUpForm.setVisibility(View.GONE);
        errorTextView.setVisibility(View.VISIBLE);
    }
}
