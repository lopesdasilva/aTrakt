package com.lopesdasilva.trakt.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;

import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.fragments.ShowFragment;

import java.net.URL;

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

            Intent intent = getIntent();
            Uri data = intent.getData();
            URL url = null;
            if (data != null) {
                try {
                    url = new URL(data.getScheme(), data.getHost(),
                            data.getPath());
                } catch (Exception e) {


                    e.printStackTrace();
                }

            String s_url = url.getPath();
            String[] name_temp = s_url.split("/show/");
            String show_imdb = name_temp[1];
            Log.d("trakt it", "url: " + s_url);
            arguments.putString("show_imdb", show_imdb);
            if (show_imdb.contains("episode")) {
                String show_aux = show_imdb.substring(0, show_imdb.indexOf("/"));
                arguments.putString("show_imdb", show_aux);
                //split season
                if(show_imdb.contains("/specials/")){
                    arguments.putInt("show_season", 0);
                }else {

                    String[] season_aux = show_imdb.split("/season/");

                    Log.d("trakt id", "season: " + season_aux[1].substring(0, season_aux[1].indexOf("/")));
                    arguments.putInt("show_season", Integer.parseInt(season_aux[1].substring(0, season_aux[1].indexOf("/"))));
                }
                String[] episode_aux = show_imdb.split("/episode/");
                Log.d("trakt id", "episode: " + episode_aux[1]);

                Intent i = new Intent(getBaseContext(), EpisodeActivity.class);

                arguments.putInt("show_episode", Integer.parseInt(episode_aux[1]));
                i.putExtras(arguments);

                startActivity(i);
                finish();
            } else {
                if (show_imdb.contains("season")) {
                    String show_aux = show_imdb.substring(0, show_imdb.indexOf("/"));
                    arguments.putString("show_imdb", show_aux);
                }
            }
            }
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
