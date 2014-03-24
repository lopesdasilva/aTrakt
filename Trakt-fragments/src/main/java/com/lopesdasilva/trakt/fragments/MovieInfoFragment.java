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
public class MovieInfoFragment extends Fragment implements DownloadMovieInfo.OnMovieTaskCompleted, RateMovieHate.OnRatingMovieHateCompleted, RateMovieLove.OnRatingMovieLoveCompleted, UnrateMovie.OnUnratingMovieCompleted {
    private View rootView;
    private String movie;
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
//
//        Log.d("Trakt", "ServiceManager: " + manager);
//        Log.d("Trakt", "movie_imdb received: " + movie);


//        mTaskDownloadMovie = new DownloadMovieInfo(this, getActivity(), manager, movie);
//        mTaskDownloadMovie.execute();

//        new CheckInChecker(getActivity(), manager, mUsername).execute();


        movie_info = (Movie) getArguments().getSerializable("movie");
        updateUI();

        return rootView;
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.mMenu = menu;
        updateOptionsMenu(mMenu);

    }

    public void updateOptionsMenu(Menu menu){
        menu.clear();
        menu.add(0, 0, 0, R.string.refresh).setIcon(android.R.drawable.ic_popup_sync).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(1,1,1,R.string.share).setIcon(android.R.drawable.ic_menu_share).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        if (movie_info.watched) {
            menu.add(0, 2, 2, R.string.unseen);
//                    .setIcon(R.drawable.ic_action_accept_on).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        } else
            menu.add(0, 2, 2, R.string.seen);
//                    .setIcon(R.drawable.ic_action_accept).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        ;


        if (movie_info.inWatchlist) {
            menu.add(0, 3, 3, R.string.unwatchlist);
        } else
            menu.add(0, 3, 3, R.string.watchlist);

        menu.add(0, 7, 7, R.string.checkin);

        if(movie_info.rating!=null){
            switch (movie_info.rating){

                case Love:
                    menu.add(0, 6, 6, R.string.unrate);
                    menu.add(0, 5, 5,  R.string.hated);
                    break;
                case Hate:
                    menu.add(0, 6, 6,  R.string.unrate);
                    menu.add(0, 4, 4, R.string.loved);

                    break;
            }
        }   else{
            menu.add(0, 4, 4, R.string.loved);
            menu.add(0, 5, 5, R.string.hated);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, movie_info.title);
                i.putExtra(Intent.EXTRA_TEXT, movie_info.url);
                startActivity(Intent.createChooser(i, "Share "+movie_info.title));

                return true;
            case 0:
                if (mTaskDownloadMovie == null) {

                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    mRefreshView = (ImageView) inflater.inflate(R.layout.refresh, null);
//
//                // Load the animation
                    Animation rotateClockwise = AnimationUtils.loadAnimation(getActivity(), R.anim.rotation);
//
//                // Apply the animation to our View
                    mRefreshView.startAnimation(rotateClockwise);
                    mRefreshItem = item;
//                // Apply the View to our MenuItem
                    item.setActionView(mRefreshView);

                    mTaskDownloadMovie = new DownloadMovieInfo(this, getActivity(), manager, movie);
                    mTaskDownloadMovie.execute();
                }
                return true;
            case 2:
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mRefreshView = (ImageView) inflater.inflate(R.layout.refresh, null);
//
//                // Load the animation
                Animation rotateClockwise = AnimationUtils.loadAnimation(getActivity(), R.anim.rotation);
//
//                // Apply the animation to our View
                mRefreshView.startAnimation(rotateClockwise);
                mRefreshItem = item;
//                // Apply the View to our MenuItem
                item.setActionView(mRefreshView);

                Log.d("Trakt Fragments", "Unseen button clicked");
                new MovieSeenUnseen().execute();
                // do whatever
                return true;
            case 3:

                Log.d("Trakt Fragments", "Add/Rem watchlist button clicked");
                new MovieWatchlist().execute();
                return true;
            case 4:
                new RateMovieLove(getActivity(), MovieInfoFragment.this, manager, movie_info, 0).execute();
                break;
            case 5:
                new RateMovieHate(getActivity(), MovieInfoFragment.this, manager, movie_info, 0).execute();
                break;
            case 6:
                new UnrateMovie(getActivity(), MovieInfoFragment.this, manager, movie_info, 0).execute();
                break;
            case 7:
                new MovieCheckIn().execute();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }


    @Override
    public void onDownloadMovieInfoComplete(Movie response) {
        movie_info = response;
        updateUI();
        mTaskDownloadMovie = null;
    }

    @Override
    public void OnRatingMovieHateCompleted(int position, RatingResponse response) {
        movie_info.rating=response.rating;
        updateOptionsMenu(mMenu);
        updateUI();
    }

    @Override
    public void OnRatingMovieLoveCompleted(int position, RatingResponse response) {
        movie_info.rating=response.rating;
        updateOptionsMenu(mMenu);
        updateUI();
    }

    @Override
    public void OnUnratingMovieCompleted(int position, RatingResponse response) {
        movie_info.rating=response.rating;
        updateOptionsMenu(mMenu);
        updateUI();
    }

    private class MovieSeenUnseen extends AsyncTask<Void, Void, Void> {


        private Exception e;

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                if (movie_info.watched) {
                    Log.d("Trakt Fragments", "Changing to unseen");
                    return manager.movieService().unseen().movie(movie).fire();
                } else {
                    Log.d("Trakt Fragments", "Changing to seen");
                    return manager.movieService().seen().movie(movie, 1, new Date()).fire();
                }
            } catch (Exception e) {
                this.e = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(Void voids) {
            if (e == null) {
                Log.d("Trakt Fragments", "Updating seen status ui");
                updateSeenUnseen();
            } else {
                Log.d("Trakt Fragments", "Error marking episode as unseen: " + e.getMessage());

            }
        }

    }

    private class MovieWatchlist extends AsyncTask<Void, Void, Void> {


        private Exception e;

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                if (movie_info.inWatchlist) {
                    Log.d("Trakt", "Adding to Unwatchlist");
                    return manager.movieService().unwatchlist().movie(movie).fire();
                } else {
                    Log.d("Trakt", "Adding to Watchlist");
                    return manager.movieService().watchlist().movie(movie).fire();
                }
            } catch (Exception e) {
                this.e = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(Void voids) {
            if (e == null) {
                Log.d("Trakt", "Updating watchlist status ui");
                updateWatchlist();
            } else {
                Log.d("Trakt", "Error changing watchlist status: " + e.getMessage());
            }
        }
    }

    private void updateWatchlist() {
        movie_info.inWatchlist = !movie_info.inWatchlist;
        updateOptionsMenu(mMenu);
        updateUI();
    }

    private void updateSeenUnseen() {



        movie_info.watched = !movie_info.watched;
        updateUI();
        updateOptionsMenu(mMenu);
    }

    private void updateUI() {
        if (getActivity() != null) {
            //So that animation in actionbar stop
            if (mRefreshView != null) {

                mRefreshView.clearAnimation();
                mRefreshItem.setActionView(null);
            }
            getActivity().getActionBar().setSubtitle(movie_info.title);
            final AQuery aq = new AQuery(getActivity());
            aq.id(R.id.textViewEpisodeTitle).text(movie_info.title);
            aq.id(R.id.imageViewEpisodeScreen).image(movie_info.images.fanart, false, true, 600, R.drawable.episode_backdrop).clicked(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!movie_info.trailer.equals(""))
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(movie_info.trailer)));
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
            aq.id(R.id.imageViewMoviePoster).image(movie_info.images.poster, false, true, 600, R.drawable.poster);
            aq.id(R.id.textViewEpisodeOverview).text(movie_info.overview);
            aq.id(R.id.textViewEpisodeRatingsPercentage).text(movie_info.ratings.percentage + "%");
            aq.id(R.id.textViewEpisodeRatingsVotes).text(movie_info.ratings.votes + " votes");

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");

            aq.id(R.id.textViewEpisodeAirDate).text("Released " + dateFormat.format(movie_info.released));


            if (!movie_info.watched || movie_info.plays == 0)
                aq.id(R.id.imageViewEpisodeSeen).gone();
            else
                aq.id(R.id.imageViewEpisodeSeen).visible();

            if (!movie_info.inWatchlist)
                aq.id(R.id.imageViewWatchlistTag).gone();
            else
                aq.id(R.id.imageViewWatchlistTag).visible();


            if (!movie_info.inCollection)
                aq.id(R.id.imageViewCollectionTag).gone();
            else
                aq.id(R.id.imageViewCollectionTag).visible();


            if (movie_info.rating == null) {
                aq.id(R.id.imageViewHatedTag).gone();
                aq.id(R.id.imageViewLovedTag).gone();
            } else
                switch (movie_info.rating) {

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

    private class MovieCheckIn extends AsyncTask<Void, Void, Response> {


        private Exception e;

        @Override
        protected Response doInBackground(Void... voids) {

            try {
                return manager.movieService().checkin(movie_info.title, movie_info.year).fire();


            } catch (Exception e) {
                this.e = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(Response response) {
            if (e == null) {
                Log.d("Trakt Fragments", "Checked in movie " + movie_info.title);
                new CheckInChecker(getActivity(), manager, mUsername).execute();
            } else {
                Log.d("Trakt Fragments", "Error marking episode as unseen: " + e.getMessage());

            }
        }
    }


}