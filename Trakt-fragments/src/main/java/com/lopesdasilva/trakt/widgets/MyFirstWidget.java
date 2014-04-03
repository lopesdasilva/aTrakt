package com.lopesdasilva.trakt.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.CalendarDate;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.Tasks.DownloadDayCalendar;
import com.lopesdasilva.trakt.extras.UserChecker;

import java.util.Date;
import java.util.List;

/**
 * Created by NB20308 on 03-04-2014.
 */
public class MyFirstWidget extends AppWidgetProvider implements DownloadDayCalendar.OnDayCalendarTaskCompleted {

    private ServiceManager manager;
    private AppWidgetManager appWidgetManager;
    private int[] appWidgetIds;
    private Context context;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
       this.appWidgetManager=appWidgetManager;
        this.appWidgetIds=appWidgetIds;
        this.context=context;
        Toast.makeText(context,"updating widget trakt",Toast.LENGTH_SHORT).show();
        Log.d("Trakt it", "TODO CODE TO UDPATE THE LIST");
        manager = UserChecker.checkUserLogin(context);
        new DownloadDayCalendar(this, manager, new Date()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);



    }

    @Override
    public void OnDayCalendarTaskCompleted(List<CalendarDate> response) {
        Log.d("Trakt it", "WIDGET: Download complete START TO UPDATE");
        final int N = appWidgetIds.length;
        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.example_appwidget);
            AQuery aq = new AQuery(context);

views.setRemoteAdapter();



//           views.setImageViewBitmap(R.id.imageViewWidgetScreen, aq.getCachedImage("http://slurm.trakt.us/images/episodes/15859-3-3-218.jpg"));
            // Get the layout for the App Widget and attach an on-click listener
            // to the button

//            views.setOnClickPendingIntent(R.id.button, pendingIntent);
//
            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    public class EpisodeTonightAdapter extends BaseAdapter {

        private final List<CalendarDate.CalendarTvShowEpisode> mListEpisodes;
        private final LayoutInflater inflater;

        public EpisodeTonightAdapter(Context context, List<CalendarDate.CalendarTvShowEpisode> mListepisodes) {
            this.mListEpisodes = mListepisodes;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mListEpisodes.size();
        }

        @Override
        public CalendarDate.CalendarTvShowEpisode getItem(int i) {
            return mListEpisodes.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.widget_tonight_item, parent, false);
            }
            AQuery aq = new AQuery(convertView);

            aq.id(R.id.imageViewDrawerEpisodeScreen).image(mListEpisodes.get(position).episode.images.screen, true, true, 0, 0, null, AQuery.FADE_IN);
            aq.id(R.id.textViewDrawerEpisodeTitle).text(mListEpisodes.get(position).episode.title);
            aq.id(R.id.textViewDrawerEpisodeNumber).text("S" + mListEpisodes.get(position).episode.season + "E" + mListEpisodes.get(position).episode.number);
            aq.id(R.id.textViewDrawerEpisodeDate).text(mListEpisodes.get(position).show.title);
            aq.id(R.id.textViewDrawerEpisodeNetwork).text(mListEpisodes.get(position).show.network);
            if (mListEpisodes.get(position).episode.watched)
                aq.id(R.id.imageViewDrawerEpisodeSeenTag).visible();
            else
                aq.id(R.id.imageViewDrawerEpisodeSeenTag).gone();
            return convertView;
        }
    }

}
