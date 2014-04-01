package com.lopesdasilva.trakt.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.Activity;
import com.jakewharton.trakt.entities.ActivityItem;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.Tasks.DownloadWeekActivity;
import com.lopesdasilva.trakt.extras.UserChecker;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersBaseAdapter;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lopesdasilva on 27/05/13.
 */
public class ActivityWeekFragment extends Fragment implements DownloadWeekActivity.OnWeekActivityTaskCompleted {

    private ServiceManager manager;
    private DownloadWeekActivity mTaskDownloadWeekCalendar;
    private List<ActivityItem> weekActivity = new LinkedList<ActivityItem>();
    private int mDate = 0;
    private WeekActivityAdapter mAdapter;

//    private List<CalendarDate.CalendarTvShowEpisode> lista = new LinkedList<CalendarDate.CalendarTvShowEpisode>();
    private View rootView;
    private StickyGridHeadersGridView l;
    private String username;
    private Activity mResponse;
    private ArrayList<Header> mListHeader;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.calendarweek_fragment, container, false);
//        Log.d("Trakt", "OnCreateView");

        mDate = getArguments().getInt("calendardate");

        username=UserChecker.getUsername(getActivity());

        if (savedInstanceState == null) {
            manager = UserChecker.checkUserLogin(getActivity());
            Log.d("Trakt", "Fragment Calendar Week Launched");
            Date d = new Date();
//            if (mDate != -1) {
//                d.setTime(d.getTime() + (mDate));
//            }
            d.setTime(d.getTime() + (-604800000));
            mTaskDownloadWeekCalendar = new DownloadWeekActivity(ActivityWeekFragment.this, getActivity(), manager, "lopesdasilva",new Date(),d);
            mTaskDownloadWeekCalendar.execute();
        }
//        else {
////            init();
//            Activity response = (Activity) savedInstanceState.get("activity");
//            if (response!= null) {
//                onWeekActivityInfoComplete(response);
//            } else {
//                manager = UserChecker.checkUserLogin(getActivity());
//                Log.d("Trakt", "Fragment Calendar Week Launched");
//                Date d = new Date();
//                if (mDate != -1) {
//                    d.setTime(d.getTime() + (mDate));
//                }
//
//                mTaskDownloadWeekCalendar = new DownloadWeekActivity(ActivityWeekFragment.this, getActivity(), manager, username,d,d);
//                mTaskDownloadWeekCalendar.execute();
//            }
//        }


        return rootView;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("activity", (Serializable) mResponse);
    }


    @Override
    public void onWeekActivityInfoComplete(Activity response) {
        mResponse=response;
        rootView.findViewById(R.id.progressBarCalendarWeek).setVisibility(View.GONE);
        mAdapter = new WeekActivityAdapter(getActivity(), mListHeader,weekActivity);

         mListHeader=new ArrayList<Header>();
        String day=null;
        List<ActivityItem> mListActivityItems= null;
        for (ActivityItem activityItem: response.activity) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("E MMM dd, yyyy");

            String compare_day = format.format(activityItem.timestamp);
            if (!compare_day.equals(day)) {

                if (mListActivityItems != null) {
                    mListHeader.add(new Header(day,mListActivityItems));
                }
                mListActivityItems=new LinkedList<ActivityItem>();
                day = compare_day;
            }
            mListActivityItems.add(activityItem);
        }


        l = (StickyGridHeadersGridView) rootView.findViewById(R.id.listViewCalendar);
        l.setAdapter(mAdapter);
        //l.setEmptyView(rootView.findViewById(R.id.emptyView));
        weekActivity.clear();
        weekActivity.addAll(response.activity);

        mAdapter.notifyDataSetChanged();
        Log.d("Trakt", "Download complete, lista size: " + weekActivity.size());
    }


    public class WeekActivityAdapter extends BaseAdapter implements StickyGridHeadersBaseAdapter {

        private final List<ActivityItem> mListActivity;
        private final ArrayList<Header> mListHeaders;
        private LayoutInflater inflater;

        public WeekActivityAdapter(Context context, ArrayList<Header> mListHeader, List<ActivityItem> mList) {
            this.mListActivity = mList;
            this.mListHeaders=mListHeader;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return weekActivity.size();
        }

        @Override
        public ActivityItem getItem(int i) {
            return weekActivity.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.calendarweek_item_episode, parent, false);
            }



            AQuery aq = new AQuery(convertView);

            switch (mListActivity.get(position).type){

                case All:
                    break;
                case Episode:
                    aq.id(R.id.imageViewCalendarEpisodeBackdrop).image(mListActivity.get(position).episode.images.screen, false, true, 200, R.drawable.episode_backdrop_small,aq.getCachedImage(R.drawable.episode_backdrop_small),AQuery.FADE_IN);
                    aq.id(R.id.textViewCalendarShowTitle).text(mListActivity.get(position).show.title);
                    aq.id(R.id.textViewCalendarEpisodeSeasonNumberAndEpisodeNumber).text("S" + mListActivity.get(position).episode.season + "E" + mListActivity.get(position).episode.number);
                    aq.id(R.id.textViewCalendarEpisodeTitle).text(mListActivity.get(position).episode.title);

                   /* if (mListActivity.get(position).episode.inCollection)
                        aq.id(R.id.imageViewCalendarEpisodeCollectionTag).visible();
                    else
                        aq.id(R.id.imageViewCalendarEpisodeCollectionTag).gone();

                    if (mListActivity.get(position).episode.inWatchlist)
                        aq.id(R.id.imageViewCalendarEpisodeWatchlistTag).visible();
                    else
                        aq.id(R.id.imageViewCalendarEpisodeWatchlistTag).gone();


                    if (lista.get(position).episode.watched || mListActivity.get(position).episode.plays>0)
                        aq.id(R.id.imageViewCalendarEpisodeSeenTag).visible();
                    else
                        aq.id(R.id.imageViewCalendarEpisodeSeenTag).gone();
                    if (mListActivity.get(position).episode.rating != null) {
                        switch (lista.get(position).episode.rating) {

                            case Love:
                                aq.id(R.id.imageViewCalendarEpisodeLoveTag).visible();
                                aq.id(R.id.imageViewCalendarEpisodeHateTag).gone();
                                break;
                            case Hate:
                                aq.id(R.id.imageViewCalendarEpisodeLoveTag).gone();
                                aq.id(R.id.imageViewCalendarEpisodeHateTag).visible();
                                break;

                        }
                    }else{
                        aq.id(R.id.imageViewCalendarEpisodeLoveTag).gone();
                        aq.id(R.id.imageViewCalendarEpisodeHateTag).gone();
                    }
*/

                    break;
                case Show:
                    break;
                case Movie:
//                    aq.id(R.id.imageViewCalendarEpisodeBackdrop).image(mListActivity.get(position).movie.images.poster, false, true, 200, R.drawable.episode_backdrop_small,aq.getCachedImage(R.drawable.episode_backdrop_small),AQuery.FADE_IN);
                    aq.id(R.id.textViewCalendarShowTitle).text(mListActivity.get(position).movie.title);
//                    aq.id(R.id.textViewCalendarEpisodeSeasonNumberAndEpisodeNumber).text("S" + mListActivity.get(position).episode.season + "E" + mListActivity.get(position).episode.number);
//                    aq.id(R.id.textViewCalendarEpisodeTitle).text(mListActivity.get(position).episode.title);
                    break;
                case List:
                    break;
            }



            aq.recycle(convertView);
            return convertView;
        }

        @Override
        public int getCountForHeader(int i) {
            return mListHeaders.get(i).activityItems.size();
        }

        @Override
        public int getNumHeaders() {
            return mListHeaders.size();
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.calendarweek_item, parent, false);
            }
            AQuery aq = new AQuery(convertView);
            aq.id(R.id.textViewCalendarWeekDay).text(mListHeaders.get(position).name + "");

            return convertView;
        }
    }

    protected class Header{
        String name;
        List<ActivityItem> activityItems;

        public Header(String day, List<ActivityItem> mListActivityItems) {
            this.name=day;
            this.activityItems=mListActivityItems;
        }
    }
}
