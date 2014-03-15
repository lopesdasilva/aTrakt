package com.lopesdasilva.trakt.fragments;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.Tasks.MarkSeenUnseen;
import com.lopesdasilva.trakt.extras.UserChecker;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersBaseAdapter;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lopesdasilva on 15/10/13.
 */
public class WatchlistEpisodes extends DialogFragment implements MarkSeenUnseen.OnMarkSeenUnseenCompleted {


    private ServiceManager manager;
    private View rootView;
    private TvShow currentShow;
    private ListAdapter mAdapter;
    private LinkedList<TvShowSeason_Auxiliar>  mListHeaders=new LinkedList<TvShowSeason_Auxiliar>();
    private List<TvShowEpisode> lista=new LinkedList<TvShowEpisode>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manager = UserChecker.checkUserLogin(getActivity());

        currentShow=(TvShow)getArguments().get("show");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(currentShow.title);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.watchlist_modal_fragment, container, false);

        StickyGridHeadersGridView listview = (StickyGridHeadersGridView) rootView.findViewById(R.id.listViewWathclistShowEpisodes);


        for(TvShowEpisode te: currentShow.episodes){
            if(mListHeaders.size()==0){
                TvShowSeason_Auxiliar season_auxiliar=new TvShowSeason_Auxiliar(te.season);
                season_auxiliar.addEpsiode(te);
                mListHeaders.addLast(season_auxiliar);
            }
            else
                if(mListHeaders.getLast().season==te.season){
                    mListHeaders.getLast().addEpsiode(te);
                }else{
                    TvShowSeason_Auxiliar season_auxiliar=new TvShowSeason_Auxiliar(te.season);
                    season_auxiliar.addEpsiode(te);
                    mListHeaders.addLast(season_auxiliar);
                }

        }

//        mListHeaders.indexOf(object)

        mAdapter=new ShowSeasonsAdapter(getActivity(),mListHeaders,currentShow.episodes);

        listview.setAdapter(mAdapter);


        return rootView;
    }

    @Override
    public void OnMarkSeenUnseenCompleted(int position) {
        Toast.makeText(getActivity(),"todo mark seen/unseen",Toast.LENGTH_SHORT).show();
    }



    public class ShowSeasonsAdapter extends BaseAdapter implements StickyGridHeadersBaseAdapter {

        private List<TvShowSeason_Auxiliar> mListHeaders;
        private List<TvShowEpisode> lista;
        private LayoutInflater inflater;

        public ShowSeasonsAdapter(Context context, List<TvShowSeason_Auxiliar> seasons, List<TvShowEpisode> lista) {
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
            aq.id(R.id.imageViewSeasonsEpisodeScreen).image(lista.get(position).images.screen, false, true, 200, 0);

//            if (lista.get(position).watched) {
//                aq.id(R.id.imageViewSeasonsEpisodeSeenTag).visible();
//                aq.id(R.id.imageViewSeasonsEpisodeOptions).clicked(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        showSeenMenu(view, lista.get(position), position);
//                    }
//                });
//            } else {
//                aq.id(R.id.imageViewSeasonsEpisodeSeenTag).gone();
//                aq.id(R.id.imageViewSeasonsEpisodeOptions).clicked(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        showSeenMenu(view, lista.get(position), position);
//                    }
//                });
//            }
//            if (lista.get(position).rating != null) {
//                switch (lista.get(position).rating) {
//                    case Love:
//                        aq.id(R.id.imageViewSeasonsEpisodeLoveTag).visible();
//                        aq.id(R.id.imageViewSeasonsEpisodeHateTag).gone();
//                        break;
//                    case Hate:
//                        aq.id(R.id.imageViewSeasonsEpisodeLoveTag).gone();
//                        aq.id(R.id.imageViewSeasonsEpisodeHateTag).visible();
//                        break;
//
//                }
//            } else {
//                aq.id(R.id.imageViewSeasonsEpisodeLoveTag).gone();
//                aq.id(R.id.imageViewSeasonsEpisodeHateTag).gone();
//
//            }
//
//            if (lista.get(position).inWatchlist)
//                aq.id(R.id.imageViewSeasonsEpisodeWatchlistTag).visible();
//            else
//                aq.id(R.id.imageViewSeasonsEpisodeWatchlistTag).gone();


            return convertView;
        }


        public void showSeenMenu(View v, final TvShowEpisode episode, final int position) {
            PopupMenu popup = new PopupMenu(getActivity(), v);

            // This activity implements OnMenuItemClickListener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {


                    switch (menuItem.getItemId()) {

                        case R.id.action_episode_unseen:
                            new MarkSeenUnseen(getActivity(), WatchlistEpisodes.this, manager, currentShow, episode, position).execute();
                            return true;
                        default:
                            return false;
                    }
                }
            });
            if (episode.watched)
                popup.inflate(R.menu.seasons_episode_actions_unseen);
            else
                popup.inflate(R.menu.seasons_episode_actions_seen);
            popup.show();
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
        public View getHeaderView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.seasons_seasons_item, parent, false);
            }
            AQuery aq = new AQuery(convertView);

            aq.id(R.id.textViewSeasonsSeasonNumber).text("Season " + mListHeaders.get(position).season);


            return convertView;
        }
    }

    public class TvShowSeason_Auxiliar{

        public final int season;
        public final List<TvShowEpisode> episodes;

        public TvShowSeason_Auxiliar(int season, List<TvShowEpisode> episodes){
            this.season=season;
            this.episodes=episodes;
        }

        public TvShowSeason_Auxiliar(Integer season) {
            this.season=season;
            this.episodes=new LinkedList<TvShowEpisode>();
        }

        public void addEpsiode(TvShowEpisode te) {
            episodes.add(te);
        }
    }

}
