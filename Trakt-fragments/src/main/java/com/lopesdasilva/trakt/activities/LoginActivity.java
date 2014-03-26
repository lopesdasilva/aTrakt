package com.lopesdasilva.trakt.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.Response;
import com.lopesdasilva.trakt.MainActivity;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.TVtraktApp;
import com.lopesdasilva.trakt.Tasks.UserLoginTask;
import com.lopesdasilva.trakt.extras.UserChecker;

/**
 * Created by lopesdasilva on 14/06/13.
 */

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends FragmentActivity implements UserLoginTask.OnLoginUserCompleted {


    public static final String PREFS_NAME = "TVtraktPrefsFile";


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // Values for email and password at the time of the login attempt.
    private String mEmail;
    private String mPassword;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;


    public ServiceManager manager;


    private String mAPIKEY;


    private TVtraktApp appState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appState = ((TVtraktApp) getApplication());
        mAPIKEY = appState.getAPIKEY();


        setContentView(R.layout.activity_login);

        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        if(getIntent().getStringExtra("mUsername")!=null) {
            mEmail = getIntent().getStringExtra("mUsername");
            mPasswordView.requestFocus();
        }

        mEmailView.setText(mEmail);


        mPasswordView
                .setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int id,
                                                  KeyEvent keyEvent) {
                        if (id == R.id.login || id == EditorInfo.IME_NULL) {
                            attemptLogin();
                            return true;
                        }
                        return false;
                    }
                });

        mLoginFormView = findViewById(R.id.login_form);
        mLoginStatusView = findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

        findViewById(R.id.sign_in_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        attemptLogin();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_register:

                startActivity(new Intent(getBaseContext(), RegisterActivity.class));
                overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
                return true;


        }
        return false;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {

        //hide keyboard
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mEmail = mEmailView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (mPassword.length() < 4) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        if (TextUtils.isEmpty(mEmail)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            showProgress(true);
           new UserLoginTask(LoginActivity.this, LoginActivity.this ,mAPIKEY,mEmail,mPassword).execute();
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginStatusView.setVisibility(show ? View.VISIBLE
                                    : View.GONE);
                        }
                    });

            mLoginFormView.setVisibility(View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginFormView.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply movie
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void OnLoginUserCompleted(Response response, Exception e) {


        if (e == null) {
            UserChecker.saveUserAndPassword(LoginActivity.this, mEmail, mPassword);

            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
        } else {
            showProgress(false);
            Log.d("Trakt", "Error on login message: " + e.getMessage());
            mEmailView.setError(getString(R.string.error_incorrect_username_or_password));
            mEmailView.requestFocus();
        }



    }

   /* *//**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     *//*
    public class UserLoginTask_old extends AsyncTask<Void, Void, Response> {
        private Exception e;

        @Override
        protected Response doInBackground(Void... params) {

            Log.d("trakt", "doINBackgournd");

            Log.d("trakt", "username: " + mEmail);
            Log.d("trakt", "password: " + mPassword);

            manager = new ServiceManager();
            manager.setAuthentication(mEmail, mPassword);
            manager.setApiKey(mAPIKEY);
            try {
                return manager.accountService().test().fire();
            } catch (Exception e) {
                this.e = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(final Response success) {

            showProgress(false);
            Log.d("trakt", "This must be null: " + e);

            if (e == null) {

                UserChecker.saveUserAndPassword(LoginActivity.this, mEmail, mPassword);

                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);

            } else {
                mPasswordView
                        .setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }*/
}