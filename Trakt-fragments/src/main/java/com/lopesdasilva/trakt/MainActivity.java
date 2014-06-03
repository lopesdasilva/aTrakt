package com.lopesdasilva.trakt;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import com.androidquery.util.AQUtility;
import com.lopesdasilva.trakt.activities.SettingsActivity;
import com.lopesdasilva.trakt.alarms.MyAlarmReceiver;
import com.lopesdasilva.trakt.extras.UserChecker;
import com.lopesdasilva.trakt.fragments.CalendarFragment;
import com.lopesdasilva.trakt.fragments.HomeFragment;
import com.lopesdasilva.trakt.fragments.LibraryPagerFragment;
import com.lopesdasilva.trakt.fragments.RecommendedFragment;
import com.lopesdasilva.trakt.fragments.UserActivityFragment;
import com.lopesdasilva.trakt.fragments.WatchlistPagerFragment;

public class MainActivity extends FragmentActivity {

    public static final String PREFS_NAME = "TraktPrefsFile";

    private TVtraktApp appState;
    private LinearLayout mDrawerLeft;
    private String[] mPlanetTitles;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private ListView mDrawerList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        //NAVIGATION DRAWER

        mTitle = mDrawerTitle = getTitle();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerLeft = (LinearLayout) findViewById(R.id.left_drawer);

        mDrawerList = (ListView) findViewById(R.id.left);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mPlanetTitles = getResources().getStringArray(R.array.navigation_drawer);


        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mPlanetTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);


        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        if (UserChecker.checkUserLogin(this) != null) {
            if (savedInstanceState == null) {
                selectItem(0);


//            UserChecker.saveUserAndPassword(this, mUsername, mPassword);

                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                boolean firstRun = settings.getBoolean("logged", false);

                if (!firstRun) {
                    Log.d("Trakt it", "it is first run scheduling alarm for shows tonight.");
                    //RUN ONCE!
                    Bundle bundle = new Bundle();
// add extras here..
                    MyAlarmReceiver alarm = new MyAlarmReceiver(this, bundle, 24);

                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("logged", true);
                    editor.commit();


                }
            }

        }


//        findViewById(R.id.buttonEpisodeActivity).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(MainActivity.this, "Launching Episode Activity", Toast.LENGTH_SHORT).movie();
//                Log.d("Trakt", "Launching Episode Activity");
//
//
//                Bundle arguments = new Bundle();
//                arguments.putString("show_imdb", "tt0944947");
//                arguments.putInt("show_season", 3);
//                arguments.putInt("show_episode", 7);
//                Intent i = new Intent(getBaseContext(), EpisodeActivity.class);
//                i.putExtras(arguments);
//
//                startActivity(i);
//                overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);
//            }
//        });
//
//        findViewById(R.id.buttonShowActivity).setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                Bundle arguments = new Bundle();
//                arguments.putString("show_imdb", "tt2188671");
//
//                Intent i = new Intent(getBaseContext(), ShowActivity.class);
//                i.putExtras(arguments);
//
//                startActivity(i);
//                overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);
//            }
//        });
//
//        findViewById(R.id.buttonMovieActivity).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(MainActivity.this, "Launching Movie Activity", Toast.LENGTH_SHORT).movie();
//                Log.d("Trakt Fragments", "Launching Movie Activity");
//
//
//                Bundle arguments = new Bundle();
//                arguments.putString("movie_imdb", "tt1853728");
//                Intent i = new Intent(getBaseContext(), MovieActivity.class);
//                i.putExtras(arguments);
//
//                startActivity(i);
//                overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);
//            }
//        });
//
//        findViewById(R.id.buttonCalendar).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(MainActivity.this, "Launching Calendar Activity", Toast.LENGTH_SHORT).movie();
//                Log.d("Trakt", "Launching Calendar Activity");
//
//
//                Intent i = new Intent(getBaseContext(), CalendarActivity.class);
//                startActivity(i);
//                overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);
//            }
//        });
//
//        findViewById(R.id.buttonEpisodesTonight).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(MainActivity.this, "Launching Tonight Episodes Activity", Toast.LENGTH_SHORT).movie();
//                Log.d("Trakt", "Launching Tonight Episodes Activity");
//
//
//                Intent i = new Intent(getBaseContext(), EpisodesTonightActivity.class);
//                startActivity(i);
//                overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);
//            }
//        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerLeft);
        menu.findItem(R.id.search).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        switch (item.getItemId()) {
            case R.id.search:
                return true;
            case R.id.settings:
                Intent i = new Intent(getBaseContext(), SettingsActivity.class);
//                i.putExtras(arguments);
//
                startActivity(i);
//                Toast.makeText(this,"SETTINGS",Toast.LENGTH_SHORT).show();
                return true;
            default:
                if (mDrawerToggle.onOptionsItemSelected(item)) {
                    return true;
                }else
                    return super.onOptionsItemSelected(item);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }


    private void selectItem(int position) {
        mDrawerList.setItemChecked(position, true);
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerLeft);
        Fragment fragment;
        switch (position) {
            case 0:
//                fragment = getSupportFragmentManager().findFragmentByTag("trending");
//                if (fragment == null)
                fragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, "trending").commit();
                break;
            case 1:
//                fragment = getSupportFragmentManager().findFragmentByTag("library");
//                if (fragment == null)
                fragment = new LibraryPagerFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, "library").commit();
                break;
            case 2:
//                fragment = getSupportFragmentManager().findFragmentByTag("recommended");
//                if (fragment == null)
                fragment = new RecommendedFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, "recommended").commit();
                break;
            case 3:
                fragment = new WatchlistPagerFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, "watchlist").commit();

                break;
            case 4:
//                fragment = getSupportFragmentManager().findFragmentByTag("calendar");
//                if (fragment == null)
                fragment = new CalendarFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, "calendar").commit();
                break;
            case 5:
//                fragment = getSupportFragmentManager().findFragmentByTag("calendar");
//                if (fragment == null)
                fragment = new UserActivityFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, "UserActivity").commit();
                break;

        }


        // update the main content by replacing fragments
//        Fragment fragment = new PlanetFragment();
//        Bundle args = new Bundle();
//        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
//        fragment.setArguments(args);
//
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();


    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy(){

        super.onDestroy();

        if(isTaskRoot()){

            //clean the file cache with advance option
            long triggerSize = 3000000; //starts cleaning when cache size is larger than 3M
            long targetSize = 2000000;      //remove the least recently used files until cache size is less than 2M
            AQUtility.cleanCacheAsync(this, triggerSize, targetSize);
        }

    }

}
