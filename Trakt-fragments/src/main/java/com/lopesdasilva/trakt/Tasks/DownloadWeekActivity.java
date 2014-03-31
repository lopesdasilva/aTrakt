package com.lopesdasilva.trakt.Tasks;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.Activity;
import com.jakewharton.trakt.enumerations.ActivityType;

import java.util.Date;

/**
 * Created by lopesdasilva on 27/05/13.
 */
public class DownloadWeekActivity extends AsyncTask<Void, Void, Activity> {

    private final OnWeekActivityTaskCompleted listener;
    private final FragmentActivity activity;
    private final Date date;
    private final String username;
    public ServiceManager manager;
    private Exception e;

    public DownloadWeekActivity(OnWeekActivityTaskCompleted listener, FragmentActivity activity, ServiceManager manager,String username , Date date) {
        this.activity = activity;
        this.listener = listener;
        this.manager = manager;
        this.date = date;
        this.username=username;
    }


    @Override
    protected Activity doInBackground(Void... voids) {
        try {
            return manager.activityService().user(username).types(ActivityType.Episode,ActivityType.Movie,ActivityType.Show).images().min(true).timestamp(date).fire();
        } catch (Exception e) {
            this.e = e;
            return null;
        }

    }


    @Override
    protected void onPostExecute(final Activity response) {
        if (e == null) {
            listener.onWeekActivityInfoComplete(response);
        } else {
            Toast.makeText(activity, "Error downloading e: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public interface OnWeekActivityTaskCompleted {
        void onWeekActivityInfoComplete(Activity response);
    }
}
