package com.lopesdasilva.trakt.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.Response;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.TVtraktApp;
import com.lopesdasilva.trakt.Tasks.RegisterUserTask;
import com.lopesdasilva.trakt.activities.LoginActivity;
import com.lopesdasilva.trakt.extras.HashText;

import java.security.NoSuchAlgorithmException;

/**
 * Created by NB20308 on 25-03-2014.
 */
public class RegisterFragment extends Fragment implements RegisterUserTask.OnRegisterUserCompleted {


    private View rootView;
    private ServiceManager manager;
    private TVtraktApp appState;
    private String mAPIKEY;
    private EditText mPasswordView;
    private EditText mUsernameView;
    private EditText mRetypePasswordView;
    private EditText mEmailView;
    private AQuery aq;
    private String username;

    public RegisterFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.register_fragment, container, false);


        appState = ((TVtraktApp) getActivity().getApplication());
        mAPIKEY = appState.getAPIKEY();

        manager = new ServiceManager();
        manager.setApiKey(mAPIKEY);

        mUsernameView = (EditText) rootView.findViewById(R.id.editTextRegisterUsername);
        mPasswordView = (EditText) rootView.findViewById(R.id.editTextRegisterPassword);
        mRetypePasswordView = (EditText) rootView.findViewById(R.id.editTextRegisterRetypePassword);
        mEmailView = (EditText) rootView.findViewById(R.id.editTextRegisterEmail);


        mRetypePasswordView
                .setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int id,
                                                  KeyEvent keyEvent) {
                        if (id == R.id.login || id == EditorInfo.IME_NULL) {
                            attemptRegister();
                            return true;
                        }
                        return false;
                    }
                });


        aq = new AQuery(rootView);

        aq.id(R.id.buttonRegister).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });


        return rootView;

    }

    private void attemptRegister() {

        View focusView = null;
        //to clear errors
        mUsernameView.setError(null);
        mPasswordView.setError(null);
        mRetypePasswordView.setError(null);
        mEmailView.setError(null);

        boolean all_ok = true;
        username = mUsernameView.getText().toString();
        final String password = mPasswordView.getText().toString();
        String password_sha1 = "";
        try {
            password_sha1 = HashText.sha1(mPasswordView.getText().toString());
        } catch (NoSuchAlgorithmException e) {
            Log.d("Trakt it", "Eror creating sha1 hash");
            Toast.makeText(getActivity(), "Error creating hash", Toast.LENGTH_SHORT).show();
            all_ok = false;
        }

        final String password_retype = mRetypePasswordView.getText().toString();
        final String email = mEmailView.getText().toString();




        if (TextUtils.isEmpty(password_retype)) {
            aq.id(R.id.editTextRegisterRetypePassword).getEditText().setError(getResources().getString(R.string.error_field_required));
            all_ok = false;
            focusView = mRetypePasswordView;
        }

        if (!TextUtils.equals(password_retype, password)) {
            aq.id(R.id.editTextRegisterPassword).getEditText().setError(getResources().getString(R.string.error_missmatch_password));
            aq.id(R.id.editTextRegisterRetypePassword).getEditText().setError(getResources().getString(R.string.error_missmatch_password));
            all_ok = false;
            focusView = mPasswordView;
        }

        if (TextUtils.isEmpty(password)) {
            aq.id(R.id.editTextRegisterPassword).getEditText().setError(getResources().getString(R.string.error_field_required));
            all_ok = false;
            focusView = mPasswordView;
        }

        if (password.length() < 5) {
            aq.id(R.id.editTextRegisterPassword).getEditText().setError(getResources().getString(R.string.error_password_short));
            all_ok = false;
            focusView = mPasswordView;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            aq.id(R.id.editTextRegisterEmail).getEditText().setError(getResources().getString(R.string.error_invalid_email));
            all_ok = false;
            focusView = mEmailView;
        }

        if (TextUtils.isEmpty(email)) {
            aq.id(R.id.editTextRegisterEmail).getEditText().setError(getResources().getString(R.string.error_field_required));
            all_ok = false;
            focusView = mEmailView;
        }

        if (TextUtils.isEmpty(username)) {
            aq.id(R.id.editTextRegisterUsername).getEditText().setError(getResources().getString(R.string.error_field_required));
            all_ok = false;
            focusView = mUsernameView;
        }


        if (all_ok) {
            //hide keyboard
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


            aq.id(R.id.relativeLayoutRegisterLoading).visible();
            aq.id(R.id.scrollViewRegisterForm).invisible();
            new RegisterUserTask(getActivity(), RegisterFragment.this, manager, username, password_sha1, email).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        } else {
            focusView.requestFocus();
        }
    }

    @Override
    public void onRegisterUserComplete(Response response) {
        if (response.error == null) {

            Log.d("Trakt it", "message: " + response.message);
            Log.d("Trakt it", "status: " + response.status);
            Log.d("Trakt it", "error: " + response.error);



            Intent loginIntent = new Intent(getActivity().getBaseContext(), LoginActivity.class);
            loginIntent.putExtra("mUsername", username);
            startActivity(loginIntent);
            getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        } else {
            aq.id(R.id.relativeLayoutRegisterLoading).invisible();
            aq.id(R.id.scrollViewRegisterForm).visible();

            if (response.error.contains("already a registered username")) {
                mUsernameView.requestFocus();
                mUsernameView.setError(getResources().getString(R.string.error_exists_username));
            } else
                Toast.makeText(getActivity(), "Error " + response.message, Toast.LENGTH_SHORT).show();
        }

    }
}
