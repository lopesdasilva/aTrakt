package com.lopesdasilva.trakt.preferences;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.lopesdasilva.trakt.R;

/**
 * Created by lopesdasilva on 21/05/14.
 */
public class UserSettingFragment extends PreferenceFragment {


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
