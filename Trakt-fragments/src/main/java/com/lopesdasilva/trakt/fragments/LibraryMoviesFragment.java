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
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.Tasks.DownloadLibraryMovies;
import com.lopesdasilva.trakt.activities.MovieActivity;
import com.lopesdasilva.trakt.extras.UserChecker;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lopesdasilva on 04/06/13.
 */
public class LibraryMoviesFragment extends Fragment implements DownloadLibraryMovies.onLibraryMoviesListTaskComplete {

    private View rootView;
    private GridView mGridView;
    private List<Movie> mMoviesListShowing = new LinkedList<Movie>();
    private ServiceManager manager;
    private MoviesGridAdapter mAdapter;
    private DownloadLibraryMovies mLibraryMoviesTask;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.moviesall_fragment, container, false);

        Log.d("Trakt", "Library Movies saveInstance: " + savedInstanceState);
        if (savedInstanceState == null) {
            manager = UserChecker.checkUserLogin(getActivity());
            mLibraryMoviesTask = new DownloadLibraryMovies(this, getActivity(), manager);
            mLibraryMoviesTask.execute();
        } else {

            mMoviesListShowing = (List<Movie>) savedInstanceState.get("movies");
            if (mMoviesListShowing.size() != 0)
                updateView(mMoviesListShowing);
            else {
                manager = UserChecker.checkUserLogin(getActivity());
                mLibraryMoviesTask = new DownloadLibraryMovies(this, getActivity(), manager);
                mLibraryMoviesTask.execute();
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
    public void onLibraryMoviesListTaskComplete(List<Movie> response) {
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
                convertView = inflater.inflate(R.layout.library_movies_grid_item, parent, false);
            }

            AQuery aq = new AQuery(convertView);

            aq.id(R.id.imageViewMoviesPoster).progress(R.id.progress).image(mList.get(position).images.poster, true, true, 100, R.drawable.poster, null, AQuery.FADE_IN);
            aq.id(R.id.textViewMoviesMovieTitle).text(mList.get(position).title);
            aq.id(R.id.textViewLibraryMovieYear).text(mList.get(position).year + "");


            if (mList.get(position).plays == 0) {
                aq.id(R.id.textViewMoviesMoviePlays).text(mList.get(position).plays + " plays");
                aq.id(R.id.imageViewLibraryMoviesSeenTag).gone();
            } else if (mList.get(position).plays == 1) {
                aq.id(R.id.textViewMoviesMoviePlays).text(mList.get(position).plays + " play");
                aq.id(R.id.imageViewLibraryMoviesSeenTag).visible();
            } else {
                aq.id(R.id.textViewMoviesMoviePlays).text(mList.get(position).plays + " plays");
                aq.id(R.id.imageViewLibraryMoviesSeenTag).visible();
            }

            if (mList.get(position).inCollection)
                aq.id(R.id.imageViewLibraryCollectionTag).visible();
            else
                aq.id(R.id.imageViewLibraryCollectionTag).gone();

            return convertView;
        }
    }
}
