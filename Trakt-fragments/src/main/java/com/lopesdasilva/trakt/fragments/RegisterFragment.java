package com.lopesdasilva.trakt.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.Response;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.TVtraktApp;
import com.lopesdasilva.trakt.Tasks.RegisterUserTask;
import com.lopesdasilva.trakt.activities.LoginActivity;

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

    public RegisterFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.register_fragment, container, false);

        appState = ((TVtraktApp) getActivity().getApplication());
        mAPIKEY = appState.getAPIKEY();

        manager = new ServiceManager();
        manager.setApiKey(mAPIKEY);




       final AQuery aq= new AQuery(rootView);
        aq.id(R.id.buttonRegister);




        aq.clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mUsernameView = (EditText) rootView.findViewById(R.id.editTextRegisterUsername);
                mPasswordView = (EditText) rootView.findViewById(R.id.editTextRegisterPassword);
                mRetypePasswordView = (EditText) rootView.findViewById(R.id.editTextRegisterRetypePassword);
                mEmailView = (EditText) rootView.findViewById(R.id.editTextRegisterEmail);

               //to clear errors
                mUsernameView.setError(null);
                mPasswordView.setError(null);
                mRetypePasswordView.setError(null);
                mEmailView.setError(null);


                final String username=mUsernameView.getText().toString();
                final String password=mPasswordView.getText().toString();
                final String password_retype=mRetypePasswordView.getText().toString();
                final String email=mEmailView.getText().toString();

                boolean all_ok = true;

                Log.d("Trakt it","username: "+username+" password: "+password+" email: "+email);

                if (TextUtils.isEmpty(email)) {
                     aq.id(R.id.editTextRegisterEmail).getEditText().setError(getResources().getString(R.string.error_field_required));
                    all_ok = false;
                }

                if (TextUtils.isEmpty(username)) {
                    aq.id(R.id.editTextRegisterUsername).getEditText().setError(getResources().getString(R.string.error_field_required));
                    all_ok = false;
                }


                if (TextUtils.isEmpty(password)) {
                   aq.id(R.id.editTextRegisterPassword).getEditText().setError(getResources().getString(R.string.error_field_required));
                    all_ok = false;
                }

                if (TextUtils.isEmpty(password)) {
                   aq.id(R.id.editTextRegisterRetypePassword).getEditText().setError(getResources().getString(R.string.error_field_required));
                    all_ok = false;
                }

                if (!TextUtils.equals(password_retype,password)) {
                     aq.id(R.id.editTextRegisterPassword).getEditText().setError(getResources().getString(R.string.error_missmatch_password));
                     aq.id(R.id.editTextRegisterRetypePassword).getEditText().setError(getResources().getString(R.string.error_missmatch_password));
                    all_ok = false;
                }

                if (all_ok) {
                    // String username = aq.id(R.id.editTextRegisterUsername).getText().toString();
                    //String password = aq.id(R.id.editTextRegisterPassword).getText().toString();
                    //String email =  aq.id(R.id.editTextRegisterEmail).getText().toString();

                    new RegisterUserTask(getActivity(), RegisterFragment.this, manager, username, password, email).execute();
                }


            }
        });


        return rootView;

    }

    @Override
    public void onRegisterUserComplete(Response response) {
        Log.d("Trakt it", "message: "+response.message);
        Log.d("Trakt it", "status: "+response.status);
        Log.d("Trakt it", "error: "+response.error);
        Toast.makeText(getActivity().getApplicationContext(), "User created: ", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getActivity().getBaseContext(), LoginActivity.class));
        getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
}
