package com.lopesdasilva.trakt.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lopesdasilva.trakt.R;

/**
 * Created by lopesdasilva on 26/05/13.
 */
public class UserActivityFragment extends Fragment {

    private View rootView;
    private WeekPager mWeekPagerAdapter;
    private ViewPager mViewPager;
    int middlePosition=1;
    int numberOfPositions=3;

    public UserActivityFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) // must put this in
            return null;
        rootView = inflater.inflate(R.layout.user_activity_fragment, container, false);
        Log.d("Trakt", "CalendarFragment On CreateView savedinstace:" + savedInstanceState);


        mViewPager = (ViewPager) rootView.findViewById(R.id.user_activity_pager);
        mWeekPagerAdapter = new WeekPager(getActivity().getSupportFragmentManager());
        mWeekPagerAdapter.notifyDataSetChanged();
        mViewPager.setAdapter(mWeekPagerAdapter);


        return rootView;
    }



    public class WeekPager extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {

        private int mCount;

        public WeekPager(FragmentManager fm) {
            super(fm);
            mCount = numberOfPositions;
        }

        @Override
        public Fragment getItem(int position) {
            Log.d("Trakt", "CalendarFragment get position: " + position);
            int onWeekInMIliSecconds = 604800000;
            Bundle arguments = new Bundle();
            Fragment fragment = new ActivityWeekFragment();
            arguments.putInt("calendardate", -1);
            fragment.setArguments(arguments);
            Log.d("Trakt", "getItem launching fragment");
            return fragment;
        }

        @Override
        public int getCount() {
            return mCount;
        }

        @Override
        public CharSequence getPageTitle(int position) {

           return "Week"+position;
        }

        @Override
        public void onPageScrolled(int i, float v, int i2) {
            Log.d("Trakt","onPageScrolled page selected"+i);
        }

        @Override
        public void onPageSelected(int i) {
            Log.d("Trakt","onPageSelected page selected"+i);
        }

        @Override
        public void onPageScrollStateChanged(int i) {
            Log.d("Trakt","onPageScrollStateChanged page selected"+i);
        }

    }


}
