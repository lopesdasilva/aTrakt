package com.lopesdasilva.trakt.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.DismissResponse;
import com.jakewharton.trakt.entities.Movie;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.Tasks.*;
import com.lopesdasilva.trakt.activities.MovieActivity;
import com.lopesdasilva.trakt.extras.UserChecker;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lopesdasilva on 02/07/13.
 */
public class RecommendedMoviesFragment extends Fragment implements DismissRecomendationMovie.onDismissedMovieTaskComplete, DownloadRecommendedMovies.onRecommendedMovieListTaskComplete, MarkMovieSeenUnseen.OnMovieMarkSeenUnseenCompleted {

    private View rootView;
    private ServiceManager manager;
    private DownloadRecommendedMovies recommendTask;
    private ListView mListView;
    private RecommendedMoviesAdapter mAdapter;
    private List<Movie> mRecommendationsList = new LinkedList<Movie>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.recommended_fragment, container, false);

        manager = UserChecker.checkUserLogin(getActivity());

        mListView = (ListView) rootView.findViewById(R.id.listViewRecommendedShows);
        if (savedInstanceState == null) {
            recommendTask = new DownloadRecommendedMovies(this, getActivity(), manager);
            recommendTask.execute();
        } else {
            mRecommendationsList = (List<Movie>) savedInstanceState.get("recommendations");
            if (mRecommendationsList.size() != 0) {
                updateView(mRecommendationsList);
            } else {
                recommendTask = new DownloadRecommendedMovies(this, getActivity(), manager);
                recommendTask.execute();
            }

        }

        return rootView;
    }

    private void updateView(List<Movie> response) {
        if (getActivity() != null) {
            this.mRecommendationsList = response;

            mAdapter = new RecommendedMoviesAdapter(getActivity(), mRecommendationsList);
            mListView.setAdapter(mAdapter);


//            SwipeDismissListViewTouchListener touchListener =
//                    new SwipeDismissListViewTouchListener(
//                            mListView,
//                            new SwipeDismissListViewTouchListener.OnDismissCallback() {
//                                @Override
//                                public void onDismiss(ListView listView, int[] reverseSortedPositions) {
//                                    for (int position : reverseSortedPositions) {
//                                        new DismissRecomendationShow(RecommendedShowsFragment.this, getActivity(), manager, mAdapter.getItem(position), position).execute();
//                                        mAdapter.remove(mAdapter.getItem(position));
//                                    }
//                                    mAdapter.notifyDataSetChanged();
//                                }
//                            });
//
//
//            mListView.setOnTouchListener(touchListener);
//            mListView.setOnScrollListener(touchListener.makeScrollListener());

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Bundle arguments = new Bundle();
                    arguments.putString("movie_imdb", mRecommendationsList.get(i).imdbId);

                    Intent intent = new Intent(getActivity(), MovieActivity.class);
                    intent.putExtras(arguments);

                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);
                }
            });

            mListView.setDivider(null);
            mAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("recommendations", (Serializable) mRecommendationsList);
    }

    @Override
    public void onRecommendedMovieListTaskComplete(List<Movie> response) {
        updateView(response);

    }

    @Override
    public void onDismissedMovieTaskComplete(DismissResponse response, int position) {
        Toast.makeText(getActivity(), response.movie.title + " recommendation dismissed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnMovieMarkSeenUnseenCompleted(int position) {
        Log.d("trakt", "Done marking seen removing from recommendations list");

        Toast.makeText(getActivity(), mAdapter.getItem(position).title + " marked as seen", Toast.LENGTH_SHORT).show();
        mAdapter.remove(mAdapter.getItem(position));
        mAdapter.notifyDataSetChanged();

    }

    public class RecommendedMoviesAdapter extends BaseAdapter {

        private final List<Movie> mListMovies;
        private final LayoutInflater inflater;

        public RecommendedMoviesAdapter(Context context, List<Movie> mListMovies) {
            this.mListMovies = mListMovies;
            inflater = LayoutInflater.from(context);
        }


        public void remove(Movie movie) {
            mListMovies.remove(movie);
        }

        @Override
        public int getCount() {
            return mListMovies.size();
        }

        @Override
        public Movie getItem(int i) {
            return mListMovies.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.recommended_shows_item, parent, false);
            }
            AQuery aq = new AQuery(convertView);
            aq.id(R.id.imageViewRecommendedShowPoster).image(mListMovies.get(position).images.poster, true, true, 100, 0, null, AQuery.FADE_IN);
            aq.id(R.id.textViewRecommendedShowTitle).text(mListMovies.get(position).title);
            aq.id(R.id.textViewRecommendedShowOverview).text(mListMovies.get(position).overview);
            aq.id(R.id.textViewRecommendedShowPercentage).text(mListMovies.get(position).ratings.percentage + "%");
            aq.id(R.id.textViewRecommendedShowVotes).text(mListMovies.get(position).ratings.votes + " votes");
            aq.id(R.id.textViewRecommendedShowAirDate).gone();
            aq.id(R.id.textViewRecommendedShowNetwork).gone();
            aq.id(R.id.imageViewRecommendedShowOverflow).clicked(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showSeenMenu(view, mListMovies.get(position), position);
                }
            });
            return convertView;
        }
    }

    public void showSeenMenu(View v, final Movie show, final int position) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        popup.inflate(R.menu.dismiss_movie);
//        if(show.watched){
//        popup.getMenu().getItem(R.id.action_recommended_seen_show).setVisible(false);
//        popup.getMenu().getItem(R.id.action_recommended_unseen_show).setVisible(true);
//    }else{
//            popup.getMenu().getItem(R.id.action_recommended_seen_show).setVisible(true);
//            popup.getMenu().getItem(R.id.action_recommended_unseen_show).setVisible(false);
//        }
        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {


                switch (menuItem.getItemId()) {

                    case R.id.action_recommended_dismiss_show:

                        new DismissRecomendationMovie(RecommendedMoviesFragment.this, getActivity(), manager, mAdapter.getItem(position), position).execute();

                        mAdapter.remove(mAdapter.getItem(position));
                        mAdapter.notifyDataSetChanged();

                        return true;
                    case R.id.action_recommended_seen_movie:
                        mAdapter.getItem(position).watched=false;
                        new MarkMovieSeenUnseen(getActivity(), RecommendedMoviesFragment.this, manager, mAdapter.getItem(position), position).execute();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }


}
