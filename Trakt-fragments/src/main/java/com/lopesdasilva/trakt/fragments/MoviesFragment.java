package com.lopesdasilva.trakt.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.Movie;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.Tasks.DownloadTrendingMovies;
import com.lopesdasilva.trakt.activities.MovieActivity;
import com.lopesdasilva.trakt.activities.MoviesActivity;
import com.lopesdasilva.trakt.extras.UserChecker;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lopesdasilva on 04/06/13.
 */
public class MoviesFragment extends Fragment implements DownloadTrendingMovies.onTrendingMoviesListTaskComplete {

    private View rootView;
    private List<Movie> mMoviesList = new LinkedList<Movie>();
    private DownloadTrendingMovies mTrendingMoviesTask;
    private ServiceManager manager;
    private LinearLayout mMoviesLayout;
    private LayoutInflater inflater;


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("movies", (Serializable) mMoviesList);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        rootView = inflater.inflate(R.layout.movies_fragment, container, false);



//        setRetainInstance(true);
        if (savedInstanceState == null) {
            manager = UserChecker.checkUserLogin(getActivity());
            mTrendingMoviesTask = new DownloadTrendingMovies(this, getActivity(), manager);
            mTrendingMoviesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else{
            mMoviesList = (List<Movie>) savedInstanceState.get("movies");
            if(mMoviesList.size()!=0)
            updateView(mMoviesList);
            else{
                manager = UserChecker.checkUserLogin(getActivity());
                mTrendingMoviesTask = new DownloadTrendingMovies(this, getActivity(), manager);
                mTrendingMoviesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

        }


        rootView.findViewById(R.id.buttonMoviesSeeMore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), MoviesActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);

            }
        });

        return rootView;

    }

    public void updateView(List<Movie> response) {
        rootView.findViewById(R.id.progressBarMovies).setVisibility(View.GONE);
        DisplayMetrics metrics = new DisplayMetrics();
        if (getActivity() != null) {

            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int widthPixels = metrics.widthPixels;


            float pixels = (int) (115 * (metrics.densityDpi / 160f));

//        Toast.makeText(getActivity(), "pixels" + pixels + " columns=" + (int) (widthPixels / pixels), Toast.LENGTH_SHORT).movie();

            mMoviesLayout = (LinearLayout) rootView.findViewById(R.id.linearLayoutMovies);

            for (Movie m : response.subList(0, (int) (widthPixels / pixels))) {

                RelativeLayout movieItem = (RelativeLayout) inflater.inflate(R.layout.movies_grid_item, null).findViewById(R.id.relativeLayoutMoviesMovie);

                setMovie(movieItem, m);
                mMoviesLayout.addView(movieItem);

                LinearLayout.LayoutParams layoutparams = (LinearLayout.LayoutParams) movieItem.getLayoutParams();
                layoutparams.setMargins(0, 0, 5, 0);

            }
        }

    }

    private void setMovie(RelativeLayout movieItem, final Movie m) {


        final AQuery aq = new AQuery(movieItem);
        aq.id(R.id.imageViewMoviesPoster).progress(R.id.progress).image(m.images.poster, true, true, 100, R.drawable.poster, null, AQuery.FADE_IN);
        aq.id(R.id.textViewMoviesMovieTitle).text(m.title + " (" + m.year + ")");
        aq.id(R.id.textViewMoviesMoviePercentage).text(m.ratings.percentage + "%");
        aq.id(R.id.textViewMoviesMovieViewers).text(m.watchers + " viewers");
        aq.id(R.id.textViewMoviesMovieNumberVotes).text(m.ratings.votes + " votes");


        if (m.watched!=null && m.watched)
            aq.id(R.id.imageViewMoviesSeenTag).visible();
        else
            aq.id(R.id.imageViewMoviesSeenTag).gone();


        if("0".equals(m.ratingAdvanced)) {
            aq.id(R.id.relativeLayoutAdvanceRating).gone();
        } else if("1".equals(m.ratingAdvanced)){
            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_1);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewMovieRatingAdvance).text(m.ratingAdvanced);
        } else if("2".equals(m.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_2);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewMovieRatingAdvance).text(m.ratingAdvanced);
        } else if("3".equals(m.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_3);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewMovieRatingAdvance).text(m.ratingAdvanced);
        } else if("4".equals(m.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_4);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewMovieRatingAdvance).text(m.ratingAdvanced);
        } else if("5".equals(m.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_5);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewMovieRatingAdvance).text(m.ratingAdvanced);
        } else if("6".equals(m.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_6);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewMovieRatingAdvance).text(m.ratingAdvanced);
        } else if("7".equals(m.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_7);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewMovieRatingAdvance).text(m.ratingAdvanced);
        } else if("8".equals(m.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_8);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewMovieRatingAdvance).text(m.ratingAdvanced);
        } else if("9".equals(m.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_9);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewMovieRatingAdvance).text(m.ratingAdvanced);
        } else if("10".equals(m.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_10);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewMovieRatingAdvance).text(m.ratingAdvanced);
            aq.id(R.id.textViewMovieRatingAdvance).margin(0,0,1,0);
        }


        movieItem.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        aq.id(R.id.imageViewMoviesPoster).getImageView().setColorFilter(0xaf0099cc, PorterDuff.Mode.SRC_ATOP);
                        break;

                    case MotionEvent.ACTION_UP:
                        aq.id(R.id.imageViewMoviesPoster).getImageView().clearColorFilter();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        aq.id(R.id.imageViewMoviesPoster).getImageView().clearColorFilter();
                        break;
                }

                return false;
            }
        });
        movieItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("Trakt Fragments", "Launching Movie Activity");

                Bundle arguments = new Bundle();

                if(m.imdbId!=null && !"".equals(m.imdbId))
                    arguments.putString("movie_imdb", m.imdbId);
                else if(m.tmdbId!=null && !"".equals(m.imdbId))
                    arguments.putString("movie_imdb", m.tmdbId);
                else
                    arguments.putString("movie_imdb", m.title.replace(" ","-")+"-"+m.year);


                Intent intent = new Intent(getActivity(), MovieActivity.class);
                intent.putExtras(arguments);

                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);
            }
        });
    }

    @Override
    public void onTrendingMoviesListTaskComplete(List<Movie> response) {
        this.mMoviesList = response;
        updateView(mMoviesList);
    }


    protected class MoviesGridAdapter extends BaseAdapter {

        private final List<Movie> mList;
        private final LayoutInflater inflater;

        public MoviesGridAdapter(Context context, List<Movie> mList) {

            this.mList = mList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int i) {
            return mList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.movies_grid_item, parent, false);
            }

            AQuery aq = new AQuery(convertView);
            aq.id(R.id.imageViewMoviesPoster).progress(R.id.progress).image(mList.get(position).images.poster, true, true, 100, R.drawable.poster, null, AQuery.FADE_IN);
            aq.id(R.id.textViewMoviesMovieTitle).text(mList.get(position).title + " (" + mList.get(position).year + ")");
            aq.id(R.id.textViewMoviesMoviePercentage).text(mList.get(position).ratings.percentage + "%");
            aq.id(R.id.textViewMoviesMovieViewers).text(mList.get(position).watchers + " viewers");
            aq.id(R.id.textViewMoviesMovieNumberVotes).text(mList.get(position).ratings.votes + " votes");


            if (mList.get(position).watched)
                aq.id(R.id.imageViewMoviesSeenTag).visible();
            else
                aq.id(R.id.imageViewMoviesSeenTag).gone();
            if (mList.get(position).rating != null) {
                switch (mList.get(position).rating) {

                    case Love:
                        aq.id(R.id.imageViewMoviesLovedTag).visible();
                        aq.id(R.id.imageViewMoviesHatedTag).gone();
                        break;
                    case Hate:
                        aq.id(R.id.imageViewMoviesLovedTag).gone();
                        aq.id(R.id.imageViewMoviesHatedTag).visible();
                        break;
                }
            }


            return convertView;
        }
    }
}

