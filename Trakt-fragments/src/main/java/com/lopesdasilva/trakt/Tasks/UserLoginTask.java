package com.lopesdasilva.trakt.Tasks;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.Response;

/**
 * Created by NB20308 on 26-03-2014.
 */
public class UserLoginTask extends AsyncTask<Void, Void, Response> {
    private final FragmentActivity activity;
    private ServiceManager manager;
    private String mAPIKEY;
    private final OnLoginUserCompleted listener;
    private final String username;
    private final String password;
    private Exception e;

    public UserLoginTask(FragmentActivity activity, OnLoginUserCompleted listener, String mAPIKEY, String username, String password) {
        this.activity = activity;
        this.mAPIKEY = mAPIKEY;
        this.listener = listener;
        this.username = username;
        this.password = password;
    }


    @Override
    protected Response doInBackground(Void... params) {

        Log.d("trakt", "doINBackgournd");

        Log.d("trakt", "username: " + username);
       // Log.d("trakt", "password: " + password);

        manager = new ServiceManager();
        manager.setAuthentication(username, password);
        manager.setApiKey(mAPIKEY);
        try {
            return manager.accountService().test().fire();
        } catch (Exception e) {
            this.e = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(final Response response) {

        Log.d("trakt", "This must be null: " + e);



            listener.OnLoginUserCompleted(response,e);

    }


    public interface OnLoginUserCompleted {
        void OnLoginUserCompleted(Response response,Exception e);
    }
}