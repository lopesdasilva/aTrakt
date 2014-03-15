package com.lopesdasilva.trakt.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.fragments.MoviesAllFragment;
import com.lopesdasilva.trakt.fragments.ShowsAllFragment;

/**
 * Created by lopesdasilva on 19/06/13.
 */
public class MoviesActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movies_activity);

        //To get back button on actionbar
        getActionBar().setDisplayHomeAsUpEnabled(true);


        if (savedInstanceState == null) {
            Log.d("Trakt", "Launching Fragment");
            Fragment fragment = new MoviesAllFragment();
//        fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().replace(R.id.linearLayoutMoviesActivity, fragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
