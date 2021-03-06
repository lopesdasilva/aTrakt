package com.lopesdasilva.trakt.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.ActivityItem;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.activities.EpisodeActivity;
import com.lopesdasilva.trakt.activities.MovieActivity;
import com.lopesdasilva.trakt.activities.ShowActivity;
import com.lopesdasilva.trakt.extras.UserChecker;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lopesdasilva on 15/06/13.
 */
public class FriendsFragment extends Fragment {


    private View rootView;
    private FriendsTaskDownload mFriendsTask;
    private ServiceManager manager;
    private List<ActivityItem> activityList = new LinkedList<ActivityItem>();
    private LayoutInflater inflater;
    private LinearLayout mFriendsLayout;

    public FriendsFragment() {

    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        rootView = inflater.inflate(R.layout.friends_fragment, container, false);
//        Log.d("Trakt", "OnCreateView");


        if (savedInstanceState == null) {
            manager = UserChecker.checkUserLogin(getActivity());
            mFriendsTask = new FriendsTaskDownload();
            mFriendsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            activityList = (List<ActivityItem>) savedInstanceState.get("friendsActivity");
            if (activityList.size() != 0)
                updateList(activityList);
            else {
                manager = UserChecker.checkUserLogin(getActivity());
                mFriendsTask = new FriendsTaskDownload();
                mFriendsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
        return rootView;

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("friendsActivity", (Serializable) activityList);
    }

    private void updateList(List<ActivityItem> result) {

        activityList = result;

        mFriendsLayout = (LinearLayout) rootView.findViewById(R.id.linearLayoutFriends);

        int end;
        if (activityList.size() >= 5)
            end = 5;
        else
            end = activityList.size();
        for (final ActivityItem activityItem : activityList.subList(0, end)) {

//            try {
            View activityItemLayout = setActivity(activityItem);
            activityItemLayout.setPadding(0, 0, 0, 2);
            activityItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener(activityItem);
                }
            });
            mFriendsLayout.addView(activityItemLayout);
//            } catch (Exception e) {
//                Log.d("Trakt", "Something went wrong with friends list");
//            }


        }


    }

    private void clickListener(ActivityItem activityItem) {

        Bundle arguments;
        Intent i;
        switch (activityItem.type) {

            case Episode:

                Log.d("Trakt", "Launching Episode Activity");


                arguments = new Bundle();

                if(activityItem.show.imdbId!=null && !"".equals(activityItem.show.imdbId))
                    arguments.putString("show_imdb", activityItem.show.imdbId);
                else if(activityItem.show.tvdbId!=null && !"".equals(activityItem.show.tvdbId))
                    arguments.putString("show_imdb", activityItem.show.tvdbId);
                else
                    arguments.putString("show_imdb", activityItem.show.title.replace(" ","-")+"-"+activityItem.show.year);


                arguments.putInt("show_season", activityItem.episode.season);
                arguments.putInt("show_episode", activityItem.episode.number);
                i = new Intent(getActivity(), EpisodeActivity.class);
                i.putExtras(arguments);

                startActivity(i);
                getActivity().overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);
                break;
            case Show:

                Log.d("Trakt", "Launching Show Activity");

                arguments = new Bundle();
                arguments.putString("show_imdb", activityItem.show.imdbId);

                i = new Intent(getActivity(), ShowActivity.class);
                i.putExtras(arguments);

                startActivity(i);
                getActivity().overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);

                break;
            case Movie:
                Log.d("Trakt", "Launching Movie Activity");


                arguments = new Bundle();
                if(activityItem.movie.imdbId!=null && !"".equals(activityItem.movie.imdbId))
                    arguments.putString("movie_imdb", activityItem.movie.imdbId);
                else if(activityItem.movie.tmdbId!=null && !"".equals(activityItem.movie.tmdbId))
                    arguments.putString("movie_imdb", activityItem.movie.tmdbId);
                else
                    arguments.putString("movie_imdb", activityItem.movie.title.replace(" ","-")+"-"+activityItem.movie.year);


                i = new Intent(getActivity(), MovieActivity.class);
                i.putExtras(arguments);

                startActivity(i);
                getActivity().overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);

                break;
        }


    }

    private View setActivity(ActivityItem activityItem) {

        if (getActivity() != null) {
            LinearLayout relativeLayoutFriends = (LinearLayout) inflater.inflate(R.layout.friends_item_episode, null).findViewById(R.id.linearLayoutFriendsItem);
            final AQuery aq = new AQuery(relativeLayoutFriends);
            switch (activityItem.type) {

                case All:
                    break;
                case Episode:
                    aq.id(R.id.imageViewFriendsPoster).image(activityItem.episode.images.screen, false, true, 150, R.drawable.episode_backdrop, aq.getCachedImage(R.drawable.episode_backdrop_small), AQuery.FADE_IN, AQuery.RATIO_PRESERVE);
                    aq.id(R.id.textViewFriendsItemTitle).text(activityItem.show.title + " S" + activityItem.episode.season + "E" + activityItem.episode.number);
                    break;
                case Show:
                    aq.id(R.id.imageViewFriendsPoster).image(activityItem.show.images.poster, false, true, 150, R.drawable.poster_small, aq.getCachedImage(R.drawable.poster_small), AQuery.FADE_IN);
                    aq.id(R.id.textViewFriendsItemTitle).text(activityItem.show.title);

                    break;
                case Movie:
                    aq.id(R.id.imageViewFriendsPoster).image(activityItem.movie.images.fanart, false, true, 150, R.drawable.poster_small, aq.getCachedImage(R.drawable.poster_small), AQuery.FADE_IN);
                    aq.id(R.id.textViewFriendsItemTitle).text(activityItem.movie.title);
                    break;
                case List:
                    break;
            }


            if (activityItem.user != null && activityItem.user.username != null)
                aq.id(R.id.textViewFriendsUsername).text(activityItem.user.username);
            aq.id(R.id.textViewFriendsAction).text(activityItem.action.toString());


            switch (activityItem.action) {

                case All:
                    break;
                case Watching:
                    aq.id(R.id.textViewFriendsAction).text("is " + activityItem.action.toString());
                    break;
                case Scrobble:
                    aq.id(R.id.textViewFriendsAction).text("watched");
                    break;
                case Checkin:
                    break;
                case Seen:
                    break;
                case Collection:
                    break;
                case Rating:
                    aq.id(R.id.textViewFriendsAction).text("rated as " + activityItem.rating);
                    break;
                case Watchlist:
                    aq.id(R.id.textViewFriendsAction).text("added to their " + activityItem.action.toString());
                    break;
                case Shout:
                    break;
                case Created:
                    break;
                case ItemAdded:
                    break;
            }

            aq.id(R.id.textViewFriendsTime).text(activityItem.when.day + " " + activityItem.when.time);


//        relativeLayoutFriends.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                switch (motionEvent.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
////                        aq.id(R.id.relativeLayoutFriendsItem).getView().setT.setColorFilter(0xaf0099cc, PorterDuff.Mode.SRC_ATOP);
////                        aq.id(R.id.imageViewFriendsPoster).getImageView().setColorFilter(0xaf0099cc, PorterDuff.Mode.SRC_ATOP);
//                        break;
//
//                    case MotionEvent.ACTION_UP:
//                        aq.id(R.id.relativeLayoutFriendsItem).getImageView().clearColorFilter();
//                        break;
//                    case MotionEvent.ACTION_CANCEL:
//                        aq.id(R.id.relativeLayoutFriendsItem).getImageView().clearColorFilter();
//                        break;
//                }
//
//                return false;
//            }
//        });
            return relativeLayoutFriends;
        }else
            return null;



    }

    public class FriendsTaskDownload extends AsyncTask<Void, Void, List<ActivityItem>> {

        private Exception e;

        @Override
        protected List<ActivityItem> doInBackground(Void... voids) {
            try {
                Date d = new Date();
                int onWeekInMIliSecconds = 604800000;
                d.setTime(d.getTime() - onWeekInMIliSecconds);
                return manager.activityService().friends().timestamp(d).fire().activity;
            } catch (Exception e) {
                this.e = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<ActivityItem> result) {
            if (e == null) {
                updateList(result);
            } else
                Log.d("Trakt", "Error downloading Friends list");

        }

    }


}
