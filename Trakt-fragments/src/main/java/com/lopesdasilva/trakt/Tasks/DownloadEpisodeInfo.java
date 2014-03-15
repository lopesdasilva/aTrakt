package com.lopesdasilva.trakt.Tasks;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.TvEntity;

/**
 * Created by lopesdasilva on 18/05/13.
 */
public class DownloadEpisodeInfo extends AsyncTask<Void, Void, TvEntity> {

    private final FragmentActivity activity;
    private final ServiceManager manager;
    private final String show;
    private final int season;
    private final int episode;
    private onEpisodeTaskComplete listener;


    public DownloadEpisodeInfo(onEpisodeTaskComplete listener,FragmentActivity activity,ServiceManager manager, String show, int season, int episode){
        this.listener=listener;
        this.activity=activity;
        this.manager=manager;
        this.show=show;
        this.season=season;
        this.episode=episode;
    }

    private Exception e;

    @Override
    protected TvEntity doInBackground(Void... voids) {

        try {
            Log.d("Trakt Fragments", "Epsiode downloading manager:"+manager);
            return manager.showService().episodeSummary(show, season, episode).fire();
        } catch (Exception e) {
            Log.d("Trakt Fragments", "An Exception was caught");
            this.e = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(final TvEntity response) {

        if (e == null) {
            Log.d("Trakt Fragments", "Download complete" + response.episode.title);
            listener.onDownloadEpisodeInfoComplete(response);

        } else {
            Log.d("Trakt Fragments", "Failed to download episode. Exception Found: "+e.getClass() +" Message"+e.getMessage());
//           TODO Exception handler
        }
    }

    public interface onEpisodeTaskComplete {
        void onDownloadEpisodeInfoComplete(TvEntity response);
    }

}
