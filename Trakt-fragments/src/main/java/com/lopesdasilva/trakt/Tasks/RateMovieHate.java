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
import com.jakewharton.trakt.enumerations.Rating;

/**
 * Created by lopesdasilva on 30/05/13.
 */
public class RateMovieHate extends AsyncTask<Void, Void, RatingResponse> {


    private final ServiceManager manager;
    private final OnRatingMovieHateCompleted listener;
    private final Movie movie_info;
    private final int position;
    private final FragmentActivity activity;
    private Exception e = null;

    public RateMovieHate(FragmentActivity activity, OnRatingMovieHateCompleted listener, ServiceManager manager, Movie movie, int position){
        this.activity=activity;
        this.manager=manager;
        this.movie_info =movie;
        this.listener=listener;
        this.position=position;
    }




    @Override
    protected RatingResponse doInBackground(Void... voids) {
         try {

                Log.d("Trakt", "Marking "+ movie_info.imdbId+" as hated");
                return manager.rateService().movie(movie_info.title, movie_info.year).rating(Rating.Hate).fire();

        } catch (Exception e) {
            this.e = e;
            return null;
        }
    }
    @Override
    protected void onPostExecute(RatingResponse response) {
        if(e==null){

            listener.OnRatingMovieHateCompleted(position, response);

        }else{
            Log.d("Trakt","Error rating movie as hated");


            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("Error rating the movie as hated")
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                                new RateMovieHate(activity,listener,manager, movie_info,position).execute();
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

    public interface OnRatingMovieHateCompleted {
        void OnRatingMovieHateCompleted(int position, RatingResponse response);
    }
}
