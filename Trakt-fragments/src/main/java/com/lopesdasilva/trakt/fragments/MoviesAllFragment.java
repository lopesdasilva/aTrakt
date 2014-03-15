package com.lopesdasilva.trakt.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.TvShow;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.Tasks.DownloadTrendingMovies;
import com.lopesdasilva.trakt.activities.MovieActivity;
import com.lopesdasilva.trakt.activities.ShowActivity;
import com.lopesdasilva.trakt.extras.UserChecker;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lopesdasilva on 04/06/13.
 */
public class MoviesAllFragment extends Fragment implements DownloadTrendingMovies.onTrendingMoviesListTaskComplete {

    private View rootView;
    private GridView mGridView;
    private List<Movie> mMoviesListShowing = new LinkedList<Movie>();
    private ServiceManager manager;
    private MoviesGridAdapter mAdapter;
    private DownloadTrendingMovies mTrendingMoviesTask;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.moviesall_fragment, container, false);

        Log.d("Trakt", "MoviesAllFragment saveInstance: " + savedInstanceState);
        if (savedInstanceState == null) {
            manager = UserChecker.checkUserLogin(getActivity());
            mTrendingMoviesTask = new DownloadTrendingMovies(this, getActivity(), manager);
            mTrendingMoviesTask.execute();
        } else {

            mMoviesListShowing = (List<Movie>) savedInstanceState.get("movies");
            if (mMoviesListShowing.size() != 0)
                updateView(mMoviesListShowing);
            else {
                manager = UserChecker.checkUserLogin(getActivity());
                mTrendingMoviesTask = new DownloadTrendingMovies(this, getActivity(), manager);
                mTrendingMoviesTask.execute();
            }
        }

        return rootView;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("movies", (Serializable) mMoviesListShowing);
    }


    public void updateView(List<Movie> response) {
//        mMoviesListShowing.clear();
        mMoviesListShowing.addAll(response);

        if (getActivity() != null) {

            mGridView = (GridView) rootView.findViewById(R.id.gridViewMoviesAll);
            mAdapter = new MoviesGridAdapter(getActivity(), mMoviesListShowing);
            mGridView.setAdapter(mAdapter);

            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Bundle arguments = new Bundle();
                    arguments.putString("movie_imdb", mMoviesListShowing.get(i).imdbId);

                    Intent intent = new Intent(getActivity(), MovieActivity.class);
                    intent.putExtras(arguments);

                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);
                }
            });

            mAdapter.notifyDataSetChanged();


        }


    }

    @Override
    public void onTrendingMoviesListTaskComplete(List<Movie> response) {
        updateView(response);
    }

    public class MoviesGridAdapter extends BaseAdapter {

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
            }else{
                aq.id(R.id.imageViewMoviesHatedTag).gone();
                aq.id(R.id.imageViewMoviesLovedTag).gone();
            }

            return convertView;
        }
    }
}
