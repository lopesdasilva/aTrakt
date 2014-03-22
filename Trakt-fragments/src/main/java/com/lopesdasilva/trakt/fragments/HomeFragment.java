package com.lopesdasilva.trakt.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lopesdasilva.trakt.R;

/**
 * Created by lopesdasilva on 07/06/13.
 */
public class HomeFragment extends Fragment {


    private View rootView;

    public HomeFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.home_fragment, container, false);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(R.id.viewTrendingShows, new ShowsFragment(), "trending_shows").commit();
            getFragmentManager().beginTransaction().replace(R.id.viewTrendingMovies, new MoviesFragment(), "trending_movies").commit();
            getFragmentManager().beginTransaction().replace(R.id.viewSocial, new FriendsFragment()).commit();
        }



        return rootView;

    }

}
