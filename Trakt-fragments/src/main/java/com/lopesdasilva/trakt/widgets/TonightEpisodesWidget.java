package com.lopesdasilva.trakt.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.CalendarDate;
import com.lopesdasilva.trakt.MainActivity;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.Tasks.DownloadDayCalendar;
import com.lopesdasilva.trakt.activities.EpisodeActivity;
import com.lopesdasilva.trakt.extras.UserChecker;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by NB20308 on 03-04-2014.
 */
public class TonightEpisodesWidget extends AppWidgetProvider {

    public static final String TOAST_ACTION = "com.example.android.stackwidget.TOAST_ACTION";
    public static final String UPDATE_ACTION = "com.example.android.stackwidget.UPDATE_ACTION";


    public AppWidgetManager appWidgetManager;
    private ServiceManager manager;
    private int[] appWidgetIds;
    private Context context;
    private int appWidgetId;


    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        if (intent.getAction().equals(TOAST_ACTION)) {
            appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);

            Intent episodeIntent = new Intent(context, EpisodeActivity.class);
            episodeIntent.putExtras(intent.getExtras());
            episodeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(episodeIntent);

        } else if (intent.getAction().equals(UPDATE_ACTION)) {

            manager = UserChecker.checkUserLogin(context);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            Log.d("trakt it","appWidgetManager"+appWidgetManager);

            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), TonightEpisodesWidget.class.getName());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

           // new DownloadDayCalendar(this, manager, new Date()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);


            onUpdate(context, appWidgetManager, appWidgetIds);
            Toast.makeText(context,"UPDATE",Toast.LENGTH_SHORT).show();



        }
        super.onReceive(context, intent);
    }



    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        this.appWidgetManager = appWidgetManager;
        this.appWidgetIds = appWidgetIds;
        this.context = context;
        Log.d("trakt it", "onupdate widget");


        for (int i = 0; i < appWidgetIds.length; ++i) {

            Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.example_appwidget);
            rv.setRemoteAdapter(R.id.listViewWidgetTonight, intent);


            Intent mainIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);
            rv.setOnClickPendingIntent(R.id.imageViewWidgetTonightIcon, pendingIntent);

            Intent toastIntent = new Intent(context, TonightEpisodesWidget.class);
            toastIntent.setAction(TonightEpisodesWidget.TOAST_ACTION);
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);

            //EPISODE INTENT
            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.listViewWidgetTonight, toastPendingIntent);

            //UPDATE INTENT
            Intent updateIntent = new Intent(context, TonightEpisodesWidget.class);
            updateIntent.setAction(TonightEpisodesWidget.UPDATE_ACTION);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            PendingIntent updatePendingIntent = PendingIntent.getBroadcast(context, 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setOnClickPendingIntent(R.id.imageViewWidgetUpdate, updatePendingIntent);
//            rv.setEmptyView(R.id.list, R.id.empty_view);
            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);


    }



}


/**
 * If you are familiar with Adapter of ListView,this is the same as adapter
 * with few changes
 */
class ListProvider implements RemoteViewsService.RemoteViewsFactory, DownloadDayCalendar.OnDayCalendarTaskCompleted {

    private final Context mContext;
    private final int mAppWidgetId;
    private ServiceManager manager;
    private List<CalendarDate.CalendarTvShowEpisode> mTonightShows = new LinkedList<CalendarDate.CalendarTvShowEpisode>();

    public ListProvider(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        Log.d("Trakt it", "ListProvider onCreate");

        manager = UserChecker.checkUserLogin(mContext);
        new DownloadDayCalendar(this, manager, new Date()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        Log.d("Trakt it", "Download started");

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {
        mTonightShows.clear();
    }

    @Override
    public int getCount() {
        return mTonightShows.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_tonight_item);
        rv.setTextViewText(R.id.textViewWidgetShowTitle, mTonightShows.get(position).show.title);
        rv.setTextViewText(R.id.textViewWidgetEpisodeInfo, "S" + mTonightShows.get(position).episode.season + "E" + mTonightShows.get(position).episode.number + " " + mTonightShows.get(position).episode.title);


        rv.setTextViewText(R.id.textViewWidgetEpisodeSchedule, mTonightShows.get(position).show.airTimeLocalized);
        rv.setTextViewText(R.id.textViewWidgetEpisodeNetwork, mTonightShows.get(position).show.network);
        AQuery aq = new AQuery(mContext);
        Bitmap screen = aq.getCachedImage(mTonightShows.get(position).episode.images.screen);
        rv.setImageViewBitmap(R.id.imageViewWidgetScreen, screen);


        Bundle extras = new Bundle();
        extras.putString("show_imdb", mTonightShows.get(position).show.imdbId);
        extras.putInt("show_season", mTonightShows.get(position).episode.season);
        extras.putInt("show_episode", mTonightShows.get(position).episode.number);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        // Make it possible to distinguish the individual on-click
        // action of a given item
        rv.setOnClickFillInIntent(R.id.relativeLayoutWidgetTonightEpisode, fillInIntent);

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void OnDayCalendarTaskCompleted(List<CalendarDate> response) {
        mTonightShows = response.get(0).episodes;
        Log.d("trakt it", "download complete");
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        Log.d("trakt it", "tonigth shows: " + response.get(0).episodes.size());
        appWidgetManager.notifyAppWidgetViewDataChanged(mAppWidgetId, R.id.listViewWidgetTonight);
    }
}



