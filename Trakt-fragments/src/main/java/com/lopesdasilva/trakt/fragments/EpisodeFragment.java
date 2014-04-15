package com.lopesdasilva.trakt.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.RatingResponse;
import com.jakewharton.trakt.entities.Response;
import com.jakewharton.trakt.entities.TvEntity;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.Tasks.CheckInChecker;
import com.lopesdasilva.trakt.Tasks.DownloadEpisodeInfo;
import com.lopesdasilva.trakt.Tasks.EpisodeWatchlistUnWatchlist;
import com.lopesdasilva.trakt.Tasks.MarkEpisodeSeenUnseen;
import com.lopesdasilva.trakt.Tasks.RateAdvancedEpisode;
import com.lopesdasilva.trakt.Tasks.RateEpisodeHate;
import com.lopesdasilva.trakt.Tasks.RateEpisodeLove;
import com.lopesdasilva.trakt.Tasks.UnrateEpisode;
import com.lopesdasilva.trakt.activities.ShowActivity;
import com.lopesdasilva.trakt.extras.UserChecker;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lopesdasilva on 17/05/13.
 */
public class EpisodeFragment extends Fragment implements DownloadEpisodeInfo.onEpisodeTaskComplete, MarkEpisodeSeenUnseen.OnMarkSeenUnseenCompleted, EpisodeWatchlistUnWatchlist.WatchlistUnWatchlistCompleted, RateEpisodeHate.OnMarkEpisodeHateCompleted, RateEpisodeLove.OnMarkEpisodeLoveCompleted, UnrateEpisode.OnMarkEpisodeNoneCompleted, RatingAdvance.OnRatingComplete, RateAdvancedEpisode.OnRatingAdvancedEpisodeCompleted {
    private View rootView;
    private String show;
    private int season;
    private int episode;
    private ServiceManager manager;
    private TvEntity episode_info;
    private Menu mMenu;
    private ImageView mRefreshView;
    private MenuItem mRefreshItem;
    private DownloadEpisodeInfo mTaskDownloadEpisode;
    private String mUsername;
    private AQuery aq;
    private int rating_temp;


    public EpisodeFragment() {
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("mEpisode", episode_info);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.episode_fragment, container, false);



        show = getArguments().getString("show_imdb");
        season = getArguments().getInt("show_season");
        episode = getArguments().getInt("show_episode");


        manager = UserChecker.checkUserLogin(getActivity());
        mUsername = UserChecker.getUsername(getActivity());

        Log.d("Trakt it", "ServiceManager: " + manager);
        Log.d("Trakt it", "Show_imdb received: " + show);
        Log.d("Trakt it", "Show_season received: " + season);
        Log.d("Trakt it", "Show_episode received: " + episode);
        Log.d("Trakt it", "savedInstanceState: " + savedInstanceState);

        aq= new AQuery(rootView);
        aq.id(R.id.relativeLayoutEpisodeLoading).visible();
        aq.id(R.id.scrollViewepisodeInfoLayout).invisible();


        if (savedInstanceState == null) {
            mTaskDownloadEpisode = new DownloadEpisodeInfo(this, getActivity(), manager, show, season, episode);
            mTaskDownloadEpisode.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else{
            episode_info = (TvEntity) savedInstanceState.getSerializable("mEpisode");
            if(episode_info==null){
                mTaskDownloadEpisode = new DownloadEpisodeInfo(this, getActivity(), manager, show, season, episode);
                mTaskDownloadEpisode.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }else
                updateUI();
        }





        return rootView;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.mMenu = menu;
        menu.add(0, 0, 0, "Refresh").setIcon(android.R.drawable.ic_popup_sync).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        if (episode_info.episode.watched) {
            menu.add(0, 1, 1, "Unseen")
                    .setIcon(R.drawable.ic_action_accept_on).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        } else
            menu.add(0, 1, 1, "Watch").setIcon(R.drawable.ic_action_accept).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        ;


        if (episode_info.episode.inWatchlist) {
            menu.add(0, 2, 2, "UnWatchlist");
        } else
            menu.add(0, 2, 2, "Watchlist");

        menu.add(0, 3, 3, "Checkin");

            menu.add(0, 4, 4, R.string.rate);
        menu.add(0,7,7,R.string.youtube);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                if (mTaskDownloadEpisode == null) {

                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    mRefreshView = (ImageView) inflater.inflate(R.layout.refresh, null);
//
//                // Load the animation
                    Animation rotateClockwise = AnimationUtils.loadAnimation(getActivity(), R.anim.rotation);
//
//                // Apply the animation to our View
                    mRefreshView.startAnimation(rotateClockwise);
                    mRefreshItem = item;
//                // Apply the View to our MenuItem
                    item.setActionView(mRefreshView);

                    mTaskDownloadEpisode = new DownloadEpisodeInfo(this, getActivity(), manager, show, season, episode);
                    mTaskDownloadEpisode.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                return true;
            case 1:
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mRefreshView = (ImageView) inflater.inflate(R.layout.refresh, null);
//
//                // Load the animation
                Animation rotateClockwise = AnimationUtils.loadAnimation(getActivity(), R.anim.rotation);
//
//                // Apply the animation to our View
                mRefreshView.startAnimation(rotateClockwise);
                mRefreshItem = item;
//                // Apply the View to our MenuItem
                item.setActionView(mRefreshView);

                Log.d("Trakt Fragments", "Unseen button clicked");
                new MarkEpisodeSeenUnseen(getActivity(), this, manager, episode_info.show, episode_info.episode, 0).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


                return true;
            case 2:

                Log.d("Trakt Fragments", "Add/Rem watchlist button clicked");
                new EpisodeWatchlistUnWatchlist(getActivity(), this, manager, episode_info.show, episode_info.episode, 0).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                return true;
            case 3:


//                CreateNotifications.EpisodeNotification(getActivity(), episode_info);


//                Bundle bundle = new Bundle();
// add extras here..
//                MyAlarmReceiver alarm = new MyAlarmReceiver(getActivity(), bundle, 30);

                new EpisodeCheckIn().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                return true;
            case 4:
                RatingAdvance  dialog=new RatingAdvance(this,episode_info.episode.ratingAdvanced);
                dialog.show(getFragmentManager(), "NoticeDialogFragment");
//                new RateEpisodeHate(getActivity(),EpisodeFragment.this,manager,episode_info.show,episode_info.episode,0).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//
                return true;
            case 5:
                new RateEpisodeLove(getActivity(),EpisodeFragment.this,manager,episode_info.show,episode_info.episode,0).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                return true;
            case 6:
                new UnrateEpisode(getActivity(),EpisodeFragment.this,manager,episode_info.show,episode_info.episode,0).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                return true;
            case 7:
                Intent intent = new Intent(Intent.ACTION_SEARCH);
                intent.setPackage("com.google.android.youtube");
                intent.putExtra("query", episode_info.show.title+" "+episode_info.episode.title);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDownloadEpisodeInfoComplete(TvEntity response) {
        episode_info = response;
        updateUI();
        mTaskDownloadEpisode = null;
        new CheckInChecker(getActivity(), manager, mUsername).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void OnMarkSeenUnseenCompleted(int position) {
        //Dont use position
        updateSeenUnseen();
    }


    private void updateWatchlist() {
        AQuery aq = new AQuery(getActivity());
        if (episode_info.episode.inWatchlist) {
            mMenu.findItem(2).setTitle("UnWatchlist");
            aq.id(R.id.imageViewWatchlistTag).gone();
        } else {
            aq.id(R.id.imageViewWatchlistTag).visible();
            mMenu.findItem(2).setTitle("Watchlist");
        }
        episode_info.episode.inWatchlist = !episode_info.episode.inWatchlist;
    }

    private void updateSeenUnseen() {
        mRefreshView.clearAnimation();
        mRefreshItem.setActionView(null);


        AQuery aq = new AQuery(getActivity());
        if (episode_info.episode.watched) {

            mMenu.findItem(1).setIcon(R.drawable.ic_action_accept).setTitle("Unseen");
            aq.id(R.id.imageViewEpisodeSeen).gone();
        } else {
            mMenu.findItem(1).setIcon(R.drawable.ic_action_accept_on).setTitle("Seen");
            aq.id(R.id.imageViewEpisodeSeen).visible();
        }
        Log.d("Trakt Fragments", "seen status before: " + episode_info.episode.watched);
        episode_info.episode.watched = !episode_info.episode.watched;
        Log.d("Trakt Fragments", "seen status after: " + episode_info.episode.watched);
    }

    private void updateUI() {

        //So that animation in actionbar stop
        if (mRefreshView != null) {

            mRefreshView.clearAnimation();
            mRefreshItem.setActionView(null);
        }

        AQuery aq = new AQuery(rootView);
        aq.id(R.id.textViewMovieTitle).text(episode_info.episode.title);
        aq.id(R.id.textViewEpisodeSeasonNumber).text("S" + episode_info.episode.season + "E" + episode_info.episode.number);
        aq.id(R.id.imageViewEpisodeScreen).image(episode_info.episode.images.screen, false, true, 600, R.drawable.episode_backdrop, aq.getCachedImage(R.drawable.episode_backdrop), AQuery.FADE_IN,AQuery.RATIO_PRESERVE);
        aq.id(R.id.textViewEpisodeOverview).text(episode_info.episode.overview);
        aq.id(R.id.textViewEpisodeRatingsPercentage).text(episode_info.episode.ratings.percentage + "%");
        aq.id(R.id.textViewEpisodeRatingsVotes).text(episode_info.episode.ratings.votes + " votes");
        aq.id(R.id.textViewEpisodeShow).text(episode_info.show.title).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle arguments = new Bundle();
                arguments.putString("show_imdb", episode_info.show.imdbId);

                Intent i = new Intent(getActivity(), ShowActivity.class);
                i.putExtras(arguments);

                startActivity(i);
                getActivity().overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);
            }
        });


        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy hh a");
        if (episode_info.episode.firstAired.after(new Date()))
            aq.id(R.id.textViewEpisodeAirDate).text("Airs " + dateFormat.format(episode_info.episode.firstAired));
        else
            aq.id(R.id.textViewEpisodeAirDate).text("Aired " + dateFormat.format(episode_info.episode.firstAired));


        if (!episode_info.episode.watched || episode_info.episode.plays == 0)
            aq.id(R.id.imageViewEpisodeSeen).gone();
        else
            aq.id(R.id.imageViewEpisodeSeen).visible();

        if (!episode_info.episode.inWatchlist)
            aq.id(R.id.imageViewWatchlistTag).gone();
        else
            aq.id(R.id.imageViewWatchlistTag).visible();


        if (!episode_info.episode.inCollection)
            aq.id(R.id.imageViewCollectionTag).gone();
        else
            aq.id(R.id.imageViewCollectionTag).visible();


        if("0".equals(episode_info.episode.ratingAdvanced)) {
            aq.id(R.id.relativeLayoutAdvanceRating).gone();
        } else if("1".equals(episode_info.episode.ratingAdvanced)){
            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_1);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(episode_info.episode.ratingAdvanced);
        } else if("2".equals(episode_info.episode.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_2);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(episode_info.episode.ratingAdvanced);
        } else if("3".equals(episode_info.episode.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_3);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(episode_info.episode.ratingAdvanced);
        } else if("4".equals(episode_info.episode.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_4);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewMovieRatingAdvance).text(episode_info.episode.ratingAdvanced);
        } else if("5".equals(episode_info.episode.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_5);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(episode_info.episode.ratingAdvanced);
        } else if("6".equals(episode_info.episode.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_6);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(episode_info.episode.ratingAdvanced);
        } else if("7".equals(episode_info.episode.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_7);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(episode_info.episode.ratingAdvanced);
        } else if("8".equals(episode_info.episode.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_8);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(episode_info.episode.ratingAdvanced);
        } else if("9".equals(episode_info.episode.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_9);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(episode_info.episode.ratingAdvanced);
        } else if("10".equals(episode_info.episode.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_10);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(episode_info.episode.ratingAdvanced);
            aq.id(R.id.textViewShowRatingAdvance).margin(0,0,1,0);
        }

        aq.id(R.id.relativeLayoutEpisodeLoading).invisible();
        aq.id(R.id.scrollViewepisodeInfoLayout).visible();
        setHasOptionsMenu(true);
    }

    @Override
    public void WatchlistUnWatchlistCompleted(int position) {
        updateWatchlist();
    }

    @Override
    public void OnMarkEpisodeHateCompleted(int position, RatingResponse response) {
        updateRatings(response);
    }

    private void updateRatings(RatingResponse response) {
        episode_info.episode.rating = response.rating;
        AQuery aq = new AQuery(getActivity());

        if ("0".equals(episode_info.episode.ratingAdvanced)) {
            aq.id(R.id.relativeLayoutAdvanceRating).gone();
        } else if ("1".equals(episode_info.episode.ratingAdvanced)) {
            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_1);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(episode_info.episode.ratingAdvanced);
        } else if ("2".equals(episode_info.episode.ratingAdvanced)) {

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_2);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(episode_info.episode.ratingAdvanced);
        } else if ("3".equals(episode_info.episode.ratingAdvanced)) {

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_3);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(episode_info.episode.ratingAdvanced);
        } else if ("4".equals(episode_info.episode.ratingAdvanced)) {

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_4);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewMovieRatingAdvance).text(episode_info.episode.ratingAdvanced);
        } else if ("5".equals(episode_info.episode.ratingAdvanced)) {

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_5);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(episode_info.episode.ratingAdvanced);
        } else if ("6".equals(episode_info.episode.ratingAdvanced)) {

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_6);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(episode_info.episode.ratingAdvanced);
        } else if ("7".equals(episode_info.episode.ratingAdvanced)) {

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_7);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(episode_info.episode.ratingAdvanced);
        } else if ("8".equals(episode_info.episode.ratingAdvanced)) {

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_8);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(episode_info.episode.ratingAdvanced);
        } else if ("9".equals(episode_info.episode.ratingAdvanced)) {

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_9);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(episode_info.episode.ratingAdvanced);
        } else if ("10".equals(episode_info.episode.ratingAdvanced)) {

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_10);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(episode_info.episode.ratingAdvanced);
            aq.id(R.id.textViewShowRatingAdvance).margin(0, 0, 1, 0);
        }
    }

    @Override
    public void OnMarkEpisodeLoveCompleted(int position, RatingResponse response) {
        updateRatings(response);
    }

    @Override
    public void OnMarkEpisodeNoneCompleted(int position, RatingResponse response) {
        updateRatings(response);
    }

    @Override
    public void onRatingComplete(int rating) {
        new RateAdvancedEpisode(getActivity(),this,manager,episode_info.show,episode_info.episode,rating,rating).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        rating_temp=rating;
    }

    @Override
    public void OnRatingAdvancedEpisodeCompleted(int position, RatingResponse response) {
        episode_info.episode.ratingAdvanced=rating_temp+"";
        updateUI();
    }

    private class EpisodeCheckIn extends AsyncTask<Void, Void, Response> {


        private Exception e;

        @Override
        protected Response doInBackground(Void... voids) {

            try {
                return manager.showService().checkin(episode_info.show.title, episode_info.show.year).season(season).episode(episode).fire();


            } catch (Exception e) {
                this.e = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(Response response) {
            if (e == null) {
                Log.d("Trakt Fragments", "Checked in Episode " + episode_info.episode.title);
                new CheckInChecker(getActivity(), manager, mUsername).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                Log.d("Trakt Fragments", "Error marking episode as unseen: " + e.getMessage());

            }
        }
    }


}