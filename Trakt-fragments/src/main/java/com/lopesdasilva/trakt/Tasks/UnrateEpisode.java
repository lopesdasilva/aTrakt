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
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.enumerations.Rating;

/**
 * Created by lopesdasilva on 30/05/13.
 */
public class UnrateEpisode extends AsyncTask<Void, Void, RatingResponse> {

    private final TvShowEpisode episode_info;
    private final ServiceManager manager;
    private final OnMarkEpisodeNoneCompleted listener;
    private final TvShow show_info;
    private final int position;
    private final FragmentActivity activity;
    private Exception e = null;

    public UnrateEpisode(FragmentActivity activity, OnMarkEpisodeNoneCompleted listener, ServiceManager manager, TvShow show, TvShowEpisode episode, int position){
        this.activity=activity;
        this.manager=manager;
        this.episode_info=episode;
        this.show_info=show;
        this.listener=listener;
        this.position=position;
    }




    @Override
    protected RatingResponse doInBackground(Void... voids) {
         try {

                Log.d("Trakt", "Marking "+show_info.tvdbId+" as hated");
                return manager.rateService().episode(show_info.title,show_info.year).season(episode_info.season).episode(episode_info.number).rating(Rating.Unrate).fire();

        } catch (Exception e) {
            this.e = e;
            return null;
        }
    }
    @Override
    protected void onPostExecute(RatingResponse response) {
        if(e==null){

            listener.OnMarkEpisodeNoneCompleted(position, response);

        }else{
            Log.d("Trakt","Error unrating episode");


            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("Error unrating episode")
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                                new UnrateEpisode(activity,listener,manager,show_info,episode_info,position).execute();
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

    public interface OnMarkEpisodeNoneCompleted {
        void OnMarkEpisodeNoneCompleted(int position, RatingResponse response);
    }
}
