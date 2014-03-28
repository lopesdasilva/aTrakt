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

import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.CalendarDate;
import com.jakewharton.trakt.entities.RatingResponse;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.Tasks.*;
import com.lopesdasilva.trakt.Tasks.EpisodeWatchlistUnWatchlist;
import com.lopesdasilva.trakt.activities.EpisodeActivity;
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
public class CalendarWeekFragment extends Fragment implements DownloadWeekCalendar.OnWeekTaskCompleted, MarkEpisodeSeenUnseen.OnMarkSeenUnseenCompleted, EpisodeWatchlistUnWatchlist.WatchlistUnWatchlistCompleted, RateEpisodeHate.OnMarkEpisodeHateCompleted, RateEpisodeLove.OnMarkEpisodeLoveCompleted, UnrateEpisode.OnMarkEpisodeNoneCompleted {

    private ServiceManager manager;
    private DownloadWeekCalendar mTaskDownloadWeekCalendar;
    private List<CalendarDate> weekCalendar = new LinkedList<CalendarDate>();
    private int mDate = 0;
    private WeekCalendarAdapter mAdapter;

    private List<CalendarDate.CalendarTvShowEpisode> lista = new LinkedList<CalendarDate.CalendarTvShowEpisode>();
    private View rootView;
    private StickyGridHeadersGridView l;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.calendarweek_fragment, container, false);
//        Log.d("Trakt", "OnCreateView");

        mDate = getArguments().getInt("calendardate");

        if (savedInstanceState == null) {
            manager = UserChecker.checkUserLogin(getActivity());
            Log.d("Trakt", "Fragment Calendar Week Launched");
            Date d = new Date();
            if (mDate != -1) {
                d.setTime(d.getTime() + (mDate));
            }

            mTaskDownloadWeekCalendar = new DownloadWeekCalendar(this, getActivity(), manager, d);
            mTaskDownloadWeekCalendar.execute();
        } else {
            init();
            List<CalendarDate> response = (List<CalendarDate>) savedInstanceState.get("calendar");
            if (response.size() != 0) {
                onWeekInfoComplete(response);
            } else {
                manager = UserChecker.checkUserLogin(getActivity());
                Log.d("Trakt", "Fragment Calendar Week Launched");
                Date d = new Date();
                if (mDate != -1) {
                    d.setTime(d.getTime() + (mDate));
                }

                mTaskDownloadWeekCalendar = new DownloadWeekCalendar(this, getActivity(), manager, d);
                mTaskDownloadWeekCalendar.execute();
            }
        }


        return rootView;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("calendar", (Serializable) weekCalendar);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//
//        Log.d("Trakt","Activity Created lista size: "+lista.size());
//
//        setRetainInstance(true);
        init();

//
    }

    private void init() {

        l = (StickyGridHeadersGridView) rootView.findViewById(R.id.listViewCalendar);
        mAdapter = new WeekCalendarAdapter(getActivity(), weekCalendar, lista);
        l.setAdapter(mAdapter);


        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Bundle arguments = new Bundle();
                arguments.putString("show_imdb", lista.get(position).show.imdbId);
                arguments.putInt("show_season", lista.get(position).episode.season);
                arguments.putInt("show_episode", lista.get(position).episode.number);
                Intent i = new Intent(getActivity(), EpisodeActivity.class);
                i.putExtras(arguments);

                startActivity(i);
                getActivity().overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);


            }
        });
        l.setLongClickable(true);
        l.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(lista.get(position).show.title +" - S"+lista.get(position).episode.season+"E"+lista.get(position).episode.number);
                String[] options = new String[4];
                if (lista.get(position).episode.rating != null) {

                    switch (lista.get(position).episode.rating) {
                        case Love:
                            options[1] = "Remove Rating";
                            options[2] = "Mark as Hated";
                            break;
                        case Hate:
                            options[2] = "Remove Rating";
                            options[1] = "Mark as Loved";
                            break;
                    }
                } else {
                    options[1] = "Mark as Loved";
                    options[2] = "Mark as Hated";
                }
                if (lista.get(position).episode.watched)
                    options[0] = "Mark as unwatched";
                else
                    options[0] = "Mark as watched";


                if (lista.get(position).episode.inWatchlist)
                    options[3] = "Remove from watchlist";
                else
                    options[3] = "Add to watchlist";

                //options[4] = "Hide this movie";


                builder.setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item_clicked) {

                        switch (item_clicked) {
                            case 0:
                                new MarkEpisodeSeenUnseen(getActivity(), CalendarWeekFragment.this, manager, lista.get(position).show, lista.get(position).episode, position).execute();
                                break;
                            case 1:
                                if (lista.get(position).episode.rating != null)
                                    new UnrateEpisode(getActivity(),CalendarWeekFragment.this,manager,lista.get(position).show,lista.get(position).episode,position).execute();
                                else
                                new RateEpisodeLove(getActivity(),CalendarWeekFragment.this,manager,lista.get(position).show,lista.get(position).episode,position).execute();

                                break;
                            case 2:
                                if (lista.get(position).episode.rating != null)
                                    new UnrateEpisode(getActivity(),CalendarWeekFragment.this,manager,lista.get(position).show,lista.get(position).episode,position).execute();
                                else
                                new RateEpisodeHate(getActivity(),CalendarWeekFragment.this,manager,lista.get(position).show,lista.get(position).episode,position).execute();

                                break;
                            case 3:
                                new EpisodeWatchlistUnWatchlist(getActivity(), CalendarWeekFragment.this, manager, lista.get(position).show, lista.get(position).episode, position).execute();
                                break;
                        }
                    }
                }
                );
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });

    }


    @Override
    public void onWeekInfoComplete(List<CalendarDate> response) {
        rootView.findViewById(R.id.progressBarCalendarWeek).setVisibility(View.GONE);
        l.setEmptyView(rootView.findViewById(R.id.emptyView));
        lista.clear();
        weekCalendar.clear();
        weekCalendar.addAll(response);
        for (CalendarDate l : weekCalendar) {
            lista.addAll(l.episodes);
        }
        mAdapter.notifyDataSetChanged();
        Log.d("Trakt", "Download complete, lista size: " + lista.size());
    }

    @Override
    public void OnMarkSeenUnseenCompleted(int position) {

        lista.get(position).episode.watched = !lista.get(position).episode.watched;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void WatchlistUnWatchlistCompleted(int position) {
        lista.get(position).episode.inWatchlist = !lista.get(position).episode.inWatchlist;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnMarkEpisodeHateCompleted(int position, RatingResponse response) {
        lista.get(position).episode.rating = response.rating;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnMarkEpisodeLoveCompleted(int position, RatingResponse response) {
        lista.get(position).episode.rating = response.rating;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnMarkEpisodeNoneCompleted(int position, RatingResponse response) {
        lista.get(position).episode.rating = null;
        mAdapter.notifyDataSetChanged();
    }


    public class WeekCalendarAdapter extends BaseAdapter implements StickyGridHeadersBaseAdapter {

        private final List<CalendarDate> mListHeaders;
        private final List<CalendarDate.CalendarTvShowEpisode> lista;
        private LayoutInflater inflater;

        public WeekCalendarAdapter(Context context, List<CalendarDate> mList, List<CalendarDate.CalendarTvShowEpisode> lista) {
            this.mListHeaders = mList;
            this.lista = lista;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return lista.size();
        }

        @Override
        public CalendarDate.CalendarTvShowEpisode getItem(int i) {
            return lista.get(i);
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
            aq.id(R.id.imageViewCalendarEpisodeBackdrop).image(lista.get(position).episode.images.screen, false, true, 200, R.drawable.episode_backdrop_small,aq.getCachedImage(R.drawable.episode_backdrop_small),AQuery.FADE_IN);
            aq.id(R.id.textViewCalendarShowTitle).text(lista.get(position).show.title);
            aq.id(R.id.textViewCalendarEpisodeSeasonNumberAndEpisodeNumber).text("S" + lista.get(position).episode.season + "E" + lista.get(position).episode.number);
            aq.id(R.id.textViewCalendarEpisodeTitle).text(lista.get(position).episode.title);

            if (lista.get(position).episode.inCollection)
                aq.id(R.id.imageViewCalendarEpisodeCollectionTag).visible();
            else
                aq.id(R.id.imageViewCalendarEpisodeCollectionTag).gone();

            if (lista.get(position).episode.inWatchlist)
                aq.id(R.id.imageViewCalendarEpisodeWatchlistTag).visible();
            else
                aq.id(R.id.imageViewCalendarEpisodeWatchlistTag).gone();


            if (lista.get(position).episode.watched || lista.get(position).episode.plays>0)
                aq.id(R.id.imageViewCalendarEpisodeSeenTag).visible();
            else
                aq.id(R.id.imageViewCalendarEpisodeSeenTag).gone();
            if (lista.get(position).episode.rating != null) {
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


            aq.recycle(convertView);
            return convertView;
        }

        @Override
        public int getCountForHeader(int i) {
            return mListHeaders.get(i).episodes.size();
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
            SimpleDateFormat format = new SimpleDateFormat("E MMM dd, yyyy");
            aq.id(R.id.textViewCalendarWeekDay).text(format.format(mListHeaders.get(position).date) + "");

            return convertView;
        }
    }
}
