package com.lopesdasilva.trakt.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;

import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.fragments.SearchFragment;

/**
 * Created by lopesdasilva on 03/06/13.
 */
public class SearchActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Log.d("Trakt", "Launching Search Activity");

            handleIntent(getIntent());

        }

    }


    @Override
    protected void onNewIntent(Intent intent) {

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);


            Bundle arguments = new Bundle();
            arguments.putString("query", query);
            Fragment fragment = new SearchFragment();
            fragment.setArguments(arguments);
            Log.d("Trakt", "Launching new fragment SearchFragment");
            getSupportFragmentManager().beginTransaction().replace(R.id.search_activity, fragment).commit();


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
