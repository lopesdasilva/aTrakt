package com.lopesdasilva.trakt.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lopesdasilva.trakt.R;

/**
 * Created by lopesdasilva on 03/06/13.
 */
public class WatchlistPagerFragment extends Fragment {

    private View rootView;
    private ViewPager mViewPager;
    private WatchlistPager mWeekPagerAdapter;

    public WatchlistPagerFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.watchlist_pager_fragment, container, false);

        getActivity().getActionBar().setSubtitle("Watchlist");

        if (savedInstanceState == null) {
            setRetainInstance(true);

            mViewPager = (ViewPager) rootView.findViewById(R.id.watchlistPager);
            mWeekPagerAdapter = new WatchlistPager(getActivity().getSupportFragmentManager());
            mWeekPagerAdapter.notifyDataSetChanged();
            mViewPager.setAdapter(mWeekPagerAdapter);
            mViewPager.setOffscreenPageLimit(mWeekPagerAdapter.getCount());
        }


        return rootView;
    }

    public class WatchlistPager extends FragmentStatePagerAdapter {

        public WatchlistPager(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int i) {


            Fragment fragment=null;

           switch (i){
               case 0:
                   fragment=new WatchlistShowsFragment();
                   break;
               case 1:
                   fragment=new WatchlistEpisodesFragment();
                   break;
               case 2:
                   fragment=new WatchlistMoviesFragment();
                   break;
           }
            fragment.setArguments(getArguments());
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                   return "Tv Shows";
                case 1:
                    return "Episodes";
                case 2:
                   return "Movies";
            }
            return null;
        }

            @Override
        public int getCount() {
            //5 types of search
            return 3;
        }
    }
}
