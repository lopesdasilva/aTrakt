package com.lopesdasilva.trakt.broadcasts;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.Response;
import com.jakewharton.trakt.enumerations.MediaType;
import com.lopesdasilva.trakt.Tasks.CancelCheckIn;

/**
 * Created by lopesdasilva on 19/05/13.
 */
public class CheckInReceiver extends BroadcastReceiver implements CancelCheckIn.OnCheckInCanceled {
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context=context;

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();

        ServiceManager  manager = new ServiceManager();
        manager.setApiKey("633c56872d241ed0c097829678ea9fbcd15df3c4");
        manager.setAuthentication("lopesdasilva", "88f94d1bb6ceff3a063388a66a15a8380777338d");

        Bundle extras=intent.getExtras();

        String show=extras.getString("show_imdb");
        if(show!=null){
            Log.d("Trakt Fragments","Canceling show");
            new CancelCheckIn(this,manager, MediaType.TvShow).execute();
        }
        else{
            String movie=extras.getString("movie_imdb");
        if(movie!=null){
            Log.d("Trakt Fragments","Canceling movie");
            new CancelCheckIn(this,manager, MediaType.Movie).execute();
        }
        }

    }

    @Override
    public void OnCheckInCanceled(Response response) {
        Toast.makeText(context,"Checkin Canceled",Toast.LENGTH_SHORT).show();
    }
}
