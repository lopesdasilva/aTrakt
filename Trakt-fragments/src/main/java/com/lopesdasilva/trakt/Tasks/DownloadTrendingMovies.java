package com.lopesdasilva.trakt.Tasks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.Movie;

import java.util.List;

/**
 * Created by lopesdasilva on 04/06/13.
 */
public class DownloadTrendingMovies extends AsyncTask<Void, Void, List<Movie>> {

    private final onTrendingMoviesListTaskComplete listener;
    private final ServiceManager manager;
    private final FragmentActivity activity;
    private Exception e;

    public DownloadTrendingMovies(onTrendingMoviesListTaskComplete listener, FragmentActivity activity, ServiceManager manager){
    this.listener=listener;
        this.activity=activity;
        this.manager=manager;

    }


    @Override
    protected List<Movie> doInBackground(Void... voids) {
        try{

            Log.d("Trakt", "Trending movies download started");
            return manager.movieService().trending().fire();
        }catch(Exception e){
            this.e=e;
            return null;
        }

    }

    @Override
    protected void onPostExecute(final List<Movie> response) {
    if(e==null){
        Log.d("Trakt", "Trending movies download completed");
        listener.onTrendingMoviesListTaskComplete(response);
    }else{
        Log.d("Trakt", "error: "+e.getMessage()+"cause" +e.getCause());
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Error downloading trending list")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new DownloadTrendingMovies(listener,activity,manager).execute();
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


        public interface onTrendingMoviesListTaskComplete {
        void onTrendingMoviesListTaskComplete(List<Movie> response);
    }
}
