package com.lopesdasilva.trakt.Tasks;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ActionMode;
import android.widget.Toast;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.services.ShowService;

import java.util.List;

/**
 * Created by lopesdasilva on 28/06/13.
 */
public class MultipleSeenUnseenTask extends AsyncTask<Void, Void, Void> {

    private final OnMarkSeenUnseenCompleted listener;
    private final FragmentActivity activity;
    private final ServiceManager manager;
    private final boolean seen;
    private final List<TvShowEpisode> mSelectedEpisodes;
    private final TvShow mShow;
    private final ActionMode mode;
    private Exception e;


    public MultipleSeenUnseenTask(FragmentActivity activity, OnMarkSeenUnseenCompleted listener, ServiceManager manager, TvShow show, List<TvShowEpisode> mSelectedEpisodes, boolean markSeen, ActionMode mode) {
        this.activity = activity;
        this.manager = manager;
        this.listener = listener;
        this.seen = markSeen;
        this.mSelectedEpisodes = mSelectedEpisodes;
        mShow = show;
        this.mode=mode;

    }


    @Override
    protected Void doInBackground(Void... voids) {


        try {
            if (seen) {
                ShowService.EpisodeSeenBuilder seenBuilder = manager.showService().episodeSeen(mShow.imdbId);
                for (TvShowEpisode episode : mSelectedEpisodes) {
                    seenBuilder = seenBuilder.episode(episode.season, episode.number);

                }
                return seenBuilder.fire();
            } else {
                ShowService.EpisodeUnseenBuilder unseenBuilder = manager.showService().episodeUnseen(mShow.imdbId);

                for (TvShowEpisode episode : mSelectedEpisodes) {
                    unseenBuilder = unseenBuilder.episode(episode.season, episode.number);

                }
                return unseenBuilder.fire();
            }
        } catch (Exception e) {
            this.e = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void Result) {
        if (e == null) {
            listener.OnMultipleMarkSeenUnseenCompleted(mSelectedEpisodes, seen, mode);
        } else {
            Toast.makeText(activity, "Failed TODO Retry method", Toast.LENGTH_SHORT).show();
        }


    }


    public interface OnMarkSeenUnseenCompleted {
        void OnMultipleMarkSeenUnseenCompleted(List<TvShowEpisode> mSelectedEpisodes, boolean markSeen, ActionMode mode);
    }
}
