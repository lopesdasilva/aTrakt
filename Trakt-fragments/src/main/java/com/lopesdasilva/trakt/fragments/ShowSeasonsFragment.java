package com.lopesdasilva.trakt.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.jakewharton.trakt.entities.TvShowSeason;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.Tasks.*;
import com.lopesdasilva.trakt.activities.EpisodeActivity;
import com.lopesdasilva.trakt.extras.UserChecker;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersBaseAdapter;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lopesdasilva on 22/05/13.
 */
public class ShowSeasonsFragment extends Fragment implements MarkEpisodeSeenUnseen.OnMarkSeenUnseenCompleted, MultipleSeenUnseenTask.OnMarkSeenUnseenCompleted, EpisodeWatchlistUnWatchlist.WatchlistUnWatchlistCompleted, DownloadSeasonsInfo.onSeasonsTaskComplete, DownloadShowInfo.onShowInfoTaskComplete {


    List<TvShowEpisode> mSelectedEpisodes = new LinkedList<TvShowEpisode>();
    private View rootView;
    private TvShow show;
    private ServiceManager manager;
    private DownloadSeasonsInfo mTaskDownloadSeasons;
    private StickyGridHeadersGridView mListView;
    private ShowSeasonsAdapter mAdapter;
    private List<TvShowSeason> mListHeaders = new LinkedList<TvShowSeason>();
    private List<TvShowEpisode> lista = new LinkedList<TvShowEpisode>();
    private Bundle savedInstanceState;
    private String mShow;

    public ShowSeasonsFragment() {
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.seasons_fragment, container, false);
//        Log.d("Trakt", "OnCreateView");

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setRetainInstance(true);
        if(show==null)
        show = (TvShow) getArguments().getSerializable("show");
        mShow = (String) getArguments().getSerializable("show_imdb");
        if(getActivity()!=null) {
            manager = UserChecker.checkUserLogin(getActivity());
            this.savedInstanceState = savedInstanceState;

            if (savedInstanceState == null)
                if (show != null && show.seasons != null) {
                    Log.d("Trakt Fragments", "ServiceManager: " + manager);


                    mListHeaders.addAll(show.seasons);
                    for (TvShowSeason season : show.seasons) {
                        lista.addAll(season.episodes.episodes);
                    }

                    mListView = (StickyGridHeadersGridView) rootView.findViewById(R.id.listViewSeasons);
                    mAdapter = new ShowSeasonsAdapter(getActivity(), mListHeaders, lista);
                    mListView.setAdapter(mAdapter);
                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                            if (getActivity().findViewById(R.id.episode_list) != null) {

                                Bundle arguments = new Bundle();
                                arguments.putString("show_imdb", show.imdbId);
                                arguments.putInt("show_season", lista.get(position).season);
                                arguments.putInt("show_episode", lista.get(position).number);

                                Fragment fragment = new EpisodeFragment();
                                fragment.setArguments(arguments);


                                mListView.setSelection(position);

                                Log.d("Trakt", "Launching new fragment EpisodeFragment");
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.episode_activity, fragment).commit();

                            } else {

                                Log.d("Trakt", "Launching Episode Activity");
                                Log.d("Trakt", "Episode: S" + lista.get(position).season + "E" + lista.get(position).number);

                                Bundle arguments = new Bundle();
                                arguments.putString("show_imdb", show.imdbId);
                                arguments.putInt("show_season", lista.get(position).season);
                                arguments.putInt("show_episode", lista.get(position).number);
                                arguments.putSerializable("show", show);

                                Intent intent = new Intent(getActivity(), EpisodeActivity.class);
                                intent.putExtras(arguments);

                                startActivity(intent);
                                getActivity().overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);
                            }
                        }
                    });
                    mListView.setChoiceMode(StickyGridHeadersGridView.CHOICE_MODE_MULTIPLE_MODAL);
                    mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {


                        @Override
                        public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                              long id, boolean checked) {


                            int realPosition = mListView.mAdapter.translatePosition(position).mPosition;

//                    Log.d("Trakt", "postion: " + position + " checked:" + checked);
//                    Log.d("Trakt", "postion: " + postion.mPosition + " checked:" + checked);
//                    Log.d("Trakt","Seasons:"+mListHeaders.size());

                            Log.d("Trakt", "Episode: S" + lista.get(realPosition).season + "E" + lista.get(realPosition).number);
                            if (checked)
                                mSelectedEpisodes.add(lista.get(realPosition));
                            else
                                mSelectedEpisodes.remove(lista.get(realPosition));


                            // Here you can do something when items are selected/de-selected,
                            // such as update the title in the CAB
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                            // Respond to clicks on the actions in the CAB

                            switch (item.getItemId()) {
                                case R.id.action_episode_seen:
                                    Toast.makeText(getActivity(), "Mark as seen", Toast.LENGTH_SHORT).show();
                                    MultipleSeenUnseenTask mMultipleSeenTask = new MultipleSeenUnseenTask(getActivity(), ShowSeasonsFragment.this, manager, show, mSelectedEpisodes, true, mode);
                                    mMultipleSeenTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                            deleteSelectedItems();

                                    return true;
                                case R.id.action_episode_unseen:
                                    Toast.makeText(getActivity(), "Mark as unseen", Toast.LENGTH_SHORT).show();
                                    MultipleSeenUnseenTask mMultipleUnseenTask = new MultipleSeenUnseenTask(getActivity(), ShowSeasonsFragment.this, manager, show, mSelectedEpisodes, false, mode);
                                    mMultipleUnseenTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                            deleteSelectedItems();

                                    return true;
                                default:
                                    return false;
                            }
                        }

                        @Override
                        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                            // Inflate the menu for the CAB
                            MenuInflater inflater = mode.getMenuInflater();
                            inflater.inflate(R.menu.seasons_episode_actions_seen, menu);
                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode mode) {
                            mSelectedEpisodes.clear();
                            // Here you can make any necessary updates to the activity when
                            // the CAB is removed. By default, selected items are deselected/unchecked.
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                            // Here you can perform updates to the CAB due to
                            // an invalidate() request
                            return false;
                        }
                    });
                    mListView.setOnHeaderClickListener(new StickyGridHeadersGridView.OnHeaderClickListener() {
                        @Override
                        public void onHeaderClick(AdapterView<?> parent, View view, long id) {

                            int position = mListHeaders.get((int) id).season;
                            for (int i = 0; i != lista.size(); i++) {
                                if (lista.get(i).season == position) {
                                    int tranlatedposition = mListView.mAdapter.translatePosition(i).mPosition;
                                    mListView.setItemChecked(i + 1 + (int) id, true);
                                }
                            }

                        }
                    });
                } else {
                    new DownloadShowInfo(ShowSeasonsFragment.this, getActivity(), manager, mShow).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                new DownloadSeasonsInfo(ShowSeasonsFragment.this,getActivity(),manager,mShow).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
        }
    }

    @Override
    public void OnMarkSeenUnseenCompleted(int position) {
        lista.get(position).watched = !lista.get(position).watched;
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void OnMultipleMarkSeenUnseenCompleted(List<TvShowEpisode> mSelectedEpisodes, boolean markSeen, ActionMode mode) {
        for (TvShowEpisode episode : mSelectedEpisodes) {
            lista.get(lista.indexOf(episode)).watched = markSeen;
        }
        this.mSelectedEpisodes.clear();
        mode.finish(); // Action picked, so close the CAB
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void WatchlistUnWatchlistCompleted(int position) {
        lista.get(position).inWatchlist = !lista.get(position).inWatchlist;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSeasonsTaskComplete(List<TvShowSeason> response) {
       if(show==null){
        show = new TvShow();
        show.imdbId = mShow;
    }
        show.seasons=response;
        onActivityCreated(savedInstanceState);
    }

    @Override
    public void onShowInfoTaskComplete(TvShow response) {
        show=response;
        onActivityCreated(savedInstanceState);
    }


    public class ShowSeasonsAdapter extends BaseAdapter implements StickyGridHeadersBaseAdapter {

        private List<TvShowSeason> mListHeaders;
        private List<TvShowEpisode> lista;
        private LayoutInflater inflater;

        public ShowSeasonsAdapter(Context context, List<TvShowSeason> seasons, List<TvShowEpisode> lista) {
            this.mListHeaders = seasons;
            this.lista = lista;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return lista.size();
        }

        @Override
        public TvShowEpisode getItem(int i) {
            return lista.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.seasons_episode_item, parent, false);
            }

            AQuery aq = new AQuery(convertView);
            aq.id(R.id.textViewSeasonsEpisodeNumber).text("Episode " + lista.get(position).number);
            aq.id(R.id.textViewSeasonsEpisodeTitle).text(lista.get(position).title);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
            aq.id(R.id.textViewSeasonsEpisodeDate).text("" + dateFormat.format(lista.get(position).firstAired));
            aq.id(R.id.imageViewSeasonsEpisodeScreen).image(lista.get(position).images.screen, false, true, 200, R.drawable.episode_backdrop_small, aq.getCachedImage(R.drawable.episode_backdrop_small), AQuery.FADE_IN, AQuery.RATIO_PRESERVE);

            if (lista.get(position).watched) {
                aq.id(R.id.imageViewSeasonsEpisodeSeenTag).visible();
                aq.id(R.id.imageViewSeasonsEpisodeOptions).clicked(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showContextMenu(view, lista.get(position), position);
                    }
                });
            } else {
                aq.id(R.id.imageViewSeasonsEpisodeSeenTag).gone();
                aq.id(R.id.imageViewSeasonsEpisodeOptions).clicked(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showContextMenu(view, lista.get(position), position);
                    }
                });
            }

            aq.id(R.id.imageViewSeasonsEpisodeLoveTag).gone();
            aq.id(R.id.imageViewSeasonsEpisodeHateTag).gone();


            if ("0".equals(lista.get(position).ratingAdvanced)) {
                aq.id(R.id.relativeLayoutAdvanceRating).gone();
            } else if ("1".equals(lista.get(position).ratingAdvanced)) {
                aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_1);
                aq.id(R.id.relativeLayoutAdvanceRating).visible();
                aq.id(R.id.textViewEpisodeRatingAdvance).text(lista.get(position).ratingAdvanced);
            } else if ("2".equals(lista.get(position).ratingAdvanced)) {

                aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_2);
                aq.id(R.id.relativeLayoutAdvanceRating).visible();
                aq.id(R.id.textViewEpisodeRatingAdvance).text(lista.get(position).ratingAdvanced);
            } else if ("3".equals(lista.get(position).ratingAdvanced)) {

                aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_3);
                aq.id(R.id.relativeLayoutAdvanceRating).visible();
                aq.id(R.id.textViewEpisodeRatingAdvance).text(lista.get(position).ratingAdvanced);
            } else if ("4".equals(lista.get(position).ratingAdvanced)) {

                aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_4);
                aq.id(R.id.relativeLayoutAdvanceRating).visible();
                aq.id(R.id.textViewMovieRatingAdvance).text(lista.get(position).ratingAdvanced);
            } else if ("5".equals(lista.get(position).ratingAdvanced)) {

                aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_5);
                aq.id(R.id.relativeLayoutAdvanceRating).visible();
                aq.id(R.id.textViewEpisodeRatingAdvance).text(lista.get(position).ratingAdvanced);
            } else if ("6".equals(lista.get(position).ratingAdvanced)) {

                aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_6);
                aq.id(R.id.relativeLayoutAdvanceRating).visible();
                aq.id(R.id.textViewEpisodeRatingAdvance).text(lista.get(position).ratingAdvanced);
            } else if ("7".equals(lista.get(position).ratingAdvanced)) {

                aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_7);
                aq.id(R.id.relativeLayoutAdvanceRating).visible();
                aq.id(R.id.textViewEpisodeRatingAdvance).text(lista.get(position).ratingAdvanced);
            } else if ("8".equals(lista.get(position).ratingAdvanced)) {

                aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_8);
                aq.id(R.id.relativeLayoutAdvanceRating).visible();
                aq.id(R.id.textViewEpisodeRatingAdvance).text(lista.get(position).ratingAdvanced);
            } else if ("9".equals(lista.get(position).ratingAdvanced)) {

                aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_9);
                aq.id(R.id.relativeLayoutAdvanceRating).visible();
                aq.id(R.id.textViewShowRatingAdvance).text(lista.get(position).ratingAdvanced);
            } else if ("10".equals(lista.get(position).ratingAdvanced)) {

                aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_10);
                aq.id(R.id.relativeLayoutAdvanceRating).visible();
                aq.id(R.id.textViewEpisodeRatingAdvance).text(lista.get(position).ratingAdvanced);
                aq.id(R.id.textViewEpisodeRatingAdvance).margin(0, 0, 1, 0);
            }


            if (lista.get(position).inWatchlist)
                aq.id(R.id.imageViewSeasonsEpisodeWatchlistTag).visible();
            else
                aq.id(R.id.imageViewSeasonsEpisodeWatchlistTag).gone();

            if (lista.get(position).inCollection)
                aq.id(R.id.imageViewSeasonsEpisodeCollectionTag).visible();
            else
                aq.id(R.id.imageViewSeasonsEpisodeCollectionTag).gone();


            return convertView;
        }


        public void showContextMenu(View v, final TvShowEpisode episode, final int position) {
            PopupMenu popup = new PopupMenu(getActivity(), v);

            // This activity implements OnMenuItemClickListener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {


                    switch (menuItem.getItemId()) {

                        case R.id.action_episode_seen:
                            new MarkEpisodeSeenUnseen(getActivity(), ShowSeasonsFragment.this, manager, show, episode, position).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            return true;
                        case R.id.action_episode_unseen:
                            new MarkEpisodeSeenUnseen(getActivity(), ShowSeasonsFragment.this, manager, show, episode, position).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            return true;
                        case R.id.action_episode_remove_watchlist:
                            new EpisodeWatchlistUnWatchlist(getActivity(), ShowSeasonsFragment.this, manager, show, episode, position).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            return true;
                        case R.id.action_episode_add_watchlist:
                            new EpisodeWatchlistUnWatchlist(getActivity(), ShowSeasonsFragment.this, manager, show, episode, position).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            return true;

                        default:
                            return false;
                    }
                }
            });
            if (episode.watched) {
                popup.inflate(R.menu.seasons_episode_actions_unseen);
            } else
                popup.inflate(R.menu.seasons_episode_actions_seen);
            popup.show();
            if (episode.inWatchlist) {
                popup.inflate(R.menu.seasons_episode_actions_remove_watchlist);
            } else
                popup.inflate(R.menu.seasons_episode_actions_add_watchlist);
        }


        @Override
        public int getCountForHeader(int i) {
            return mListHeaders.get(i).episodes.episodes.size();
        }

        @Override
        public int getNumHeaders() {
            return mListHeaders.size();
        }

        @Override
        public View getHeaderView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.seasons_seasons_item, parent, false);
            }
            AQuery aq = new AQuery(convertView);

            aq.id(R.id.textViewSeasonsSeasonNumber).text("Season " + mListHeaders.get(position).season);


            return convertView;
        }
    }


}

