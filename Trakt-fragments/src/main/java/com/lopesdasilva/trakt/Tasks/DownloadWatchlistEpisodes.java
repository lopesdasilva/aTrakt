package com.lopesdasilva.trakt.Tasks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.TvShow;

import java.util.List;

/**
 * Created by lopesdasilva on 04/06/13.
 */
public class DownloadWatchlistEpisodes extends AsyncTask<Void, Void, List<TvShow>> {

    private final onWatchlistEpisodesListTaskComplete listener;
    private final ServiceManager manager;
    private final FragmentActivity activity;
    private Exception e;

    public DownloadWatchlistEpisodes(onWatchlistEpisodesListTaskComplete listener, FragmentActivity activity, ServiceManager manager){
    this.listener=listener;
        this.activity=activity;
        this.manager=manager;

    }


    @Override
    protected List<TvShow> doInBackground(Void... voids) {
        try{
            Log.d("Trakt", "Trending shows download started");
            return manager.userService().watchlistEpisodes(manager.username).fire();
        }catch(Exception e){
            this.e=e;
            return null;
        }

    }

    @Override
    protected void onPostExecute(final List<TvShow> response) {
    if(e==null){
        Log.d("Trakt","Watchlist shows download completed");
        listener.onWatchlistEpisodesListTaskComplete(response);
    }else{
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Error downloading watchlist list")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new DownloadWatchlistEpisodes(listener,activity,manager).execute();
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


        public interface onWatchlistEpisodesListTaskComplete {
        void onWatchlistEpisodesListTaskComplete(List<TvShow> response);
    }
}
