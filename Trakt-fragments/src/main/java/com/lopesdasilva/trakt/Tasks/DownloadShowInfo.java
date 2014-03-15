package com.lopesdasilva.trakt.Tasks;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.TvShow;

/**
 * Created by lopesdasilva on 18/05/13.
 */
public class DownloadShowInfo extends AsyncTask<Void, Void, TvShow> {

    private final FragmentActivity activity;
    private final ServiceManager manager;
    private final String show;
    private onShowInfoTaskComplete listener;


    public DownloadShowInfo(onShowInfoTaskComplete listener, FragmentActivity activity, ServiceManager manager, String show){
        this.listener=listener;
        this.activity=activity;
        this.manager=manager;
        this.show=show;
    }

    private Exception e;

    @Override
    protected TvShow doInBackground(Void... voids) {

        try {
            Log.d("Trakt Fragments", "Epsiode downloading manager:"+manager);
            return manager.showService().summary(show).extended().fire();
        } catch (Exception e) {
            Log.d("Trakt Fragments", "An Exception was caught");
            this.e = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(final TvShow response) {

        if (e == null) {
            Log.d("Trakt Fragments", "Download show complete: " + response.title);
            listener.onShowInfoTaskComplete(response);

        } else {
            Log.d("Trakt Fragments", "Failed to download Seasons info. Exception Found: "+e.getClass() +" Message"+e.getMessage());
//           TODO Exception handler
        }
    }

    public interface onShowInfoTaskComplete {
        void onShowInfoTaskComplete(TvShow response);
    }

}
