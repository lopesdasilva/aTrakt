package com.lopesdasilva.trakt.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.CalendarDate;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.Tasks.DownloadDayCalendar;
import com.lopesdasilva.trakt.activities.EpisodeActivity;
import com.lopesdasilva.trakt.activities.EpisodesTonightActivity;
import com.lopesdasilva.trakt.extras.UserChecker;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lopesdasilva on 31/05/13.
 */
public class DrawerEpisodesTonightFragment extends ListFragment implements DownloadDayCalendar.OnDayCalendarTaskCompleted {


    private ServiceManager manager;
    List<CalendarDate.CalendarTvShowEpisode> mListepisodes = new LinkedList<CalendarDate.CalendarTvShowEpisode>();
    private EpisodeTonightAdapter mAdapter;
    private View rootView;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setRetainInstance(true);


        mAdapter = new EpisodeTonightAdapter(getActivity(), mListepisodes);
        setListAdapter(mAdapter);
        getListView().setDivider(getResources().getDrawable(R.drawable.divider));
//        getListView().setBackgroundColor(getResources().getColor(R.color.calendar_background));
        LinearLayout l = new LinearLayout(getActivity());

        TextView t = new TextView(getActivity());
        t.setText("On tonight");
        l.addView(t);
//        getListView().addHeaderView(l);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.drawer_episodes_tonight, null);
        view.findViewById(R.id.textViewOnTonight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(), EpisodesTonightActivity.class));

            }
        });
        if (savedInstanceState == null) {
            manager = UserChecker.checkUserLogin(getActivity());
            new DownloadDayCalendar(this, manager, new Date()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mListepisodes = (List<CalendarDate.CalendarTvShowEpisode>) savedInstanceState.get("episodesTonight");
            mAdapter.notifyDataSetChanged();
        }


        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("episodesTonight", (Serializable) mListepisodes);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Bundle arguments = new Bundle();
        arguments.putString("show_imdb", mListepisodes.get(position).show.imdbId);
        arguments.putInt("show_season", mListepisodes.get(position).episode.season);
        arguments.putInt("show_episode", mListepisodes.get(position).episode.number);
        Intent i = new Intent(getActivity(), EpisodeActivity.class);
        i.putExtras(arguments);

        startActivity(i);
        getActivity().overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);


    }

    @Override
    public void OnDayCalendarTaskCompleted(List<CalendarDate> response) {

        if (response.size() != 0) {
            mListepisodes.clear();
            mListepisodes.addAll(response.get(0).episodes);
            mAdapter.notifyDataSetChanged();
        }
    }

    public class EpisodeTonightAdapter extends BaseAdapter {

        private final List<CalendarDate.CalendarTvShowEpisode> mListEpisodes;
        private final LayoutInflater inflater;

        public EpisodeTonightAdapter(Context context, List<CalendarDate.CalendarTvShowEpisode> mListepisodes) {
            this.mListEpisodes = mListepisodes;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mListEpisodes.size();
        }

        @Override
        public CalendarDate.CalendarTvShowEpisode getItem(int i) {
            return mListEpisodes.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.drawer_episodes_tonight_item, parent, false);
            }
            AQuery aq = new AQuery(convertView);

            aq.id(R.id.imageViewDrawerEpisodeScreen).image(mListEpisodes.get(position).episode.images.screen, true, true, 0, 0, null, AQuery.FADE_IN);
            aq.id(R.id.textViewDrawerEpisodeTitle).text(mListEpisodes.get(position).episode.title);
            aq.id(R.id.textViewDrawerEpisodeNumber).text("S" + mListEpisodes.get(position).episode.season + "E" + mListEpisodes.get(position).episode.number);
            aq.id(R.id.textViewDrawerEpisodeDate).text(mListEpisodes.get(position).show.title);
            aq.id(R.id.textViewDrawerEpisodeNetwork).text(mListEpisodes.get(position).show.network);

            if(mListEpisodes.get(position).episode.watched!=null) {
                if (mListEpisodes.get(position).episode.watched)
                    aq.id(R.id.imageViewDrawerEpisodeSeenTag).visible();
                else
                    aq.id(R.id.imageViewDrawerEpisodeSeenTag).gone();
            }
            return convertView;
        }
    }
}
