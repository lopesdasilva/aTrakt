package com.lopesdasilva.trakt.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.jakewharton.trakt.ServiceManager;
import com.lopesdasilva.trakt.R;

import java.text.SimpleDateFormat;

/**
 * Created by lopesdasilva on 26/05/13.
 */
public class CalendarFragment extends Fragment {

    private View rootView;
    private WeekPager mWeekPagerAdapter;
    private ViewPager mViewPager;
    int middlePosition=3;
    int numberOfPositions=7;

    public CalendarFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) // must put this in
            return null;
        rootView = inflater.inflate(R.layout.calendar_fragment, container, false);
        Log.d("Trakt", "CalendarFragment On CreateView savedinstace:" + savedInstanceState);


        mViewPager = (ViewPager) rootView.findViewById(R.id.calendar_pager);
        mWeekPagerAdapter = new WeekPager(getActivity().getSupportFragmentManager());
        mWeekPagerAdapter.notifyDataSetChanged();
        mViewPager.setAdapter(mWeekPagerAdapter);
        mViewPager.setCurrentItem(middlePosition);
        mViewPager.setOffscreenPageLimit(numberOfPositions);


        return rootView;
    }

    public class WeekPager extends FragmentStatePagerAdapter {

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
            Fragment fragment = new CalendarWeekFragment();
            Log.d("Trakt", "current Position: " + position);
            if (position < middlePosition) {
                arguments.putInt("calendardate", -onWeekInMIliSecconds * (mCount - position - middlePosition-1));

            } else if (position > middlePosition) {
                arguments.putInt("calendardate", onWeekInMIliSecconds * (position - middlePosition));

            } else
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

            if (position == middlePosition)
                return "this week";
            else if (position == middlePosition-1)
                return "last week";
            else if (position == middlePosition+1)
                return "next week";
            else {
                if (position < middlePosition) {
                    int aux_position = mCount - position - middlePosition;
                    return aux_position + " weeks ago";
                } else
                    return String.valueOf(position - middlePosition) + " weeks ahead";
            }

        }
    }


}
