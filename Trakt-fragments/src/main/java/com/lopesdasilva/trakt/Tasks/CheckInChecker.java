package com.lopesdasilva.trakt.Tasks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.ActivityItemBase;
import com.jakewharton.trakt.entities.TvEntity;
import com.lopesdasilva.trakt.extras.CreateNotifications;

/**
 * Created by lopesdasilva on 18/05/13.
 */
public class CheckInChecker extends AsyncTask<Void, Void, ActivityItemBase> {


    private final ServiceManager mManager;
    private final String mUsername;
    private final Activity mActivity;
    private Exception mException;
    private ActivityItemBase mActivityBase;

    public CheckInChecker(FragmentActivity activity, ServiceManager manager, String username) {
        mActivity = activity;
        mManager = manager;
        mUsername = username;
    }

    @Override
    protected ActivityItemBase doInBackground(Void... voids) {
        try {
            return mManager.userService().watching(mUsername).fire();
        } catch (Exception e) {
            mException = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(final ActivityItemBase response) {
        mActivityBase = response;
        if (mException == null) {
            //Check data received if user is watching
            if (response != null) {
                AQuery aq = new AQuery(mActivity);
                switch (response.type) {
                    case All:
                        break;
                    case Episode:

                        aq.ajax(response.episode.images.screen, Bitmap.class, new AjaxCallback<Bitmap>() {
                            @Override
                            public void callback(String url, Bitmap object, AjaxStatus status) {

                                CreateNotifications.EpisodeNotification((FragmentActivity) mActivity, new TvEntity(response.show, response.episode), object);
                            }
                        });
                        break;
                    case Show:
                        break;
                    case Movie:

                        aq.ajax(response.movie.images.fanart, Bitmap.class, new AjaxCallback<Bitmap>() {

                            @Override
                            public void callback(String url, Bitmap object, AjaxStatus status) {


                                Toast.makeText(mActivity, "Watching a movie", Toast.LENGTH_SHORT).show();
                                CreateNotifications.MovieNotification((FragmentActivity) mActivity, response.movie, object);

                            }
                        });


                        break;
                    case List:
                        break;
                }
            }
        } else {
            //TODO ERROR handler
        }

    }


}
