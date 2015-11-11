package com.investmobile.invest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity /*implements LoaderCallbacks<Cursor> */{

    private final static String APP_AUTH_SHARED_PREFS = "auth_preferences";

    // UI references.
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mEmailLoginFormView;
    private View mSignOutButtons;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences(APP_AUTH_SHARED_PREFS, Context.MODE_PRIVATE);
        boolean isUserLoggedIn = sharedPrefs.getBoolean("userLoggedInState", false);
        if (isUserLoggedIn) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        // Set up the login form.
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.email);
//        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });


        Button mUsernameLoginButton = (Button) findViewById(R.id.username_login_in_button);
        mUsernameLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button signUpButton = (Button) findViewById(R.id.signup_button);
        signUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpPageActivity.class);
                intent.putExtra("login", true);
                startActivity(intent);
            }
        });
        
        Button skipButton = (Button) findViewById(R.id.skip_button);
        skipButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences(APP_AUTH_SHARED_PREFS, Context.MODE_PRIVATE);
                sharedPrefs.edit().putBoolean("userSkippedLoggedInState", true).apply();
                startActivity(intent);
            }
        });


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mEmailLoginFormView = findViewById(R.id.email_login_form);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("ec2-52-2-91-221.compute-1.amazonaws.com/api/reauth", null, new MyCustomResponseListener(), new MyCustomErrorListener());
            queue.add(jsonObjectRequest);
        }
    }


    private boolean isPasswordValid(String password) {
        return password.length() >= 8;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void skipLogin() {
    }

    private class MyCustomResponseListener implements Response.Listener<JSONObject>{

        @Override
        public void onResponse(JSONObject response) {
            mProgressView.setVisibility(View.GONE);
            String token = null;
            try {
                SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences(APP_AUTH_SHARED_PREFS, Context.MODE_PRIVATE);
                token = response.getString("token");
                sharedPrefs.edit().putString("authToken", token).apply();
                sharedPrefs.edit().putBoolean("userLoggedInState", true).apply();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            } catch (JSONException e) {
                TextView errorTextView = (TextView) findViewById(R.id.error);
                errorTextView.setVisibility(View.VISIBLE);
            }

        }
    }

    private class MyCustomErrorListener implements Response.ErrorListener{

        @Override
        public void onErrorResponse(VolleyError error) {
            mProgressView.setVisibility(View.GONE);
            TextView errorTextView = (TextView) findViewById(R.id.error);
            errorTextView.setVisibility(View.VISIBLE);
        }
    }
}

