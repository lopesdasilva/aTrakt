package com.lopesdasilva.trakt.Tasks;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;

import java.util.Date;

/**
 * Created by lopesdasilva on 05/07/13.
 */
public class MarkMovieSeenUnseen extends AsyncTask<Void, Void, Void> {

    private final FragmentActivity activity;
    private final ServiceManager manager;
    private final Movie movie;
    private final OnMovieMarkSeenUnseenCompleted listener;
    private final int position;

    public MarkMovieSeenUnseen(FragmentActivity activity, OnMovieMarkSeenUnseenCompleted listener, ServiceManager manager, Movie movie, int position){
        this.activity=activity;
        this.manager=manager;
        this.movie=movie;
        this.listener=listener;
        this.position=position;
    }


    private Exception e;

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            if (movie.watched) {
                Log.d("Trakt Fragments", "Changing to unseen");
                return manager.movieService().unseen().movie(movie.imdbId).fire();
            } else {
                Log.d("Trakt Fragments", "Changing to seen");
                return manager.movieService().seen().movie(movie.imdbId, 1, new Date()).fire();
            }
        } catch (Exception e) {
            this.e = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(Void voids) {
        if (e == null) {
            Log.d("Trakt Fragments", "Updating seen status ui");
           listener.OnMovieMarkSeenUnseenCompleted(position);
        } else {
            Log.d("Trakt Fragments", "Error marking episode as unseen: " + e.getMessage());

        }
    }

    public interface OnMovieMarkSeenUnseenCompleted {
        void OnMovieMarkSeenUnseenCompleted(int position);
    }
}
