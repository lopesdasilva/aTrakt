package com.lopesdasilva.trakt.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.DismissResponse;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.Tasks.DismissRecomendationShow;
import com.lopesdasilva.trakt.Tasks.DownloadRecommendedShows;
import com.lopesdasilva.trakt.Tasks.MarkSeenUnseen;
import com.lopesdasilva.trakt.activities.ShowActivity;
import com.lopesdasilva.trakt.extras.SwipeDismissListViewTouchListener;
import com.lopesdasilva.trakt.extras.UserChecker;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lopesdasilva on 02/07/13.
 */
public class RecommendedShowsFragment extends Fragment implements DownloadRecommendedShows.onRecommendedShowListTaskComplete, DismissRecomendationShow.onDismissedShowTaskComplete {

    private View rootView;
    private ServiceManager manager;
    private DownloadRecommendedShows recommendTask;
    private ListView mListView;
    private RecommendedShowsAdapter mAdapter;
    private List<TvShow> mRecommendationsList = new LinkedList<TvShow>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.recommended_fragment, container, false);

        manager = UserChecker.checkUserLogin(getActivity());

        mListView = (ListView) rootView.findViewById(R.id.listViewRecommendedShows);
        if (savedInstanceState == null) {
            recommendTask = new DownloadRecommendedShows(this, getActivity(), manager);
            recommendTask.execute();
        } else {
            mRecommendationsList = (List<TvShow>) savedInstanceState.get("recommendations");
            if (mRecommendationsList.size() != 0) {
                updateView(mRecommendationsList);
            } else {
                recommendTask = new DownloadRecommendedShows(this, getActivity(), manager);
                recommendTask.execute();
            }

        }

        return rootView;
    }

    private void updateView(List<TvShow> response) {
        if (getActivity() != null) {
            this.mRecommendationsList = response;

            mAdapter = new RecommendedShowsAdapter(getActivity(), mRecommendationsList);
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
                    arguments.putString("show_imdb", mRecommendationsList.get(i).imdbId);

                    Intent intent = new Intent(getActivity(), ShowActivity.class);
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
    public void onRecommendedShowListTaskComplete(List<TvShow> response) {
        updateView(response);

    }

    @Override
    public void onDismissedShowTaskComplete(DismissResponse response, int position) {
        Toast.makeText(getActivity(), response.show.title + " recommendation dismissed", Toast.LENGTH_SHORT).show();
    }

    public class RecommendedShowsAdapter extends BaseAdapter {

        private final List<TvShow> mListShows;
        private final LayoutInflater inflater;

        public RecommendedShowsAdapter(Context context, List<TvShow> mListShows) {
            this.mListShows = mListShows;
            inflater = LayoutInflater.from(context);
        }


        public void remove(TvShow show) {
            mListShows.remove(show);
        }

        @Override
        public int getCount() {
            return mListShows.size();
        }

        @Override
        public TvShow getItem(int i) {
            return mListShows.get(i);
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
            aq.id(R.id.imageViewRecommendedShowPoster).image(mListShows.get(position).images.poster, true, true, 100, 0, null, AQuery.FADE_IN);
            aq.id(R.id.textViewRecommendedShowTitle).text(mListShows.get(position).title);
            aq.id(R.id.textViewRecommendedShowOverview).text(mListShows.get(position).overview);
            aq.id(R.id.textViewRecommendedShowNetwork).text(mListShows.get(position).network);
            aq.id(R.id.textViewRecommendedShowPercentage).text(mListShows.get(position).ratings.percentage + "%");
            aq.id(R.id.textViewRecommendedShowVotes).text(mListShows.get(position).ratings.votes + " votes");
            if(mListShows.get(position).airDay!=null && mListShows.get(position).airTime!=null)
            aq.id(R.id.textViewRecommendedShowAirDate).text(mListShows.get(position).airDay.toString() + " at " + mListShows.get(position).airTime);
            else
                aq.id(R.id.textViewRecommendedShowAirDate).text("");


            aq.id(R.id.imageViewRecommendedShowOverflow).clicked(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showSeenMenu(view, mListShows.get(position), position);
                }
            });

            return convertView;
        }
    }

    public void showSeenMenu(View v, final TvShow show, final int position) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        popup.inflate(R.menu.dismiss_show);
        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {


                switch (menuItem.getItemId()) {

                    case R.id.action_recommended_dismiss_show:

                        new DismissRecomendationShow(RecommendedShowsFragment.this, getActivity(), manager, mAdapter.getItem(position), position).execute();

                        mAdapter.remove(mAdapter.getItem(position));
                        mAdapter.notifyDataSetChanged();

                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

}
