package com.lopesdasilva.trakt.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.androidquery.AQuery;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.entities.TvShowSeason;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.activities.EpisodeActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lopesdasilva on 22/05/13.
 */
public class ShowInfoFragment extends Fragment {


    private TvShow mTVshow;
    private View rootView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.showinfo_fragment, container, false);

        setRetainInstance(true);


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setRetainInstance(true);

        mTVshow = (TvShow) getArguments().getSerializable("show");

        updateUI(rootView, mTVshow);

    }

    private void updateUI(View rootView, final TvShow show) {

        AQuery aq = new AQuery(rootView);
        aq.id(R.id.textViewShowInfo).text(show.title);
        aq.id(R.id.imageViewShowFanart).image(show.images.fanart, false, true);
        aq.id(R.id.textViewShowOverview).text(show.overview);
        aq.id(R.id.textViewShowNetwork).text(show.network);
        aq.id(R.id.textViewAirDate).text(show.airDay.name().substring(0, 3) + " " + show.airTime.replace(":00", ""));

        TvShowEpisode episode_unwatched = null;
        for (TvShowSeason season : show.seasons) {
            if (season.season != 0)
                for (TvShowEpisode episode : season.episodes.episodes) {
                    if (!episode.watched) {
                        episode_unwatched = episode;
                        break;
                    }
                }
        }
        final TvShowEpisode finalEpisode_unwatched = episode_unwatched;
        if (episode_unwatched != null) {
            aq.id(R.id.textViewShowNextEpisode).text("Next episode: S" + episode_unwatched.season + "E" + episode_unwatched.number + " - " + episode_unwatched.title);

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
            if (episode_unwatched.firstAired.after(new Date()))
                aq.id(R.id.textViewShowNextEpisodeDate).text("Airs " + dateFormat.format(episode_unwatched.firstAired));
            else
                aq.id(R.id.textViewShowNextEpisodeDate).text("Aired " + dateFormat.format(episode_unwatched.firstAired));
            aq.id(R.id.buttonShowNextEpisode).visible().clicked(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(), "Laucnhing episode", Toast.LENGTH_SHORT).show();


                    Bundle arguments = new Bundle();
                    arguments.putString("show_imdb", show.imdbId);
                    arguments.putInt("show_season", finalEpisode_unwatched.season);
                    arguments.putInt("show_episode", finalEpisode_unwatched.number);
                    Intent i = new Intent(getActivity(), EpisodeActivity.class);
                    i.putExtras(arguments);

                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);

                }
            });
        } else {
            aq.id(R.id.buttonShowNextEpisode).gone();
            aq.id(R.id.textViewShowNextEpisode).text("You have no episodes to watch");
        }
if (show.rating!=null)
        switch (show.rating) {

            case Love:
                aq.id(R.id.imageViewShowHatedTag).gone();
                aq.id(R.id.imageViewShowLovedTag).visible();
                break;
            case Hate:
                aq.id(R.id.imageViewShowLovedTag).gone();
                aq.id(R.id.imageViewShowHatedTag).visible();
                break;
        }



    }


}
