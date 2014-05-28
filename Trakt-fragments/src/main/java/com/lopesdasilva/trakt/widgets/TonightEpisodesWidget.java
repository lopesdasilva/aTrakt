package com.lopesdasilva.trakt.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.CalendarDate;
import com.lopesdasilva.trakt.MainActivity;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.activities.CalendarActivity;
import com.lopesdasilva.trakt.activities.EpisodeActivity;
import com.lopesdasilva.trakt.activities.EpisodesTonightActivity;
import com.lopesdasilva.trakt.extras.UserChecker;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by NB20308 on 03-04-2014.
 */
public class TonightEpisodesWidget extends AppWidgetProvider {

    public static final String TOAST_ACTION = "com.example.android.stackwidget.TOAST_ACTION";
    public static final String PREFS_NAME = "TraktPrefsFile";


    public AppWidgetManager appWidgetManager;
    private ServiceManager manager;
    private int[] appWidgetIds;
    private Context context;
    private int appWidgetId;


    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        String mUsername = settings.getString("username", null);
        if (mUsername != null) {
            if (intent.getAction().equals(TOAST_ACTION)) {
                appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID);

                Intent episodeIntent = new Intent(context, EpisodeActivity.class);
                episodeIntent.putExtras(intent.getExtras());
                episodeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(episodeIntent);

            } else if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
                appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.listViewWidgetTonight);
            }
        }
        super.onReceive(context, intent);
    }


    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        this.appWidgetManager = appWidgetManager;
        this.appWidgetIds = appWidgetIds;
        this.context = context;
        Log.d("trakt it", "onupdate widget");

//        Toast.makeText(context, "UPDATING TRAKT WIDGET", Toast.LENGTH_SHORT).show();

        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        String mUsername = settings.getString("username", null);
        if (mUsername == null) {
            for (int i = 0; i < appWidgetIds.length; ++i) {
                Intent mainIntent = new Intent(context, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);


                RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_not_logged);
                rv.setOnClickPendingIntent(R.id.relativeLayoutWidgetNotLogged, pendingIntent);
                appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
            }
        } else {


            for (int i = 0; i < appWidgetIds.length; ++i) {

                Intent intent = new Intent(context, WidgetService.class);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
                intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

                RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_episodes_tonight_layout);
                rv.setRemoteAdapter(R.id.listViewWidgetTonight, intent);
                rv.setEmptyView(R.id.listViewWidgetTonight,R.id.relativeLayoutWidgetEmpty);


                Intent mainIntent = new Intent(context, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);
                rv.setOnClickPendingIntent(R.id.imageViewWidgetTonightIcon, pendingIntent);

                Intent calendarIntent = new Intent(context, CalendarActivity.class);
                PendingIntent calendarPendingIntent = PendingIntent.getActivity(context, 0, calendarIntent, 0);
                rv.setOnClickPendingIntent(R.id.imageViewWidgetCalendar, calendarPendingIntent);


                Intent tonightIntent = new Intent(context, EpisodesTonightActivity.class);
                PendingIntent tonightPendingIntent = PendingIntent.getActivity(context, 0, tonightIntent, 0);
                rv.setOnClickPendingIntent(R.id.textViewWidgetTonightEpisodes, tonightPendingIntent);

                Intent toastIntent = new Intent(context, TonightEpisodesWidget.class);
                toastIntent.setAction(TonightEpisodesWidget.TOAST_ACTION);
                toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);

                //EPISODE INTENT
                PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                rv.setPendingIntentTemplate(R.id.listViewWidgetTonight, toastPendingIntent);

                //UPDATE INTENT
                Intent updateIntent = new Intent(context, TonightEpisodesWidget.class);
                updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
                PendingIntent updatePendingIntent = PendingIntent.getBroadcast(context, 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                rv.setOnClickPendingIntent(R.id.imageViewWidgetUpdate, updatePendingIntent);
//            rv.setEmptyView(R.id.list, R.id.empty_view);
                appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
            }
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);


    }


}

/**
 * If you are familiar with Adapter of ListView,this is the same as adapter
 * with few changes
 */
class ListProvider implements RemoteViewsService.RemoteViewsFactory {

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


    }

    @Override
    public void onDataSetChanged() {


        try {
            manager = UserChecker.checkUserLogin(mContext);
            Log.d("Trakt it", "Download started");
            List<CalendarDate> response = manager.userService().calendarShows(manager.username).date(new Date()).days(1).fire();
            mTonightShows = response.get(0).episodes;

//        new DownloadDayCalendar(this, manager, new Date()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            Log.d("Trakt it", "Download finished");
        } catch (Exception e) {
            Log.d("trakt it", "error Downloading message:" + e.getMessage());
//            Toast.makeText(mContext, "Error updating the widget, are you connected?",Toast.LENGTH_SHORT).show();
        }


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

        if (mTonightShows.get(position).show.airDayLocalized != null)
            rv.setTextViewText(R.id.textViewWidgetEpisodeSchedule, mTonightShows.get(position).show.airDayLocalized.substring(0, 3) + ", " + mTonightShows.get(position).show.airTimeLocalized);
        else
            rv.setTextViewText(R.id.textViewWidgetEpisodeSchedule, mTonightShows.get(position).show.airTimeLocalized);
        rv.setTextViewText(R.id.textViewWidgetEpisodeNetwork, mTonightShows.get(position).show.network);
//        AQuery aq = new AQuery(mContext);

        try {

            Bitmap screen = BitmapFactory.decodeStream(new URL(mTonightShows.get(position).episode.images.screen).openConnection().getInputStream());

            //  Bitmap screen = aq.getCachedImage(, 300);
            rv.setImageViewBitmap(R.id.imageViewWidgetScreen, screen);
        } catch (IOException e) {
            Log.e("trakt", "error downloading screen for widget, show: " + mTonightShows.get(position).show.title);
        }

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
        return new RemoteViews(mContext.getPackageName(), R.layout.widget_tonight_item_loading);

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


}



