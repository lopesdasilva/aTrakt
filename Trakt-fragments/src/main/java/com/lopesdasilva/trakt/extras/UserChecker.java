package com.lopesdasilva.trakt.extras;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.jakewharton.trakt.ServiceManager;
import com.lopesdasilva.trakt.TVtraktApp;
import com.lopesdasilva.trakt.activities.LoginActivity;

/**
 * Created by lopesdasilva on 21/05/13.
 */
public class UserChecker {


    private static TVtraktApp appState;
    private static ServiceManager manager;
    public static final String PREFS_NAME = "TraktPrefsFile";

    public static ServiceManager checkUserLogin(FragmentActivity fragmentActivity) {
        appState = ((TVtraktApp) fragmentActivity.getApplication());



        manager=appState.getManager();
        if(manager==null){
            SharedPreferences settings = fragmentActivity.getSharedPreferences(PREFS_NAME, 0);
            String mUsername=settings.getString("username", null);
            String mPassword_SHA1=settings.getString("password_sha1", null);

            if(mUsername==null || mPassword_SHA1==null){
              //TODO: launch login SCREEN
                Log.d("Trakt", "Not Logged implement login, finishing now");
                fragmentActivity.finish();
                fragmentActivity.startActivity(new Intent(fragmentActivity.getBaseContext(), LoginActivity.class));
                return null;
            }else{
                manager=new ServiceManager();
//                manager.setUseSsl(true);
                manager.setApiKey(appState.getAPIKEY());
                manager.setAuthentication(mUsername,mPassword_SHA1);
            }
        }else if(!manager.readyWithUser()){

            SharedPreferences settings = fragmentActivity.getSharedPreferences(PREFS_NAME, 0);
            String mUsername=settings.getString("username", null);
            String mPassword_SHA1=settings.getString("password_sha1", null);

            manager.setApiKey(appState.getAPIKEY());
            manager.setAuthentication(mUsername,mPassword_SHA1);
//            manager.setUseSsl(true);
        }
        return manager;
    }

    public static ServiceManager checkUserLogin(Context context) {
        appState = ((TVtraktApp) context.getApplicationContext());



        manager=appState.getManager();
        if(manager==null){
            SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
            String mUsername=settings.getString("username", null);
            String mPassword_SHA1=settings.getString("password_sha1", null);

            if(mUsername==null || mPassword_SHA1==null){
                //TODO: launch login SCREEN
                Log.d("Trakt","Not Logged implement login, finishing now");

            }else{
                manager=new ServiceManager();
//                manager.setUseSsl(true);
                manager.setApiKey(appState.getAPIKEY());
                manager.setAuthentication(mUsername,mPassword_SHA1);
            }
        }if(!manager.readyWithUser()){

            SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
            String mUsername=settings.getString("username", null);
            String mPassword_SHA1=settings.getString("password_sha1", null);

            manager.setApiKey(appState.getAPIKEY());
//            manager.setUseSsl(true);
            manager.setAuthentication(mUsername,mPassword_SHA1);
        }
        return manager;
    }


    public static String getUsername(FragmentActivity fragmentActivity) {
        SharedPreferences settings = fragmentActivity.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString("username", null);

    }
    public static void saveUserAndPassword(FragmentActivity fragmentActivity,String mUsername, String mPassword_sha1){
        ServiceManager manager = new ServiceManager();

        appState = ((TVtraktApp) fragmentActivity.getApplication());
        appState.setManager(manager);

        SharedPreferences settings = fragmentActivity.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("username", mUsername);
        editor.putString("password_sha1", mPassword_sha1);
        editor.commit();


    }
}
