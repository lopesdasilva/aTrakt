package com.lopesdasilva.trakt.Tasks;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.DismissResponse;
import com.jakewharton.trakt.entities.Movie;

public class DismissRecomendationMovie extends AsyncTask<Void, Void, DismissResponse> {

    private final FragmentActivity activity;
    private final ServiceManager manager;
    private final Movie dismiss_movie;
    private final onDismissedMovieTaskComplete listener;
    private final int position;
    private Exception e;

    public DismissRecomendationMovie(onDismissedMovieTaskComplete listener, FragmentActivity activity, ServiceManager manager, Movie dismiss_movie, int position) {
        this.listener=listener;
        this.activity=activity;
        this.manager=manager;
        this.dismiss_movie =dismiss_movie;
        this.position=position;
    }

    @Override
    protected DismissResponse doInBackground(Void... voids) {
        try{
        return manager.recommendationsService().dismissMovie(dismiss_movie.imdbId).fire();
        }catch (Exception e){
            this.e=e;
            return null;
        }
    }


    @Override
    protected void onPostExecute(DismissResponse response) {
        if(e==null){
            listener.onDismissedMovieTaskComplete(response, position);
        }else{
            Log.d("Trakt","Error Dismissing: "+ dismiss_movie.title);
        }

    }

    public interface onDismissedMovieTaskComplete {
        void onDismissedMovieTaskComplete(DismissResponse response, int position);
    }

}