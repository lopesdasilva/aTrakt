package com.lopesdasilva.trakt.Tasks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.RatingResponse;

/**
 * Created by lopesdasilva on 30/05/13.
 */
public class RateAdvancedMovie extends AsyncTask<Void, Void, RatingResponse> {


    private final ServiceManager manager;
    private final OnRatingAdvancedMovieCompleted listener;
    private final Movie movie;
    private final int position;
    private final FragmentActivity activity;
    private final int rating_advanced;
    private Exception e = null;

    public RateAdvancedMovie(FragmentActivity activity, OnRatingAdvancedMovieCompleted listener, ServiceManager manager, Movie movie, int rating_advanced, int position){
        this.activity=activity;
        this.manager=manager;
        this.movie=movie;
        this.listener=listener;
        this.position=position;
        this.rating_advanced=rating_advanced;
    }




    @Override
    protected RatingResponse doInBackground(Void... voids) {
         try {

                Log.d("Trakt", "Marking "+ movie.imdbId+" as loved");
                return manager.rateService().movie(movie.title, movie.year).advanced_rating(rating_advanced).fire();

        } catch (Exception e) {
            this.e = e;
            return null;
        }
    }
    @Override
    protected void onPostExecute(RatingResponse response) {
        if(e==null){

            listener.OnRatingAdvancedMovieCompleted(position, response);

        }else{
            Log.d("Trakt","Error rating movie as: "+rating_advanced);


            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("Error rating the movie")
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                                new RateAdvancedMovie(activity,listener,manager, movie,rating_advanced,position).execute();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(activity,"Cancel",Toast.LENGTH_SHORT).show();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();


        }

    }

    public interface OnRatingAdvancedMovieCompleted {
        void OnRatingAdvancedMovieCompleted(int position, RatingResponse response);
    }
}
