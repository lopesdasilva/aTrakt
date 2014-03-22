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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.RatingResponse;
import com.jakewharton.trakt.entities.TvShow;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.Tasks.AddShowToWatchlist;
import com.lopesdasilva.trakt.Tasks.DownloadShowInfo;
import com.lopesdasilva.trakt.Tasks.RateShowHate;
import com.lopesdasilva.trakt.Tasks.RateShowLove;
import com.lopesdasilva.trakt.Tasks.RemoveShowFromWatchlist;
import com.lopesdasilva.trakt.Tasks.UnrateShow;
import com.lopesdasilva.trakt.extras.UserChecker;

import java.util.Locale;

public class ShowFragment extends Fragment implements ActionBar.TabListener, DownloadShowInfo.onShowInfoTaskComplete, RateShowLove.OnRatingShowLoveCompleted, UnrateShow.OnUnratingShowCompleted, RateShowHate.OnRatingShowHateCompleted, AddShowToWatchlist.OnAddShowToWatchlistCompleted, RemoveShowFromWatchlist.OnRemovingShowFromWatchlistCompleted {

    private View rootView;
    private ServiceManager manager;
    private DownloadShowInfo mTaskDownloadShowInfo;
    private ActionBar actionBar;
    private TvShow mTVshow;
    private Menu mMenu;
    private ShowInfoFragment mInfoFragment;

    public ShowFragment() {

    }


    SectionsPagerAdapter mSectionsPagerAdapter;


    ViewPager mViewPager;


    public String show;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("mShow", mTVshow);
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.mMenu = menu;
        updateOptionsMenu(mMenu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                new RemoveShowFromWatchlist(getActivity(),ShowFragment.this,manager,mTVshow,0).execute();
                return true;
            case 1:
                new AddShowToWatchlist(getActivity(),ShowFragment.this,manager,mTVshow,0).execute();
                return true;
            case 2:
                new UnrateShow(getActivity(),ShowFragment.this,manager,mTVshow,0).execute();

                return true;
            case 3:
                new RateShowHate(getActivity(),ShowFragment.this,manager,mTVshow,0).execute();

                return true;
            case 4:
                new RateShowLove(getActivity(),ShowFragment.this,manager,mTVshow,0).execute();
                return true;
            case 5:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, mTVshow.title);
                i.putExtra(Intent.EXTRA_TEXT, mTVshow.url);
                startActivity(Intent.createChooser(i, "Share "+mTVshow.title));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateOptionsMenu(Menu menu){
        menu.clear();
        if(mTVshow.inWatchlist!=null )
            if(mTVshow.inWatchlist)
        menu.add(0, 0, 0,  R.string.remove_watchlist);
        else
        menu.add(0, 1, 1, R.string.watchlist);
        if(mTVshow.rating!=null){
            switch (mTVshow.rating){

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
        rootView = inflater.inflate(R.layout.show_fragment, container, false);




        if (savedInstanceState == null) {
            show = getArguments().getString("show_imdb");

            manager = UserChecker.checkUserLogin(getActivity());


            Log.d("Trakt", "ServiceManager: " + manager);
            Log.d("Trakt", "Show_imdb received: " + show);

            mTaskDownloadShowInfo = new DownloadShowInfo(this, getActivity(), manager, show);
            mTaskDownloadShowInfo.execute();
        } else {
            mTVshow = (TvShow) savedInstanceState.getSerializable("mShow");
            updateShow(mTVshow);
            if (mTVshow == null) {

                manager = UserChecker.checkUserLogin(getActivity());
                mTaskDownloadShowInfo = new DownloadShowInfo(this, getActivity(), manager, show);
                mTaskDownloadShowInfo.execute();

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

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        if (actionBar != null)
//            actionBar.removeAllTabs();
//        Log.d("trakt", " Detaching ShowFragment should Remove all tabs");
//    }

    @Override
    public void onShowInfoTaskComplete(TvShow response) {

        Log.d("Trakt", "Download show info complete");
        updateShow(response);



    }

    private void updateShow(TvShow response) {
        if (getActivity() != null) {
            actionBar = getActivity().getActionBar();
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setSubtitle(response.title);

            mTVshow = response;

            mViewPager = (ViewPager) rootView.findViewById(R.id.show_pager);
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
    public void OnRatingShowLoveCompleted(int position, RatingResponse response) {
        mTVshow.rating=response.rating;
        updateOptionsMenu(mMenu);
        mInfoFragment.updateInfo(mTVshow);
    }

    @Override
    public void OnUnratingShowCompleted(int position, RatingResponse response) {
        mTVshow.rating=response.rating;
        updateOptionsMenu(mMenu);
        mInfoFragment.updateInfo(mTVshow);
    }

    @Override
    public void OnRatingShowHateCompleted(int position, RatingResponse response) {
        mTVshow.rating=response.rating;
        updateOptionsMenu(mMenu);
        mInfoFragment.updateInfo(mTVshow);
    }

    @Override
    public void OnAddShowToWatchlistCompleted(int position) {
        mTVshow.inWatchlist=true;
        updateOptionsMenu(mMenu);
        mInfoFragment.updateInfo(mTVshow);
    }

    @Override
    public void OnRemovingShowfromWatchlistCompleted(int position) {
        mTVshow.inWatchlist=false;
        updateOptionsMenu(mMenu);
        mInfoFragment.updateInfo(mTVshow);
    }

    /**
     * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private ShowCommentsFragment commentFragment;

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
                    arguments.putSerializable("show", mTVshow);
                    mInfoFragment = new ShowInfoFragment();
                    fragment=mInfoFragment;
                    break;
                case 1:
                    arguments.putSerializable("show", mTVshow);
                    fragment = new SeasonsFragment();
                    break;
                case 2:
                    arguments.putSerializable("show", mTVshow);

                        fragment = new ShowCommentsFragment();

                    break;
                case 3:
                    arguments.putSerializable("showpeople", mTVshow.people);
                    fragment = new ShowCastFragment();
                    break;

            }
            fragment.setArguments(arguments);
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return "Info".toUpperCase(l);
                case 1:
                    return "Seasons".toUpperCase(l);
                case 2:
                    return "Shouts".toUpperCase(l);
                case 3:
                    return "Cast".toUpperCase(l);
            }
            return null;
        }


    }


}
