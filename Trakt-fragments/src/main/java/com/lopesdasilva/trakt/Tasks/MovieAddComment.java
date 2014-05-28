package com.lopesdasilva.trakt.Tasks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.Response;

/**
 * Created by lopesdasilva on 18/05/13.
 */
public class MovieAddComment extends AsyncTask<Void, Void, Response> {

    private final FragmentActivity activity;
    private final ServiceManager manager;
    private final Movie movie;
    private final String comment;
    private OnShowAddCommentTaskCompleted listener;


    public MovieAddComment(FragmentActivity activity, OnShowAddCommentTaskCompleted listener, ServiceManager manager, Movie movie, String comment){
        this.listener=listener;
        this.activity=activity;
        this.manager=manager;
        this.movie =movie;
        this.comment=comment;
    }

    private Exception e;

    @Override
    protected Response doInBackground(Void... voids) {

        try {
            Log.d("Trakt Fragments", "Add a comment to movie "+ movie.imdbId);
           return manager.shoutService().movie(movie.title, movie.year).shout(comment).fire();
//            return manager.movieService().summary(movie).fire();
        } catch (Exception e) {
            Log.d("Trakt Fragments", "An Exception was caught");
            this.e = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(final Response response) {

        if (e == null) {
            Log.d("Trakt it", "Comment added" + response.message);
            listener.onShowAddCommentComplete(response);

        } else {
            Log.d("Trakt it", "Failed to add your comment to the movie. Exception Found: " + e.getMessage());


            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("Error  adding your comment to the movie.")
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            new MovieAddComment(activity,listener,manager, movie,comment).execute();
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

    public interface OnShowAddCommentTaskCompleted {
        void onShowAddCommentComplete(Response response);
    }

}
