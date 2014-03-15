package com.lopesdasilva.trakt.Tasks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;

/**
 * Created by lopesdasilva on 30/05/13.
 */
public class MarkEpisodeWatchlistUnWatchlist extends AsyncTask<Void, Void, Void> {

    private final FragmentActivity activity;
    private final ServiceManager manager;
    private final TvShow show;
    private final TvShowEpisode episode;
    private final int position;
    private final WatchlistUnWatchlistCompleted listener;
    private Exception e;

    public MarkEpisodeWatchlistUnWatchlist(FragmentActivity activity,WatchlistUnWatchlistCompleted listener, ServiceManager manager,TvShow show, TvShowEpisode episode,int position){
        this.activity=activity;
        this.listener=listener;
        this.manager=manager;
        this.show=show;
        this.episode=episode;
        this.position=position;
    }


    @Override
    protected Void doInBackground(Void... voids) {

        try {
            if (episode.inWatchlist) {
                Log.d("Trakt Fragments", "Adding to Unwatchlist");
                return manager.showService().episodeUnwatchlist(show.imdbId).episode(episode.season, episode.number).fire();
            } else {
                Log.d("Trakt Fragments", "Adding to Watchlist");
                return manager.showService().episodeWatchlist(show.imdbId).episode(episode.season, episode.number).fire();
            }
        } catch (Exception e) {
            this.e = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(Void response) {
        if(e==null){
            listener.WatchlistUnWatchlistCompleted(position);

        }else{
            Log.d("Trakt","Erro add watchlist/unwatchlist");


            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("Error updating episode watchlist")
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            new MarkEpisodeWatchlistUnWatchlist(activity,listener,manager,show,episode,position).execute();
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

    public interface WatchlistUnWatchlistCompleted {
        void WatchlistUnWatchlistCompleted(int position);
    }
}
