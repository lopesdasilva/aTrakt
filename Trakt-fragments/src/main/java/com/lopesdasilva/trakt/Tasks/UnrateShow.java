package com.lopesdasilva.trakt.Tasks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.RatingResponse;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.enumerations.Rating;

/**
 * Created by lopesdasilva on 30/05/13.
 */
public class UnrateShow extends AsyncTask<Void, Void, RatingResponse> {


    private final ServiceManager manager;
    private final OnUnratingShowCompleted listener;
    private final TvShow show_info;
    private final int position;
    private final FragmentActivity activity;
    private Exception e = null;

    public UnrateShow(FragmentActivity activity, OnUnratingShowCompleted listener, ServiceManager manager, TvShow show, int position){
        this.activity=activity;
        this.manager=manager;
        this.show_info=show;
        this.listener=listener;
        this.position=position;
    }




    @Override
    protected RatingResponse doInBackground(Void... voids) {
         try {

                Log.d("Trakt", "Marking "+show_info.tvdbId+" as hated");
                return manager.rateService().show(show_info.title, show_info.year).rating(Rating.Unrate).fire();

        } catch (Exception e) {
            this.e = e;
            return null;
        }
    }
    @Override
    protected void onPostExecute(RatingResponse response) {
        if(e==null){

            listener.OnUnratingShowCompleted(position, response);

        }else{
            Log.d("Trakt","Error unrating the movie");


            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("Error unrating the movie")
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                                new UnrateShow(activity,listener,manager,show_info,position).execute();
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

    public interface OnUnratingShowCompleted {
        void OnUnratingShowCompleted(int position, RatingResponse response);
    }
}
