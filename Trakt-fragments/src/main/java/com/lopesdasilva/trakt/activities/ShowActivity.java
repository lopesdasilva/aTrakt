package com.lopesdasilva.trakt.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.fragments.ShowFragment;

/**
 * Created by lopesdasilva on 22/05/13.
 */
public class ShowActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_activity);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {

            Bundle arguments = getIntent().getExtras();
            Fragment fragment = new ShowFragment();
            fragment.setArguments(arguments);
            Log.d("Trakt", "Launching new fragment ShowFragment");
            getSupportFragmentManager().beginTransaction().replace(R.id.show_activity, fragment).commit();
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
