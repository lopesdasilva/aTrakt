package com.lopesdasilva.trakt.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.RatingResponse;
import com.jakewharton.trakt.entities.Response;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.Tasks.*;
import com.lopesdasilva.trakt.extras.UserChecker;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lopesdasilva on 17/05/13.
 */
public class MovieInfoFragment extends Fragment{
    private View rootView;
    private ServiceManager manager;
    private Movie movie_info;
    private Menu mMenu;
    private ImageView mRefreshView;
    private MenuItem mRefreshItem;
    private DownloadMovieInfo mTaskDownloadMovie;
    private String mUsername;


    public MovieInfoFragment() {
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.movie_info_fragment, container, false);


        setRetainInstance(true);
//        movie = getArguments().getString("movie_imdb");


        manager = UserChecker.checkUserLogin(getActivity());
        mUsername = UserChecker.getUsername(getActivity());

        setRetainInstance(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        movie_info = (Movie) getArguments().getSerializable("movie");
        updateUI(movie_info);
    }








    protected void updateSeenUnseen(MovieFragment movieFragment) {



        movie_info.watched = !movie_info.watched;
        updateUI(movie_info);
        movieFragment.updateOptionsMenu(movieFragment.mMenu);
    }

    protected void updateUI(final Movie movie) {
        if (getActivity() != null) {


            final AQuery aq = new AQuery(rootView);
            aq.id(R.id.textViewMovieTitle).text(movie.title);
            aq.id(R.id.imageViewEpisodeScreen).image(movie.images.fanart, false, true, 600, R.drawable.episode_backdrop).clicked(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!movie_info.trailer.equals(""))
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(movie.trailer)));
                    else
                        Toast.makeText(getActivity(), "Trailer not available", Toast.LENGTH_SHORT).show();


                }
            }).getView().setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            aq.id(R.id.imageViewMoviePlayTrailer).getImageView().setColorFilter(0xaf0099cc, PorterDuff.Mode.SRC_ATOP);
                            break;

                        case MotionEvent.ACTION_UP:
                            aq.id(R.id.imageViewMoviePlayTrailer).getImageView().clearColorFilter();
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            aq.id(R.id.imageViewMoviePlayTrailer).getImageView().clearColorFilter();
                            break;
                    }

                    return false;
                }
            });
            aq.id(R.id.imageViewMoviePoster).image(movie.images.poster, false, true, 600, R.drawable.poster);
            aq.id(R.id.textViewEpisodeOverview).text(movie.overview);
            aq.id(R.id.textViewEpisodeRatingsPercentage).text(movie.ratings.percentage + "%");
            aq.id(R.id.textViewEpisodeRatingsVotes).text(movie.ratings.votes + " votes");

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");

            aq.id(R.id.textViewEpisodeAirDate).text("Released " + dateFormat.format(movie.released));


            if (!movie.watched || movie.plays == 0)
                aq.id(R.id.imageViewEpisodeSeen).gone();
            else
                aq.id(R.id.imageViewEpisodeSeen).visible();

            if (!movie.inWatchlist)
                aq.id(R.id.imageViewWatchlistTag).gone();
            else
                aq.id(R.id.imageViewWatchlistTag).visible();


            if (!movie.inCollection)
                aq.id(R.id.imageViewCollectionTag).gone();
            else
                aq.id(R.id.imageViewCollectionTag).visible();


            if (movie.rating == null) {
                aq.id(R.id.imageViewHatedTag).gone();
                aq.id(R.id.imageViewLovedTag).gone();
            } else
                switch (movie.rating) {

                    case Love:
                        aq.id(R.id.imageViewHatedTag).gone();
                        aq.id(R.id.imageViewLovedTag).visible();
                        break;
                    case Hate:
                        aq.id(R.id.imageViewLovedTag).gone();
                        aq.id(R.id.imageViewHatedTag).visible();
                        break;
                }


//            setHasOptionsMenu(true);
        }
    }




}