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

    int middlePosition = 1;
    int numberOfPositions = 10;
    private View rootView;
    private WeekPager mWeekPagerAdapter;
    private ViewPager mViewPager;
    final int ONE_WEEK_IN_MILLISECONDS = 604800000;

    public UserActivityFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) // must put this in
            return null;
        rootView = inflater.inflate(R.layout.user_activity_fragment, container, false);
        Log.d("Trakt", "CalendarFragment On CreateView saved instance:" + savedInstanceState);


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
            Bundle arguments = new Bundle();
            Fragment fragment = new ActivityWeekFragment();


            arguments.putInt("start_ts", (position+1)* ONE_WEEK_IN_MILLISECONDS);
            arguments.putInt("end_ts", position* ONE_WEEK_IN_MILLISECONDS);

            fragment.setArguments(arguments);
            if(position==mCount-1) {
                Log.d("Trakt", "Last tab should increase..");
//                mCount++;
//                this.notifyDataSetChanged();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return mCount;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title;
            switch (position) {
                case 0:
                    title = "this week";
                    break;
                case 1:
                    title ="last week";
                    break;
                default:
                    title = position + " weeks ago";
            }

            return title;
        }

        @Override
        public void onPageScrolled(int i, float v, int i2) {
            Log.d("Trakt", "onPageScrolled page selected" + i);
        }

        @Override
        public void onPageSelected(int i) {
            Log.d("Trakt", "onPageSelected page selected" + i);
        }

        @Override
        public void onPageScrollStateChanged(int i) {
            Log.d("Trakt", "onPageScrollStateChanged page selected" + i);
        }

    }


}
