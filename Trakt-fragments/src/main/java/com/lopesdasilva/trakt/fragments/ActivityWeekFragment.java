package com.lopesdasilva.trakt.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.Activity;
import com.jakewharton.trakt.entities.ActivityItem;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.enumerations.ActivityAction;
import com.jakewharton.trakt.enumerations.ActivityType;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.Tasks.DownloadWeekActivity;
import com.lopesdasilva.trakt.activities.EpisodeActivity;
import com.lopesdasilva.trakt.activities.MovieActivity;
import com.lopesdasilva.trakt.activities.ShowActivity;
import com.lopesdasilva.trakt.extras.UserChecker;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersBaseAdapter;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private List<ActivityItem> mActivityItems;
    private long mStart_ts;
    private long mEnd_ts;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.calendarweek_fragment, container, false);
//        Log.d("Trakt", "OnCreateView");

        getActivity().getActionBar().setSubtitle(getResources().getString(R.string.UserActivity));


        mStart_ts = getArguments().getLong("start_ts");
        mEnd_ts = getArguments().getLong("end_ts");
        username = UserChecker.getUsername(getActivity());

        if (savedInstanceState == null) {
            Log.d("Trakt", "start_ts: "+mStart_ts+" end_ts: "+mEnd_ts);
            manager = UserChecker.checkUserLogin(getActivity());
            Log.d("Trakt", "Fragment Calendar Week Launched");

            Date start_ts= new Date();
            start_ts.setTime(start_ts.getTime()-(mStart_ts));

            Date end_ts= new Date();
            end_ts.setTime(end_ts.getTime()-(mEnd_ts));
            SimpleDateFormat format = new SimpleDateFormat("E MMM dd, yyyy");
               Log.d("Trakt it ","end: "+format.format(end_ts)+" start:"+format.format(start_ts));
            mTaskDownloadWeekCalendar = new DownloadWeekActivity(ActivityWeekFragment.this, getActivity(), manager, username, start_ts, end_ts);
            mTaskDownloadWeekCalendar.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else {
            Activity response = (Activity) savedInstanceState.get("activity");
            if (response!= null) {
                onWeekActivityInfoComplete(response);
            } else {
                manager = UserChecker.checkUserLogin(getActivity());
                Log.d("Trakt", "Fragment Calendar Week Launched");

                Date start_ts= new Date();
                start_ts.setTime(start_ts.getTime()-(mStart_ts));

                Date end_ts= new Date();
                end_ts.setTime(end_ts.getTime()-(mEnd_ts));

                mTaskDownloadWeekCalendar = new DownloadWeekActivity(ActivityWeekFragment.this, getActivity(), manager, username, start_ts, end_ts);
                mTaskDownloadWeekCalendar.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }


        return rootView;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("activity", (Serializable) mResponse);
    }


    @Override
    public void onWeekActivityInfoComplete(Activity response) {
        if (getActivity() != null) {
            Log.d("Trakt", "Download complete, response size: " + response.activity.size());
            mResponse = response;
            rootView.findViewById(R.id.progressBarCalendarWeek).setVisibility(View.GONE);

            mActivityItems = mResponse.activity;
            mListHeader = new ArrayList<Header>();
            String day = null;
            List<ActivityItem> mListActivityItems = null;
            for (ActivityItem activityItem : response.activity) {
                SimpleDateFormat format = new SimpleDateFormat("E MMM dd, yyyy");


                String compare_day = format.format(activityItem.timestamp);
                if (!compare_day.equals(day)) {

                    if (mListActivityItems != null) {
                        mListHeader.add(new Header(compare_day, mListActivityItems));
                    }
                    mListActivityItems = new LinkedList<ActivityItem>();
                    day = compare_day;

                }
                if (activityItem.action == ActivityAction.Seen && activityItem.type == ActivityType.Episode && activityItem.episode == null && activityItem.episodes != null) {
                    for (TvShowEpisode episode : activityItem.episodes) {
                        ActivityItem item = activityItem;
                        item.episodes = null;
                        item.episode = episode;
                        weekActivity.add(item);
                        mListActivityItems.add(item);
                    }

                } else {
                    weekActivity.add(activityItem);
                    mListActivityItems.add(activityItem);
                }
                if (response.activity.indexOf(activityItem) + 1 == response.activity.size()) {
                    mListHeader.add(new Header(compare_day, mListActivityItems));
                }
            }
            mAdapter = new WeekActivityAdapter(getActivity(), mListHeader, weekActivity);

            l = (StickyGridHeadersGridView) rootView.findViewById(R.id.listViewCalendar);
            l.setAdapter(mAdapter);
            //l.setEmptyView(rootView.findViewById(R.id.emptyView));
//        weekActivity.clear();
//        weekActivity.addAll(mResponse.activity);

            mAdapter.notifyDataSetChanged();
            l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Bundle arguments = new Bundle();
                    Intent intent;
                    switch (weekActivity.get(i).type) {
                        case Episode:
                            Log.d("Trakt", "Launching Episode Activity");
                            arguments = new Bundle();
                            arguments.putString("show_imdb", weekActivity.get(i).show.imdbId);
                            arguments.putInt("show_season", weekActivity.get(i).episode.season);
                            arguments.putInt("show_episode", weekActivity.get(i).episode.number);
                            intent = new Intent(getActivity(), EpisodeActivity.class);
                            intent.putExtras(arguments);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);

                            break;
                        case Show:
                            Log.d("Trakt it", "Launching Show Activity");

                            if(weekActivity.get(i).show.imdbId!=null && !"".equals(weekActivity.get(i).show.imdbId))
                                arguments.putString("show_imdb", weekActivity.get(i).show.imdbId);
                            else if(weekActivity.get(i).show.tvdbId!=null && !"".equals(weekActivity.get(i).show.tvdbId))
                                arguments.putString("show_imdb", weekActivity.get(i).show.tvdbId);
                            else
                                arguments.putString("show_imdb", weekActivity.get(i).show.title.replace(" ","-")+"-"+weekActivity.get(i).show.year);


                            intent = new Intent(getActivity(), ShowActivity.class);
                            intent.putExtras(arguments);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);

                            break;
                        case Movie:
                            Log.d("Trakt Fragments", "Launching Movie Activity");

                            if(weekActivity.get(i).movie.imdbId!=null && !"".equals(weekActivity.get(i).movie.imdbId))
                                arguments.putString("movie_imdb", weekActivity.get(i).movie.imdbId);
                            else if(weekActivity.get(i).movie.tmdbId!=null && !"".equals(weekActivity.get(i).movie.tmdbId))
                                arguments.putString("movie_imdb", weekActivity.get(i).movie.tmdbId);
                            else
                                arguments.putString("movie_imdb", weekActivity.get(i).movie.title.replace(" ","-")+"-"+weekActivity.get(i).movie.year);

                            intent = new Intent(getActivity(), MovieActivity.class);
                            intent.putExtras(arguments);

                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);

                            break;
                    }
                }
            });
        }
    }


    public class WeekActivityAdapter extends BaseAdapter implements StickyGridHeadersBaseAdapter {

        private final List<ActivityItem> mListActivity;
        private final ArrayList<Header> mListHeaders;
        private LayoutInflater inflater;

        public WeekActivityAdapter(Context context, ArrayList<Header> mListHeader, List<ActivityItem> mList) {
            this.mListActivity = mList;
            this.mListHeaders = mListHeader;
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

            switch (mListActivity.get(position).type) {

                case All:
                    break;
                case Episode:
                    aq.id(R.id.imageViewCalendarEpisodeBackdrop).image(mListActivity.get(position).episode.images.screen, false, true, 200, R.drawable.episode_backdrop_small, aq.getCachedImage(R.drawable.episode_backdrop_small), AQuery.FADE_IN);

                    aq.id(R.id.textViewCalendarShowTitle).text(mListActivity.get(position).show.title);
                    aq.id(R.id.textViewCalendarEpisodeSeasonNumberAndEpisodeNumber).text("S" + mListActivity.get(position).episode.season + "E" + mListActivity.get(position).episode.number);
                    aq.id(R.id.textViewCalendarEpisodeTitle).text(mListActivity.get(position).episode.title);

                    if (mListActivity.get(position).episode.inCollection == null || !mListActivity.get(position).episode.inCollection)
                        aq.id(R.id.imageViewCalendarEpisodeCollectionTag).gone();
                    else
                        aq.id(R.id.imageViewCalendarEpisodeCollectionTag).visible();

                    if (mListActivity.get(position).episode.inWatchlist == null || !mListActivity.get(position).episode.inWatchlist)
                        aq.id(R.id.imageViewCalendarEpisodeWatchlistTag).gone();
                    else
                        aq.id(R.id.imageViewCalendarEpisodeWatchlistTag).visible();


                    if (mListActivity.get(position).action == ActivityAction.Seen) {
                        aq.id(R.id.imageViewCalendarEpisodeSeenTag).visible();
                        aq.id(R.id.imageViewCalendarEpisodeCheckInTag).gone();
                    } else if (mListActivity.get(position).action == ActivityAction.Checkin) {
                        aq.id(R.id.imageViewCalendarEpisodeCheckInTag).visible();
                        aq.id(R.id.imageViewCalendarEpisodeSeenTag).gone();
                    } else if (mListActivity.get(position).action == ActivityAction.Scrobble){
                        aq.id(R.id.imageViewCalendarEpisodeScrobbleTag).visible();
                        aq.id(R.id.imageViewCalendarEpisodeCheckInTag).gone();
                        aq.id(R.id.imageViewCalendarEpisodeSeenTag).gone();
                    }


                    if (mListActivity.get(position).episode.ratingAdvanced==null || "0".equals(mListActivity.get(position).episode.ratingAdvanced)) {
                        aq.id(R.id.relativeLayoutAdvanceRating).gone();
                    } else if ("1".equals(mListActivity.get(position).episode.ratingAdvanced)) {
                        aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_1);
                        aq.id(R.id.relativeLayoutAdvanceRating).visible();
                        aq.id(R.id.textViewEpisodeRatingAdvance).text(mListActivity.get(position).episode.ratingAdvanced);
                    } else if ("2".equals(mListActivity.get(position).episode.ratingAdvanced)) {

                        aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_2);
                        aq.id(R.id.relativeLayoutAdvanceRating).visible();
                        aq.id(R.id.textViewEpisodeRatingAdvance).text(mListActivity.get(position).episode.ratingAdvanced);
                    } else if ("3".equals(mListActivity.get(position).episode.ratingAdvanced)) {

                        aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_3);
                        aq.id(R.id.relativeLayoutAdvanceRating).visible();
                        aq.id(R.id.textViewEpisodeRatingAdvance).text(mListActivity.get(position).episode.ratingAdvanced);
                    } else if ("4".equals(mListActivity.get(position).episode.ratingAdvanced)) {

                        aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_4);
                        aq.id(R.id.relativeLayoutAdvanceRating).visible();
                        aq.id(R.id.textViewMovieRatingAdvance).text(mListActivity.get(position).episode.ratingAdvanced);
                    } else if ("5".equals(mListActivity.get(position).episode.ratingAdvanced)) {

                        aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_5);
                        aq.id(R.id.relativeLayoutAdvanceRating).visible();
                        aq.id(R.id.textViewEpisodeRatingAdvance).text(mListActivity.get(position).episode.ratingAdvanced);
                    } else if ("6".equals(mListActivity.get(position).episode.ratingAdvanced)) {

                        aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_6);
                        aq.id(R.id.relativeLayoutAdvanceRating).visible();
                        aq.id(R.id.textViewEpisodeRatingAdvance).text(mListActivity.get(position).episode.ratingAdvanced);
                    } else if ("7".equals(mListActivity.get(position).episode.ratingAdvanced)) {

                        aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_7);
                        aq.id(R.id.relativeLayoutAdvanceRating).visible();
                        aq.id(R.id.textViewEpisodeRatingAdvance).text(mListActivity.get(position).episode.ratingAdvanced);
                    } else if ("8".equals(mListActivity.get(position).episode.ratingAdvanced)) {

                        aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_8);
                        aq.id(R.id.relativeLayoutAdvanceRating).visible();
                        aq.id(R.id.textViewEpisodeRatingAdvance).text(mListActivity.get(position).episode.ratingAdvanced);
                    } else if ("9".equals(mListActivity.get(position).episode.ratingAdvanced)) {

                        aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_9);
                        aq.id(R.id.relativeLayoutAdvanceRating).visible();
                        aq.id(R.id.textViewShowRatingAdvance).text(mListActivity.get(position).episode.ratingAdvanced);
                    } else if ("10".equals(mListActivity.get(position).episode.ratingAdvanced)) {

                        aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_10);
                        aq.id(R.id.relativeLayoutAdvanceRating).visible();
                        aq.id(R.id.textViewEpisodeRatingAdvance).text(mListActivity.get(position).episode.ratingAdvanced);
                        aq.id(R.id.textViewEpisodeRatingAdvance).margin(0, 0, 1, 0);
                    }

                    break;
                case Show:
                    break;
                case Movie:
                    aq.id(R.id.imageViewCalendarEpisodeBackdrop).image(mListActivity.get(position).movie.images.fanart, false, true, 200, R.drawable.episode_backdrop_small, aq.getCachedImage(R.drawable.episode_backdrop_small), AQuery.FADE_IN);
                    aq.id(R.id.textViewCalendarShowTitle).text(mListActivity.get(position).movie.title + " (" + mListActivity.get(position).movie.year + ")");
                    aq.id(R.id.textViewCalendarEpisodeSeasonNumberAndEpisodeNumber).gone();
                    aq.id(R.id.textViewCalendarEpisodeTitle).gone();


                    if (mListActivity.get(position).movie.inCollection == null || !mListActivity.get(position).movie.inCollection)
                        aq.id(R.id.imageViewCalendarEpisodeCollectionTag).gone();
                    else
                        aq.id(R.id.imageViewCalendarEpisodeCollectionTag).visible();

                    if (mListActivity.get(position).movie.inWatchlist == null || !mListActivity.get(position).movie.inWatchlist)
                        aq.id(R.id.imageViewCalendarEpisodeWatchlistTag).gone();
                    else
                        aq.id(R.id.imageViewCalendarEpisodeWatchlistTag).visible();


                    if (mListActivity.get(position).action == ActivityAction.Seen) {
                        aq.id(R.id.imageViewCalendarEpisodeSeenTag).visible();
                        aq.id(R.id.imageViewCalendarEpisodeCheckInTag).gone();
                    } else if (mListActivity.get(position).action == ActivityAction.Checkin) {
                        aq.id(R.id.imageViewCalendarEpisodeCheckInTag).visible();
                        aq.id(R.id.imageViewCalendarEpisodeSeenTag).gone();
                    } else {
                        aq.id(R.id.imageViewCalendarEpisodeCheckInTag).gone();
                        aq.id(R.id.imageViewCalendarEpisodeSeenTag).gone();
                    }


                        if (mListActivity.get(position).movie.ratingAdvanced==null || "0".equals(mListActivity.get(position).movie.ratingAdvanced)) {
                            aq.id(R.id.relativeLayoutAdvanceRating).gone();
                        } else if ("1".equals(mListActivity.get(position).movie.ratingAdvanced)) {
                            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_1);
                            aq.id(R.id.relativeLayoutAdvanceRating).visible();
                            aq.id(R.id.textViewEpisodeRatingAdvance).text(mListActivity.get(position).movie.ratingAdvanced);
                        } else if ("2".equals(mListActivity.get(position).movie.ratingAdvanced)) {

                            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_2);
                            aq.id(R.id.relativeLayoutAdvanceRating).visible();
                            aq.id(R.id.textViewEpisodeRatingAdvance).text(mListActivity.get(position).movie.ratingAdvanced);
                        } else if ("3".equals(mListActivity.get(position).movie.ratingAdvanced)) {

                            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_3);
                            aq.id(R.id.relativeLayoutAdvanceRating).visible();
                            aq.id(R.id.textViewEpisodeRatingAdvance).text(mListActivity.get(position).movie.ratingAdvanced);
                        } else if ("4".equals(mListActivity.get(position).movie.ratingAdvanced)) {

                            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_4);
                            aq.id(R.id.relativeLayoutAdvanceRating).visible();
                            aq.id(R.id.textViewMovieRatingAdvance).text(mListActivity.get(position).movie.ratingAdvanced);
                        } else if ("5".equals(mListActivity.get(position).movie.ratingAdvanced)) {

                            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_5);
                            aq.id(R.id.relativeLayoutAdvanceRating).visible();
                            aq.id(R.id.textViewEpisodeRatingAdvance).text(mListActivity.get(position).movie.ratingAdvanced);
                        } else if ("6".equals(mListActivity.get(position).movie.ratingAdvanced)) {

                            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_6);
                            aq.id(R.id.relativeLayoutAdvanceRating).visible();
                            aq.id(R.id.textViewEpisodeRatingAdvance).text(mListActivity.get(position).movie.ratingAdvanced);
                        } else if ("7".equals(mListActivity.get(position).movie.ratingAdvanced)) {

                            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_7);
                            aq.id(R.id.relativeLayoutAdvanceRating).visible();
                            aq.id(R.id.textViewEpisodeRatingAdvance).text(mListActivity.get(position).movie.ratingAdvanced);
                        } else if ("8".equals(mListActivity.get(position).movie.ratingAdvanced)) {

                            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_8);
                            aq.id(R.id.relativeLayoutAdvanceRating).visible();
                            aq.id(R.id.textViewEpisodeRatingAdvance).text(mListActivity.get(position).movie.ratingAdvanced);
                        } else if ("9".equals(mListActivity.get(position).movie.ratingAdvanced)) {

                            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_9);
                            aq.id(R.id.relativeLayoutAdvanceRating).visible();
                            aq.id(R.id.textViewShowRatingAdvance).text(mListActivity.get(position).movie.ratingAdvanced);
                        } else if ("10".equals(mListActivity.get(position).movie.ratingAdvanced)) {

                            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_10);
                            aq.id(R.id.relativeLayoutAdvanceRating).visible();
                            aq.id(R.id.textViewEpisodeRatingAdvance).text(mListActivity.get(position).movie.ratingAdvanced);
                            aq.id(R.id.textViewEpisodeRatingAdvance).margin(0, 0, 1, 0);
                        }


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

        @Override
        public int getItemViewType(int position) {
            return mListActivity.get(position).action.ordinal();
        }

        @Override
        public int getViewTypeCount() {
            return 11;
        }

    }

    protected class Header {
        String name;
        List<ActivityItem> activityItems;

        public Header(String day, List<ActivityItem> mListActivityItems) {
            this.name = day;
            this.activityItems = mListActivityItems;
        }
    }
}
