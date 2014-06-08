package com.lopesdasilva.trakt.fragments;


import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.Movie;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.Tasks.DownloadMovieInfo;
import com.lopesdasilva.trakt.extras.UserChecker;

import java.text.SimpleDateFormat;

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

            aq.id(R.id.imageViewEpisodeScreen).image(movie.images.fanart, false, true, 600,R.drawable.episode_backdrop,aq.getCachedImage(R.drawable.episode_backdrop),AQuery.FADE_IN,AQuery.RATIO_PRESERVE).clicked(new View.OnClickListener() {
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
            aq.id(R.id.imageViewMoviePoster).image(movie.images.poster, false, true, 600, R.drawable.poster,aq.getCachedImage(R.drawable.poster),AQuery.FADE_IN,AQuery.RATIO_PRESERVE);
            aq.id(R.id.textViewEpisodeOverview).text(movie.overview);
            aq.id(R.id.textViewEpisodeRatingsPercentage).text(movie.ratings.percentage + "%");
            aq.id(R.id.textViewEpisodeRatingsVotes).text(movie.ratings.votes + " votes");
            aq.id(R.id.textViewMovieInfoRuntime).text(movie.runtime+" minutes");
            StringBuilder genres= new StringBuilder();
            for(int index=0; index!=movie.genres.length;index++){
                genres.append(movie.genres[index]);
                if(index!=movie.genres.length-1)
                    genres.append(" | ");

            }

            aq.id(R.id.textViewMovieInfoGenre).text(genres.toString());


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


//            if (movie.rating == null) {
//                aq.id(R.id.imageViewHatedTag).gone();
//                aq.id(R.id.imageViewLovedTag).gone();
//            } else
//                switch (movie.rating) {
//
//                    case Love:
//                        aq.id(R.id.imageViewHatedTag).gone();
//                        aq.id(R.id.imageViewLovedTag).visible();
//                        break;
//                    case Hate:
//                        aq.id(R.id.imageViewLovedTag).gone();
//                        aq.id(R.id.imageViewHatedTag).visible();
//                        break;
//                }
            if("0".equals(movie.ratingAdvanced)) {
                aq.id(R.id.relativeLayoutAdvanceRating).gone();
            } else if("1".equals(movie.ratingAdvanced)){
                aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_1);
                aq.id(R.id.relativeLayoutAdvanceRating).visible();
                aq.id(R.id.textViewMovieRatingAdvance).text(movie.ratingAdvanced);
            } else if("2".equals(movie.ratingAdvanced)){

                aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_2);
                aq.id(R.id.relativeLayoutAdvanceRating).visible();
                aq.id(R.id.textViewMovieRatingAdvance).text(movie.ratingAdvanced);
            } else if("3".equals(movie.ratingAdvanced)){

                aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_3);
                aq.id(R.id.relativeLayoutAdvanceRating).visible();
                aq.id(R.id.textViewMovieRatingAdvance).text(movie.ratingAdvanced);
            } else if("4".equals(movie.ratingAdvanced)){

                aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_4);
                aq.id(R.id.relativeLayoutAdvanceRating).visible();
                aq.id(R.id.textViewMovieRatingAdvance).text(movie.ratingAdvanced);
            } else if("5".equals(movie.ratingAdvanced)){

                aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_5);
                aq.id(R.id.relativeLayoutAdvanceRating).visible();
                aq.id(R.id.textViewMovieRatingAdvance).text(movie.ratingAdvanced);
            } else if("6".equals(movie.ratingAdvanced)){

                aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_6);
                aq.id(R.id.relativeLayoutAdvanceRating).visible();
                aq.id(R.id.textViewMovieRatingAdvance).text(movie.ratingAdvanced);
            } else if("7".equals(movie.ratingAdvanced)){

                aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_7);
                aq.id(R.id.relativeLayoutAdvanceRating).visible();
                aq.id(R.id.textViewMovieRatingAdvance).text(movie.ratingAdvanced);
            } else if("8".equals(movie.ratingAdvanced)){

                aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_8);
                aq.id(R.id.relativeLayoutAdvanceRating).visible();
                aq.id(R.id.textViewMovieRatingAdvance).text(movie.ratingAdvanced);
            } else if("9".equals(movie.ratingAdvanced)){

                aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_9);
                aq.id(R.id.relativeLayoutAdvanceRating).visible();
                aq.id(R.id.textViewMovieRatingAdvance).text(movie.ratingAdvanced);
            } else if("10".equals(movie.ratingAdvanced)){

                aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_10);
                aq.id(R.id.relativeLayoutAdvanceRating).visible();
                aq.id(R.id.textViewMovieRatingAdvance).text(movie.ratingAdvanced);
               aq.id(R.id.textViewMovieRatingAdvance).margin(0,0,1,0);
            }





//            setHasOptionsMenu(true);
        }
    }




}