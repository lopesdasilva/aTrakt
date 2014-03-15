package com.lopesdasilva.trakt.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lopesdasilva.trakt.R;

/**
 * Created by lopesdasilva on 03/06/13.
 */
public class SearchFragment extends Fragment {

    private View rootView;
    private ViewPager mViewPager;
    private SearchPager mWeekPagerAdapter;

    public SearchFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.search_fragment, container, false);

        if (savedInstanceState == null) {
            setRetainInstance(true);

            mViewPager = (ViewPager) rootView.findViewById(R.id.searchPager);
            mWeekPagerAdapter = new SearchPager(getActivity().getSupportFragmentManager());
            mWeekPagerAdapter.notifyDataSetChanged();
            mViewPager.setAdapter(mWeekPagerAdapter);

        }


        return rootView;
    }

    public class SearchPager extends FragmentPagerAdapter {

        public SearchPager(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int i) {


            Fragment fragment=null;

           switch (i){
               case 0:
                   fragment=new SearchShowFragment();
                   break;
               case 1:
                   fragment=new SearchMovieFragment();
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
                   return "Movies";
            }
            return null;
        }

            @Override
        public int getCount() {
            //5 types of search
            return 2;
        }
    }
}
