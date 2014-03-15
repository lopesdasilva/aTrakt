package com.lopesdasilva.trakt.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.fragments.CalendarFragment;

/**
 * Created by lopesdasilva on 26/05/13.
 */
public class CalendarActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_activity);


        //To get back button on actionbar
        getActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState==null){


            Bundle arguments = getIntent().getExtras();

            Fragment fragment= new CalendarFragment();
            fragment.setArguments(arguments);
            Log.d("Trakt", "Launching new fragment CalendarFragment");
            getSupportFragmentManager().beginTransaction().replace(R.id.calendar_activity, fragment).commit();

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
