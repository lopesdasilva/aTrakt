package com.lopesdasilva.trakt.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;

import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.fragments.RegisterFragment;


/**
 * Created by lopesdasilva on 17/05/13.
 */
public class RegisterActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        //To get back button on actionbar
        getActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState==null){

    //THIS IS STATIC


            Bundle arguments = getIntent().getExtras();
            Fragment fragment = new RegisterFragment();
            fragment.setArguments(arguments);
            Log.d("Trakt", "Launching new RegisterFragment");
            getSupportFragmentManager().beginTransaction().replace(R.id.register_activity, fragment).commit();

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(getBaseContext(), LoginActivity.class));
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
