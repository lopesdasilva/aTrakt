package com.lopesdasilva.trakt.activities;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;

import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.fragments.MovieFragment;

import java.net.URL;


/**
 * Created by lopesdasilva on 17/05/13.
 */
public class MovieActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_activity);

        //To get back button on actionbar
        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {




            Bundle arguments = getIntent().getExtras();

            Intent intent = getIntent();
            Uri data = intent.getData();
            URL url = null;
            if (data != null) {
                try {
                    url = new URL(data.getScheme(), data.getHost(),
                            data.getPath());
                    String s_url = url.getPath();


                    String[] name_temp = s_url.split("/movie/");
                    String movie_name = name_temp[1];
                    arguments.putString("movie_imdb",movie_name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Fragment fragment = new MovieFragment();
            fragment.setArguments(arguments);
            Log.d("Trakt", "Launching new fragment MovieFragment");
            getSupportFragmentManager().beginTransaction().replace(R.id.movie_activity, fragment).commit();

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
