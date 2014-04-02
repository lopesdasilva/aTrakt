package com.lopesdasilva.trakt.fragments;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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

import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.RatingResponse;
import com.jakewharton.trakt.entities.Response;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.Tasks.CheckInChecker;
import com.lopesdasilva.trakt.Tasks.DownloadMovieInfo;
import com.lopesdasilva.trakt.Tasks.RateMovieHate;
import com.lopesdasilva.trakt.Tasks.RateMovieLove;
import com.lopesdasilva.trakt.Tasks.UnrateMovie;
import com.lopesdasilva.trakt.extras.UserChecker;

import java.util.Date;
import java.util.Locale;

public class MovieFragment extends Fragment implements ActionBar.TabListener, DownloadMovieInfo.OnMovieTaskCompleted, RateMovieHate.OnRatingMovieHateCompleted, RateMovieLove.OnRatingMovieLoveCompleted, UnrateMovie.OnUnratingMovieCompleted {

    private View rootView;
    private ServiceManager manager;
    private DownloadMovieInfo mTaskDownloadMovieInfo;
    private ActionBar actionBar;
    private Movie mMovie;
    protected Menu mMenu;
    private MovieInfoFragment mInfoFragment;
    private MenuItem mRefreshItem;
    private ImageView mRefreshView;
    private DownloadMovieInfo mTaskDownloadMovie;
    private String mUsername;

    public MovieFragment() {

    }


    SectionsPagerAdapter mSectionsPagerAdapter;


    ViewPager mViewPager;


    public String movie;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("mMovie", mMovie);
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.mMenu = menu;
        updateOptionsMenu(mMenu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, mMovie.title);
                i.putExtra(Intent.EXTRA_TEXT, mMovie.url);
                startActivity(Intent.createChooser(i, "Share "+mMovie.title));

                return true;
            case 0:
                if (mTaskDownloadMovie == null) {

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

//                    mTaskDownloadMovie = new DownloadMovieInfo(this, getActivity(), manager, movie);
                    mTaskDownloadMovie.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                return true;
            case 2:
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
                new MovieSeenUnseen().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                // do whatever
                return true;
            case 3:

                Log.d("Trakt Fragments", "Add/Rem watchlist button clicked");
                new MovieWatchlist().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                return true;
            case 4:
                new RateMovieLove(getActivity(), MovieFragment.this, manager, mMovie, 0).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
            case 5:
                new RateMovieHate(getActivity(), MovieFragment.this, manager, mMovie, 0).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
            case 6:
                new UnrateMovie(getActivity(), MovieFragment.this, manager, mMovie, 0).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
            case 7:
                new MovieCheckIn().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }



    @Override
    public void OnRatingMovieHateCompleted(int position, RatingResponse response) {
        mMovie.rating=response.rating;
        updateOptionsMenu(mMenu);
        mInfoFragment.updateUI(mMovie);
    }

    @Override
    public void OnRatingMovieLoveCompleted(int position, RatingResponse response) {
        mMovie.rating=response.rating;
        updateOptionsMenu(mMenu);
        mInfoFragment.updateUI(mMovie);
    }

    @Override
    public void OnUnratingMovieCompleted(int position, RatingResponse response) {
        mMovie.rating=response.rating;
        updateOptionsMenu(mMenu);
        mInfoFragment.updateUI(mMovie);
    }


    public void updateOptionsMenu(Menu menu){
        menu.clear();
//        menu.add(0, 0, 0, R.string.refresh).setIcon(android.R.drawable.ic_popup_sync).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(1,1,1,R.string.share).setIcon(android.R.drawable.ic_menu_share).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        if (mMovie.watched) {
            menu.add(0, 2, 2, R.string.unseen);
//                    .setIcon(R.drawable.ic_action_accept_on).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        } else
            menu.add(0, 2, 2, R.string.seen);
//                    .setIcon(R.drawable.ic_action_accept).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        ;


        if (mMovie.inWatchlist) {
            menu.add(0, 3, 3, R.string.unwatchlist);
        } else
            menu.add(0, 3, 3, R.string.watchlist);

        menu.add(0, 7, 7, R.string.checkin);

        if(mMovie.rating!=null){
            switch (mMovie.rating){

                case Love:
                    menu.add(0, 6, 6, R.string.unrate);
                    menu.add(0, 5, 5,  R.string.hated);
                    break;
                case Hate:
                    menu.add(0, 6, 6,  R.string.unrate);
                    menu.add(0, 4, 4, R.string.loved);

                    break;
            }
        }   else{
            menu.add(0, 4, 4, R.string.loved);
            menu.add(0, 5, 5, R.string.hated);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.movie_fragment, container, false);

        mUsername = UserChecker.getUsername(getActivity());



        if (savedInstanceState == null) {
            movie = getArguments().getString("movie_imdb");

            manager = UserChecker.checkUserLogin(getActivity());


            Log.d("Trakt", "ServiceManager: " + manager);
            Log.d("Trakt", "movie_imdb received: " + movie);

            mTaskDownloadMovieInfo = new DownloadMovieInfo(this, getActivity(), manager, movie);
            mTaskDownloadMovieInfo.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mMovie = (Movie) savedInstanceState.getSerializable("mMovie");

            if (mMovie == null) {

                manager = UserChecker.checkUserLogin(getActivity());
                mTaskDownloadMovieInfo = new DownloadMovieInfo(this, getActivity(), manager, movie);
                mTaskDownloadMovieInfo.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }else
            updateMovie(mMovie);
        }


        return rootView;
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab,
                              FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        Log.d("trakt", "tab selected position: " + tab.getPosition());
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab,
                                FragmentTransaction fragmentTransaction) {
        Log.d("trakt", "tab unselected position: " + tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab,
                                FragmentTransaction fragmentTransaction) {
        Log.d("trakt", "tab reselected position: " + tab.getPosition());
    }




    private void updateMovie(Movie response) {
        if (getActivity() != null) {
            actionBar = getActivity().getActionBar();
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setSubtitle(response.title);

            mMovie = response;

            mViewPager = (ViewPager) rootView.findViewById(R.id.movie_pager);
            mSectionsPagerAdapter = new SectionsPagerAdapter(
                    getActivity().getSupportFragmentManager());
//        mSectionsPagerAdapter.notifyDataSetChanged();
            mViewPager.setAdapter(mSectionsPagerAdapter);
            mViewPager.setOffscreenPageLimit(2);
            for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
                // Create a tab with text corresponding to the page title defined by
                // the adapter. Also specify this Activity object, which implements
                // the TabListener interface, as the callback (listener) for when
                // this tab is selected.
                actionBar.addTab(actionBar.newTab()
                        .setText(mSectionsPagerAdapter.getPageTitle(i))
                        .setTabListener(this));
            }

            mViewPager
                    .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                        @Override
                        public void onPageSelected(int position) {
                            actionBar.setSelectedNavigationItem(position);
                        }
                    });
            setHasOptionsMenu(true);
            new CheckInChecker(getActivity(), manager, mUsername).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void onDownloadMovieInfoComplete(Movie response) {
         updateMovie(response);
    }


    /**
     * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {



        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle arguments = new Bundle();

            Log.d("trakt", "Fragment position: " + position);
            Fragment fragment = null;
            switch (position) {
                case 0:
                    arguments.putSerializable("movie", mMovie);
                    mInfoFragment = new MovieInfoFragment();
                    fragment=mInfoFragment;
                    break;
                case 1:
                    arguments.putSerializable("movie", mMovie);
                        fragment = new MovieCommentsFragment();
                    break;
                case 2:
                    arguments.putSerializable("movie", mMovie);
                    fragment = new MovieCastFragment();
                    break;

            }
            fragment.setArguments(arguments);
            return fragment;
        }

        @Override
        public int getCount() {

            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return "Info".toUpperCase(l);
                case 1:
                    return "Shouts".toUpperCase(l);
                case 2:
                    return "Cast".toUpperCase(l);
            }
            return null;
        }


    }


    private class MovieSeenUnseen extends AsyncTask<Void, Void, Void> {


        private Exception e;

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                if (mMovie.watched) {
                    Log.d("Trakt Fragments", "Changing to unseen");
                    return manager.movieService().unseen().movie(mMovie.title).fire();
                } else {
                    Log.d("Trakt Fragments", "Changing to seen");
                    return manager.movieService().seen().movie(mMovie.title, 1, new Date()).fire();
                }
            } catch (Exception e) {
                this.e = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(Void voids) {
            if (e == null) {
                Log.d("Trakt Fragments", "Updating seen status ui");
                mInfoFragment.updateSeenUnseen(MovieFragment.this);
            } else {
                Log.d("Trakt Fragments", "Error marking episode as unseen: " + e.getMessage());

            }
        }

    }


    private class MovieWatchlist extends AsyncTask<Void, Void, Void> {


        private Exception e;

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                if (mMovie.inWatchlist) {
                    Log.d("Trakt", "Adding to Unwatchlist");
                    return manager.movieService().unwatchlist().movie(mMovie.title).fire();
                } else {
                    Log.d("Trakt", "Adding to Watchlist");
                    return manager.movieService().watchlist().movie(mMovie.title).fire();
                }
            } catch (Exception e) {
                this.e = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(Void voids) {
            if (e == null) {
                Log.d("Trakt", "Updating watchlist status ui");
                mMovie.inWatchlist=!mMovie.inWatchlist;
                mInfoFragment.updateUI(mMovie);
                updateOptionsMenu(mMenu);
            } else {
                Log.d("Trakt", "Error changing watchlist status: " + e.getMessage());
            }
        }
    }

    private class MovieCheckIn extends AsyncTask<Void, Void, Response> {


        private Exception e;

        @Override
        protected Response doInBackground(Void... voids) {

            try {
                return manager.movieService().checkin(mMovie.title, mMovie.year).fire();


            } catch (Exception e) {
                this.e = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(Response response) {
            if (e == null) {
                Log.d("Trakt Fragments", "Checked in movie " + mMovie.title);
                new CheckInChecker(getActivity(), manager, mUsername).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                Log.d("Trakt Fragments", "Error marking episode as unseen: " + e.getMessage());

            }
        }
    }

}
