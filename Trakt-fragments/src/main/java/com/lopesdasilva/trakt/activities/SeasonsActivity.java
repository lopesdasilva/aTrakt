package com.lopesdasilva.trakt.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.fragments.SeasonsFragment;

/**
 * Created by lopesdasilva on 22/05/13.
 */
public class SeasonsActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seasons_activity);

        getActionBar().setDisplayHomeAsUpEnabled(true);


        if (savedInstanceState == null) {

            Bundle arguments = getIntent().getExtras();
            Fragment fragment = new SeasonsFragment();
            fragment.setArguments(arguments);
            Log.d("Trakt", "Launching new fragment SeasonsFragment");
            getSupportFragmentManager().beginTransaction().replace(R.id.seasons_activity, fragment).commit();
        }
    }
}