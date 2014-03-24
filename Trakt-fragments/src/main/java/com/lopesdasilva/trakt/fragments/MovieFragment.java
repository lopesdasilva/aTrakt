package com.lopesdasilva.trakt.fragments;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.Movie;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.Tasks.*;
import com.lopesdasilva.trakt.extras.UserChecker;

import java.util.Locale;

public class MovieFragment extends Fragment implements ActionBar.TabListener, DownloadMovieInfo.OnMovieTaskCompleted {

    private View rootView;
    private ServiceManager manager;
    private DownloadMovieInfo mTaskDownloadMovieInfo;
    private ActionBar actionBar;
    private Movie mMovie;
    private Menu mMenu;
    private MovieInfoFragment mInfoFragment;

    public MovieFragment() {

    }


    SectionsPagerAdapter mSectionsPagerAdapter;


    ViewPager mViewPager;


    public String movie;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("mShow", mMovie);
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.mMenu = menu;
        updateOptionsMenu(mMenu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
//                new RemoveShowFromWatchlist(getActivity(),MovieFragment.this,manager, mMovie,0).execute();
                return true;
            case 1:
//                new AddShowToWatchlist(getActivity(),MovieFragment.this,manager, mMovie,0).execute();
                return true;
            case 2:
//                new UnrateShow(getActivity(),MovieFragment.this,manager, mMovie,0).execute();

                return true;
            case 3:
//                new RateShowHate(getActivity(),MovieFragment.this,manager, mMovie,0).execute();

                return true;
            case 4:
//                new RateShowLove(getActivity(),MovieFragment.this,manager, mMovie,0).execute();
                return true;
            case 5:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, mMovie.title);
                i.putExtra(Intent.EXTRA_TEXT, mMovie.url);
                startActivity(Intent.createChooser(i, "Share "+ mMovie.title));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateOptionsMenu(Menu menu){
        menu.clear();
        if(mMovie.inWatchlist!=null )
            if(mMovie.inWatchlist)
        menu.add(0, 0, 0,  R.string.remove_watchlist);
        else
        menu.add(0, 1, 1, R.string.watchlist);
        if(mMovie.rating!=null){
            switch (mMovie.rating){

                case Love:
                    menu.add(0, 2, 2,  R.string.unrate);
                    menu.add(0, 3, 3,  R.string.hated);
                    break;
                case Hate:
                    menu.add(0, 2, 2,  R.string.unrate);
                    menu.add(0, 4, 4,  R.string.loved);
                    break;
            }
        }else{
            menu.add(0, 3, 3,  R.string.hated);
            menu.add(0, 4, 4,  R.string.loved);
        }

      //  menu.add(0, 4, 4, R.string.seen);
        menu.add(0,5,5, R.string.share).setIcon(android.R.drawable.ic_menu_share).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.movie_fragment, container, false);




        if (savedInstanceState == null) {
            movie = getArguments().getString("movie_imdb");

            manager = UserChecker.checkUserLogin(getActivity());


            Log.d("Trakt", "ServiceManager: " + manager);
            Log.d("Trakt", "movie_imdb received: " + movie);

            mTaskDownloadMovieInfo = new DownloadMovieInfo(this, getActivity(), manager, movie);
            mTaskDownloadMovieInfo.execute();
        } else {
            mMovie = (Movie) savedInstanceState.getSerializable("mShow");
            updateMovie(mMovie);
            if (mMovie == null) {

                manager = UserChecker.checkUserLogin(getActivity());
                mTaskDownloadMovieInfo = new DownloadMovieInfo(this, getActivity(), manager, movie);
                mTaskDownloadMovieInfo.execute();

            }
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
            mViewPager.setOffscreenPageLimit(3);
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
                        fragment = new MovieInfoFragment();
                    break;
                case 2:
                    arguments.putSerializable("moviepeople", mMovie.people);
                    fragment = new MovieInfoFragment();
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


}
