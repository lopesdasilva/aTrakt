package com.lopesdasilva.trakt.Tasks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.Response;

/**
 * Created by lopesdasilva on 30/05/13.
 */
public class RegisterUserTask extends AsyncTask<Void, Void, Response> {

    private final ServiceManager manager;
    private final OnRegisterUserCompleted listener;
    private final FragmentActivity activity;
    private final String email;
    private final String username;
    private final String password;
    private Exception e = null;

    public RegisterUserTask(FragmentActivity activity, OnRegisterUserCompleted listener, ServiceManager manager, String username, String password, String email) {
        this.activity = activity;
        this.manager = manager;
        this.listener = listener;
        this.email = email;
        this.username = username;
        this.password = password;
    }


    @Override
    protected Response doInBackground(Void... voids) {
        try {
            Log.d("Trakt", "Creating account");
            return manager.accountService().create(username, password, email).fire();
        } catch (Exception e) {
            this.e = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(Response response) {
        if (e == null) {

            listener.onRegisterUserComplete(response);

        } else {

            Log.d("Trakt", "Error creating account message: " + e.getMessage());


            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            if (e.getMessage().contains("UnknownHostException"))
                builder.setMessage("Error connecting to trakt");
            else
                builder.setMessage("Error creating account");

            builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    new RegisterUserTask(activity, listener, manager, username, password, email).execute();
                }
            })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(activity, "Cancel", Toast.LENGTH_SHORT).show();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();


        }

    }

    public interface OnRegisterUserCompleted {
        void onRegisterUserComplete(Response response);
    }
}
