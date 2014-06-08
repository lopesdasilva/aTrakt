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
import com.lopesdasilva.trakt.fragments.ShowSeasonsFragment;

/**
 * Created by lopesdasilva on 30/05/13.
 */
public class RateAdvancedEpisode extends AsyncTask<Void, Void, RatingResponse> {


    private final ServiceManager manager;
    private final OnRatingAdvancedEpisodeCompleted listener;
    private final TvShow show_info;
    private final int position;
    private final FragmentActivity activity;
    private final int rating_advanced;
    private final TvShowEpisode episode;
    private Exception e = null;

    public RateAdvancedEpisode(FragmentActivity activity, OnRatingAdvancedEpisodeCompleted listener, ServiceManager manager, TvShow show, TvShowEpisode episode, int rating_advanced, int position) {
        this.activity = activity;
        this.manager = manager;
        this.show_info = show;
        this.listener = listener;
        this.position = position;
        this.episode = episode;
        this.rating_advanced = rating_advanced;
    }



    @Override
    protected RatingResponse doInBackground(Void... voids) {
        try {

            Log.d("Trakt", "Marking " + show_info.tvdbId + " as loved");
            return manager.rateService().episode(show_info.title, show_info.year).season(episode.season).episode(episode.number).advanced_rating(rating_advanced).fire();

        } catch (Exception e) {
            this.e = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(RatingResponse response) {
        if (e == null) {

            listener.OnRatingAdvancedEpisodeCompleted(position, response);

        } else {
            Log.d("Trakt", "Error rating episode as: " + rating_advanced);


            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("Error rating the episode")
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            new RateAdvancedEpisode(activity, listener, manager, show_info, episode, rating_advanced, position).execute();
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

    public interface OnRatingAdvancedEpisodeCompleted {
        void OnRatingAdvancedEpisodeCompleted(int position, RatingResponse response);
    }
}
