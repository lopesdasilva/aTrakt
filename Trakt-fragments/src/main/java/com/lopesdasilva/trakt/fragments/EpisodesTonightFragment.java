package com.lopesdasilva.trakt.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.CalendarDate;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.Tasks.DownloadDayCalendar;
import com.lopesdasilva.trakt.activities.EpisodeActivity;
import com.lopesdasilva.trakt.extras.UserChecker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lopesdasilva on 31/05/13.
 */
public class EpisodesTonightFragment extends ListFragment implements DownloadDayCalendar.OnDayCalendarTaskCompleted {


    private ServiceManager manager;
    List<CalendarDate.CalendarTvShowEpisode> mListepisodes = new LinkedList<CalendarDate.CalendarTvShowEpisode>();
    private EpisodeTonightAdapter mAdapter;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setRetainInstance(true);

        if (savedInstanceState == null) {
            manager = UserChecker.checkUserLogin(getActivity());


            new DownloadDayCalendar(this, manager, new Date()).execute();


        }

        getActivity().getActionBar().setTitle("Shows On tonight");
        SimpleDateFormat dateFormat = new SimpleDateFormat("E d MMM");
        getActivity().getActionBar().setSubtitle(dateFormat.format(new Date()));
        mAdapter = new EpisodeTonightAdapter(getActivity(), mListepisodes);
        setListAdapter(mAdapter);
        getListView().setDivider(null);
        getListView().setBackgroundColor(getResources().getColor(R.color.calendar_background));


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

        if(response.size()!=0){
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
                convertView = inflater.inflate(R.layout.episodes_tonight_item, parent, false);
            }
            AQuery aq = new AQuery(convertView);
            aq.id(R.id.imageViewEpisodeTonightScreen).image(mListEpisodes.get(position).episode.images.screen,false,true);
            aq.id(R.id.textViewEpisodeTonightShowTitle).text(mListEpisodes.get(position).show.title);
            aq.id(R.id.textViewEpisodeTonightSeasonEpisodeNumber).text("S" + mListepisodes.get(position).episode.season + "E" + mListepisodes.get(position).episode.number);
            aq.id(R.id.textViewEpisodeTonightEpisodeTitle).text(mListEpisodes.get(position).episode.title);
            if (mListEpisodes.get(position).episode.overview!=null) {
                aq.id(R.id.textViewEpisodeTonightEpisodeOverview).text(mListEpisodes.get(position).episode.overview).visible();
                aq.id(R.id.viewEpisodeTonightLine).visible();
            } else {
                aq.id(R.id.textViewEpisodeTonightEpisodeOverview).gone();
                aq.id(R.id.viewEpisodeTonightLine).gone();
            }
            aq.id(R.id.textViewEpisodeTonightShowNetwork).text(mListEpisodes.get(position).show.network);
            aq.id(R.id.textViewEpisodeTonightRatingsPercentage).text(mListEpisodes.get(position).episode.ratings.percentage+"%");

            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
            aq.id(R.id.textViewEpisodeTonightEpisodeAirDate).text(dateFormat.format(mListEpisodes.get(position).episode.firstAired));
            return convertView;
        }
    }
}
