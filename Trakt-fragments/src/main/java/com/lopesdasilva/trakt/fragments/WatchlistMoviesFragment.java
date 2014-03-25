package com.lopesdasilva.trakt.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.Movie;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.Tasks.DownloadWatchlistMovies;
import com.lopesdasilva.trakt.Tasks.MarkMovieWatchlistUnWatchlist;
import com.lopesdasilva.trakt.activities.MovieActivity;
import com.lopesdasilva.trakt.extras.UserChecker;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lopesdasilva on 04/06/13.
 */
public class WatchlistMoviesFragment extends Fragment implements DownloadWatchlistMovies.onWatchlistMoviesListTaskComplete, MarkMovieWatchlistUnWatchlist.WatchlistUnWatchlistCompleted {

    private View rootView;
    private GridView mGridView;
    private List<Movie> mMoviesListShowing = new LinkedList<Movie>();
    private ServiceManager manager;
    private MoviesGridAdapter mAdapter;
    private DownloadWatchlistMovies mWatchlistMoviesTask;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.moviesall_fragment, container, false);


        manager = UserChecker.checkUserLogin(getActivity());

        Log.d("Trakt", "Watchlist Movies saveInstance: " + savedInstanceState);
        if (savedInstanceState == null) {

            mWatchlistMoviesTask = new DownloadWatchlistMovies(this, getActivity(), manager);
            mWatchlistMoviesTask.execute();
        } else {

            if (((List<Movie>) savedInstanceState.get("movies")).size() != 0)
                updateView((List<Movie>) savedInstanceState.get("movies"));
            else {
                mWatchlistMoviesTask = new DownloadWatchlistMovies(this, getActivity(), manager);
                mWatchlistMoviesTask.execute();
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


            mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(mMoviesListShowing.get(i).title);


                    builder.setItems(getResources().getStringArray(R.array.watchlist_remove), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item_clicked) {

                            switch (item_clicked) {
                                case 0:
                                    Toast.makeText(getActivity(), "Mark watched", Toast.LENGTH_SHORT).show();
                                    new MarkMovieWatchlistUnWatchlist(getActivity(), WatchlistMoviesFragment.this, manager, mMoviesListShowing.get(i), i).execute();
//                                    new MarkEpisodeSeenUnseen(getActivity(), CalendarWeekFragment.this, manager, lista.get(position).movie, lista.get(position).episode, position).execute();
                                    break;
                                case 1:
                                    Toast.makeText(getActivity(), "Mark watched", Toast.LENGTH_SHORT).show();
//  new EpisodeWatchlistUnWatchlist(getActivity(), CalendarWeekFragment.this, manager, lista.get(position).movie, lista.get(position).episode, position).execute();
                                    break;
                            }
                        }
                    }
                    );

                    AlertDialog dialog = builder.create();
                    dialog.show();


                    return false;
                }
            });


            mAdapter.notifyDataSetChanged();


        }


    }


    @Override
    public void onWatchlistMoviesListTaskComplete(List<Movie> response) {
        updateView(response);
    }

    @Override
    public void WatchlistUnWatchlistCompleted(int position) {
        if (mAdapter != null && mMoviesListShowing != null && mMoviesListShowing.size() >= position) {
            mMoviesListShowing.remove(position);
            mAdapter.notifyDataSetChanged();
        }
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
