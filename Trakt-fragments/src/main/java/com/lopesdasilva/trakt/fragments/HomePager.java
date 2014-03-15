package com.lopesdasilva.trakt.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lopesdasilva.trakt.R;

/**
 * Created by lopesdasilva on 04/06/13.
 */
public class HomePager extends Fragment {

    private View rootView;
    private ViewPager mViewPager;
    private HomePagerAdapter mWeekPagerAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.home_pager_fragment, container, false);

        setRetainInstance(true);
        if (savedInstanceState == null) {


            mViewPager = (ViewPager) rootView.findViewById(R.id.home_pager);
            mWeekPagerAdapter = new HomePagerAdapter(getActivity().getSupportFragmentManager());
            mWeekPagerAdapter.notifyDataSetChanged();
            mViewPager.setAdapter(mWeekPagerAdapter);

        }
        return rootView;
    }

    public class HomePagerAdapter extends FragmentPagerAdapter {


        public HomePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch(i){
                case 0:
                    return new ShowsFragment();
                case 1:
                    return new MoviesFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Tv Shows";
                case 1:
                    return "Movies";
            }
            return "";
        }
    }
}

