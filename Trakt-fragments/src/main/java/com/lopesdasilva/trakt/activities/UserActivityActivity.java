package com.lopesdasilva.trakt.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;

import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.fragments.UserActivityFragment;

/**
 * Created by lopesdasilva on 26/05/13.
 */
public class UserActivityActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_activity);


        //To get back button on actionbar
        getActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState==null){


            Bundle arguments = getIntent().getExtras();

            Fragment fragment= new UserActivityFragment();
            fragment.setArguments(arguments);
            Log.d("Trakt", "Launching new fragment UserActivity Fragment");
            getSupportFragmentManager().beginTransaction().replace(R.id.user_activity_activity, fragment).commit();

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
