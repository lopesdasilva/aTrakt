package com.lopesdasilva.trakt.Tasks;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.Movie;

/**
 * Created by lopesdasilva on 18/05/13.
 */
public class DownloadMovieInfo extends AsyncTask<Void, Void, Movie> {

    private final FragmentActivity activity;
    private final ServiceManager manager;
    private final String mMovie;
    private OnMovieTaskCompleted listener;


    public DownloadMovieInfo(OnMovieTaskCompleted listener, FragmentActivity activity, ServiceManager manager, String movie){
        this.listener=listener;
        this.activity=activity;
        this.manager=manager;
        this.mMovie =movie;
    }

    private Exception e;

    @Override
    protected Movie doInBackground(Void... voids) {

        try {
            Log.d("Trakt Fragments", "Movie downloading");

            return manager.movieService().summary(mMovie).fire();
        } catch (Exception e) {
            Log.d("Trakt Fragments", "An Exception was caught");
            this.e = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(final Movie response) {

        if (e == null) {
            Log.d("Trakt Fragments", "Download complete" + response.title);
            listener.onDownloadMovieInfoComplete(response);

        } else {
            Log.d("Trakt Fragments", "Failed to download movie. Exception Found: " + e.getMessage());
//           TODO Exception handler
        }
    }

    public interface OnMovieTaskCompleted{
        void onDownloadMovieInfoComplete(Movie response);
    }

}
