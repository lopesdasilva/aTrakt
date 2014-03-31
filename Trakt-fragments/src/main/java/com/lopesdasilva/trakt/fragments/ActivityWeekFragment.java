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


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.calendarweek_fragment, container, false);
//        Log.d("Trakt", "OnCreateView");

        mDate = getArguments().getInt("calendardate");

        username=UserChecker.getUsername(getActivity());

        if (savedInstanceState == null) {
            manager = UserChecker.checkUserLogin(getActivity());
            Log.d("Trakt", "Fragment Calendar Week Launched");
            Date d = new Date();
            if (mDate != -1) {
                d.setTime(d.getTime() + (mDate));
            }

            mTaskDownloadWeekCalendar = new DownloadWeekActivity(ActivityWeekFragment.this, getActivity(), manager, username,d);
            mTaskDownloadWeekCalendar.execute();
        } else {
//            init();
            Activity response = (Activity) savedInstanceState.get("activity");
            if (response!= null) {
                onWeekActivityInfoComplete(response);
            } else {
                manager = UserChecker.checkUserLogin(getActivity());
                Log.d("Trakt", "Fragment Calendar Week Launched");
                Date d = new Date();
                if (mDate != -1) {
                    d.setTime(d.getTime() + (mDate));
                }

                mTaskDownloadWeekCalendar = new DownloadWeekActivity(ActivityWeekFragment.this, getActivity(), manager, username,d);
                mTaskDownloadWeekCalendar.execute();
            }
        }


        return rootView;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("activity", (Serializable) mResponse);
    }



//    private void init() {
//
//        l = (StickyGridHeadersGridView) rootView.findViewById(R.id.listViewCalendar);
//        mAdapter = new WeekActivityAdapter(getActivity(), weekActivity);
//        l.setAdapter(mAdapter);
//
//
//        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//
//                Bundle arguments = new Bundle();
//                arguments.putString("show_imdb", weekActivity.get(position).show.imdbId);
//                arguments.putInt("show_season", weekActivity.get(position).episode.season);
//                arguments.putInt("show_episode", weekActivity.get(position).episode.number);
//                Intent i = new Intent(getActivity(), EpisodeActivity.class);
//                i.putExtras(arguments);
//
//                startActivity(i);
//                getActivity().overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);
//
//
//            }
//        });
//        l.setLongClickable(true);
//        l.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setTitle(weekActivity.get(position).show.title +" - S"+weekActivity.get(position).episode.season+"E"+weekActivity.get(position).episode.number);
//                String[] options = new String[4];
//                if (weekActivity.get(position).episode.rating != null) {
//
//                    switch (weekActivity.get(position).episode.rating) {
//                        case Love:
//                            options[1] = "Remove Rating";
//                            options[2] = "Mark as Hated";
//                            break;
//                        case Hate:
//                            options[2] = "Remove Rating";
//                            options[1] = "Mark as Loved";
//                            break;
//                    }
//                } else {
//                    options[1] = "Mark as Loved";
//                    options[2] = "Mark as Hated";
//                }
//                if (weekActivity.get(position).episode.watched)
//                    options[0] = "Mark as unwatched";
//                else
//                    options[0] = "Mark as watched";
//
//
//                if (weekActivity.get(position).episode.inWatchlist)
//                    options[3] = "Remove from watchlist";
//                else
//                    options[3] = "Add to watchlist";
//
//                //options[4] = "Hide this movie";
//
//
//                builder.setItems(options, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int item_clicked) {
//
//                        switch (item_clicked) {
//                            case 0:
//                                new MarkEpisodeSeenUnseen(getActivity(), ActivityWeekFragment.this, manager, weekActivity.get(position).show, weekActivity.get(position).episode, position).execute();
//                                break;
//                            case 1:
//                                if (weekActivity.get(position).episode.rating != null)
//                                    new UnrateEpisode(getActivity(),ActivityWeekFragment.this,manager,weekActivity.get(position).show,weekActivity.get(position).episode,position).execute();
//                                else
//                                new RateEpisodeLove(getActivity(),ActivityWeekFragment.this,manager,weekActivity.get(position).show,weekActivity.get(position).episode,position).execute();
//
//                                break;
//                            case 2:
//                                if (weekActivity.get(position).episode.rating != null)
//                                    new UnrateEpisode(getActivity(),ActivityWeekFragment.this,manager,weekActivity.get(position).show,weekActivity.get(position).episode,position).execute();
//                                else
//                                new RateEpisodeHate(getActivity(),ActivityWeekFragment.this,manager,weekActivity.get(position).show,weekActivity.get(position).episode,position).execute();
//
//                                break;
//                            case 3:
//                                new EpisodeWatchlistUnWatchlist(getActivity(), ActivityWeekFragment.this, manager, weekActivity.get(position).show, weekActivity.get(position).episode, position).execute();
//                                break;
//                        }
//                    }
//                }
//                );
//                AlertDialog dialog = builder.create();
//                dialog.show();
//                return true;
//            }
//        });
//
//    }



    @Override
    public void onWeekActivityInfoComplete(Activity response) {
        mResponse=response;
        rootView.findViewById(R.id.progressBarCalendarWeek).setVisibility(View.GONE);
        mAdapter = new WeekActivityAdapter(getActivity(), weekActivity);
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
        private LayoutInflater inflater;

        public WeekActivityAdapter(Context context, List<ActivityItem> mList) {
            this.mListActivity = mList;
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
            return mListActivity.get(i).episodes.size();
        }

        @Override
        public int getNumHeaders() {
            return mListActivity.size();
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.calendarweek_item, parent, false);
            }
            AQuery aq = new AQuery(convertView);
            SimpleDateFormat format = new SimpleDateFormat("E MMM dd, yyyy");
            aq.id(R.id.textViewCalendarWeekDay).text(format.format(mListActivity.get(position).timestamp) + "");

            return convertView;
        }
    }
}
