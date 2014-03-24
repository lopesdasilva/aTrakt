package com.lopesdasilva.trakt.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.jakewharton.trakt.entities.TvShow;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.Tasks.DownloadWatchlistEpisodes;
import com.lopesdasilva.trakt.Tasks.DownloadWatchlistShows;
import com.lopesdasilva.trakt.activities.ShowActivity;
import com.lopesdasilva.trakt.extras.UserChecker;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lopesdasilva on 04/06/13.
 */
public class WatchlistEpisodesFragment extends Fragment implements DownloadWatchlistEpisodes.onWatchlistEpisodesListTaskComplete {

    private View rootView;
    private GridView mGridView;
    private List<TvShow> mShowsListShowing = new LinkedList<TvShow>();
    private ServiceManager manager;
    private ShowsGridAdapter mAdapter;
    private DownloadWatchlistEpisodes mWatchlistShowsTask;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.showsall_fragment, container, false);


        manager = UserChecker.checkUserLogin(getActivity());
        Log.d("Trakt", "Watchlist Shows saveInstance: " + savedInstanceState);
        if (savedInstanceState == null) {

            mWatchlistShowsTask = new DownloadWatchlistEpisodes(this, getActivity(), manager);
            mWatchlistShowsTask.execute();
        } else {

            if (((List<TvShow>) savedInstanceState.get("shows")).size() != 0)
                updateView((List<TvShow>) savedInstanceState.get("shows"));
            else {

                mWatchlistShowsTask = new DownloadWatchlistEpisodes(this, getActivity(), manager);
                mWatchlistShowsTask.execute();
            }
        }

        return rootView;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("shows", (Serializable) mShowsListShowing);
    }


    public void updateView(List<TvShow> response) {
//        mShowsListShowing.clear();
        mShowsListShowing.addAll(response);

        if (getActivity() != null) {

            mGridView = (GridView) rootView.findViewById(R.id.gridViewShowsAll);
            mAdapter = new ShowsGridAdapter(getActivity(), mShowsListShowing);
            mGridView.setAdapter(mAdapter);

            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Bundle arguments = new Bundle();
//                    arguments.putString("show_imdb", mShowsListShowing.get(i).imdbId);

//                    Intent intent = new Intent(getActivity(), ShowActivity.class);
//                    intent.putExtras(arguments);
//
//                    startActivity(intent);
//                    getActivity().overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);


                    arguments.putSerializable("movie", mShowsListShowing.get(i));
                    WatchlistEpisodes watchlistEpisodes = new WatchlistEpisodes();
                    watchlistEpisodes.setArguments(arguments);
                    watchlistEpisodes.show(getFragmentManager(), "watchlist_episodes");

                }
            });



        }


    }

    @Override
    public void onWatchlistEpisodesListTaskComplete(List<TvShow> response) {
        updateView(response);
    }

    public class ShowsGridAdapter extends BaseAdapter {

        private final List<TvShow> mList;
        private final LayoutInflater inflater;

        public ShowsGridAdapter(Context context, List<TvShow> mList) {

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
                convertView = inflater.inflate(R.layout.library_shows_grid_item, parent, false);
            }

            AQuery aq = new AQuery(convertView);

            aq.id(R.id.imageViewShowsPoster).progress(R.id.progress).image(mList.get(position).images.poster, true, true, 100, R.drawable.poster, null, AQuery.FADE_IN);
            aq.id(R.id.textViewShowsShowTitle).text(mList.get(position).title);
            aq.id(R.id.textViewLibraryShowYear).text(mList.get(position).year + "");
            aq.id(R.id.imageViewShowsShowSeenTag).gone();
//            aq.id(R.id.textViewShowsShowNumberVotes).text(mList.get(position).ratings.votes + " votes");
//            aq.id(R.id.textViewShowsShowViewers).text(mList.get(position).watchers + " viewers");
//            if (mList.get(position).inCollection)
//                aq.id(R.id.imageViewLibraryCollectionTag).visible();
//            else
            aq.id(R.id.imageViewLibraryCollectionTag).gone();
            if (mList.get(position).watched)
                aq.id(R.id.imageViewShowsShowSeenTag).visible();
            else
                aq.id(R.id.imageViewShowsShowSeenTag).gone();


            return convertView;
        }
    }
}
