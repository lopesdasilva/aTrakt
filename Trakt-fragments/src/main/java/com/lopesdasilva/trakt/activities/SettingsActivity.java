package com.lopesdasilva.trakt.activities;

import android.app.Activity;
import android.os.Bundle;
import com.lopesdasilva.trakt.preferences.UserSettingFragment;

/**
 * Created by lopesdasilva on 21/05/14.
 */
public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new UserSettingFragment())
                .commit();
    }
}