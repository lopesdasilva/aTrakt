package com.lopesdasilva.trakt.Tasks;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.TvShowSeason;

import java.util.List;

/**
 * Created by lopesdasilva on 18/05/13.
 */
public class DownloadSeasonsInfo extends AsyncTask<Void, Void, List<TvShowSeason>> {

    private final FragmentActivity activity;
    private final ServiceManager manager;
    private final String show;
    private onSeasonsTaskComplete listener;


    public DownloadSeasonsInfo(onSeasonsTaskComplete listener, FragmentActivity activity, ServiceManager manager, String show){
        this.listener=listener;
        this.activity=activity;
        this.manager=manager;
        this.show=show;
    }

    private Exception e;

    @Override
    protected List<TvShowSeason> doInBackground(Void... voids) {

        try {
            Log.d("Trakt Fragments", "Epsiode downloading manager:"+manager);
            return manager.showService().seasons(show).fire();
        } catch (Exception e) {
            Log.d("Trakt Fragments", "An Exception was caught");
            this.e = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(final List<TvShowSeason> response) {

        if (e == null) {
            Log.d("Trakt Fragments", "Download complete Seasons: " + response.size());
            listener.onSeasonsTaskComplete(response);

        } else {
            Log.d("Trakt Fragments", "Failed to download Seasons info. Exception Found: "+e.getClass() +" Message"+e.getMessage());
//           TODO Exception handler
        }
    }

    public interface onSeasonsTaskComplete {
        void onSeasonsTaskComplete(List<TvShowSeason> response);
    }

}
