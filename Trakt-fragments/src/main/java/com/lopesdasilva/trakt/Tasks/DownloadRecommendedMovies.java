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
public class DownloadRecommendedMovies extends AsyncTask<Void, Void, List<Movie>> {

    private final onRecommendedMovieListTaskComplete listener;
    private final ServiceManager manager;
    private final FragmentActivity activity;
    private Exception e;

    public DownloadRecommendedMovies(onRecommendedMovieListTaskComplete listener, FragmentActivity activity, ServiceManager manager) {
        this.listener = listener;
        this.activity = activity;
        this.manager = manager;

    }


    @Override
    protected List<Movie> doInBackground(Void... voids) {
        try {
            Log.d("Trakt", "recommended movies download started");
            return manager.recommendationsService().movies().fire();
        } catch (Exception e) {
            this.e = e;
            return null;
        }

    }

    @Override
    protected void onPostExecute(final List<Movie> response) {
        if (e == null) {
            Log.d("Trakt", "Recommended movies download completed");
            listener.onRecommendedMovieListTaskComplete(response);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("Error downloading recommended list")
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            new DownloadRecommendedMovies(listener, activity, manager).execute();
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


    public interface onRecommendedMovieListTaskComplete {
        void onRecommendedMovieListTaskComplete(List<Movie> response);
    }
}
