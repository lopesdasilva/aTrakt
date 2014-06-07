package com.lopesdasilva.trakt.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        aq.id(R.id.imageViewShowFanart).image(show.images.fanart, false, true,600,R.drawable.episode_backdrop,aq.getCachedImage(R.drawable.episode_backdrop),AQuery.FADE_IN,AQuery.RATIO_PRESERVE);
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


                    Bundle arguments = new Bundle();

                    if(show.imdbId!=null && !"".equals(show.imdbId))
                        arguments.putString("show_imdb", show.imdbId);
                    else if(show.tvdbId!=null && !"".equals(show.tvdbId))
                        arguments.putString("show_imdb", show.tvdbId);
                    else
                        arguments.putString("show_imdb", show.title.replace(" ","-")+"-"+show.year);


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

        if("0".equals(show.ratingAdvanced)) {
            aq.id(R.id.relativeLayoutAdvanceRating).gone();
        } else if("1".equals(show.ratingAdvanced)){
            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_1);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(show.ratingAdvanced);
        } else if("2".equals(show.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_2);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(show.ratingAdvanced);
        } else if("3".equals(show.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_3);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(show.ratingAdvanced);
        } else if("4".equals(show.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_4);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewMovieRatingAdvance).text(show.ratingAdvanced);
        } else if("5".equals(show.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_5);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(show.ratingAdvanced);
        } else if("6".equals(show.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_6);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(show.ratingAdvanced);
        } else if("7".equals(show.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_7);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(show.ratingAdvanced);
        } else if("8".equals(show.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_8);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(show.ratingAdvanced);
        } else if("9".equals(show.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_9);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(show.ratingAdvanced);
        } else if("10".equals(show.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_10);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(show.ratingAdvanced);
            aq.id(R.id.textViewShowRatingAdvance).margin(0,0,1,0);
        }



        if (show.inCollection)
            aq.id(R.id.imageViewShowCollectionTag).visible();
        else
            aq.id(R.id.imageViewShowCollectionTag).gone();


        if (show.inWatchlist)
            aq.id(R.id.imageViewShowWatchlistTag).visible();
        else
            aq.id(R.id.imageViewShowWatchlistTag).gone();
    }


    public void updateInfo(TvShow show) {
        mTVshow=show;
        updateUI(rootView, mTVshow);
    }
}
