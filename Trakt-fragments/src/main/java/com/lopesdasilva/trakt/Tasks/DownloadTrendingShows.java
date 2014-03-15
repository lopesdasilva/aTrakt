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
public class DownloadTrendingShows extends AsyncTask<Void, Void, List<TvShow>> {

    private final onTrendingShowListTaskComplete listener;
    private final ServiceManager manager;
    private final FragmentActivity activity;
    private Exception e;

    public DownloadTrendingShows(onTrendingShowListTaskComplete  listener, FragmentActivity activity, ServiceManager manager){
    this.listener=listener;
        this.activity=activity;
        this.manager=manager;

    }


    @Override
    protected List<TvShow> doInBackground(Void... voids) {
        try{
            Log.d("Trakt", "Trending shows download started");
            return manager.showService().trending().fire();
        }catch(Exception e){
            this.e=e;
            return null;
        }

    }

    @Override
    protected void onPostExecute(final List<TvShow> response) {
    if(e==null){
        Log.d("Trakt","Trending shows download completed");
        listener.onTrendingShowListTaskComplete(response);
    }else{
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Error downloading trending list")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new DownloadTrendingShows(listener,activity,manager).execute();
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


        public interface onTrendingShowListTaskComplete {
        void onTrendingShowListTaskComplete(List<TvShow> response);
    }
}
