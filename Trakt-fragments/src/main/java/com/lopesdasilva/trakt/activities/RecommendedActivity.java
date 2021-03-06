package com.lopesdasilva.trakt.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;

import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.fragments.RecommendedShowsFragment;

/**
 * Created by lopesdasilva on 02/07/13.
 */
public class RecommendedActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommended_activity);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setIcon(R.drawable.logo_black);

        if (savedInstanceState == null) {
            Log.d("Trakt", "Launching Fragment");
            Fragment fragment = new RecommendedShowsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.linearLayoutShowsActivity, fragment).commit();
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
