package com.lopesdasilva.trakt.widgets;

import android.app.LauncherActivity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.CalendarDate;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.Tasks.DownloadDayCalendar;
import com.lopesdasilva.trakt.extras.UserChecker;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by NB20308 on 03-04-2014.
 */
public class MyFirstWidget extends AppWidgetProvider {

    private ServiceManager manager;
    public AppWidgetManager appWidgetManager;
    private int[] appWidgetIds;
    private Context context;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        this.appWidgetManager = appWidgetManager;
        this.appWidgetIds = appWidgetIds;
        this.context = context;
        Toast.makeText(context, "updating widget trakt", Toast.LENGTH_SHORT).show();
        Log.d("trakt it", "onupdate widget");



        for (int i = 0; i < appWidgetIds.length; ++i) {

            Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.example_appwidget);
            Log.d("trakt it", "setting remote adapter "+appWidgetIds[i]);
            rv.setRemoteAdapter(R.id.listViewWidgetTonight, intent);

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
    private List<CalendarDate> mTonightShows = new LinkedList<CalendarDate>();

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
rv.setTextViewText(R.id.textViewWidgetShowTitle,mTonightShows.get(position).episodes.get(0).show.title);
        rv.setTextViewText(R.id.textViewWidgetEpisodeInfo,"S"+mTonightShows.get(position).episodes.get(0).episode.season+"E"+mTonightShows.get(position).episodes.get(0).episode.number+" "+mTonightShows.get(position).episodes.get(0).episode.title);
        AQuery aq= new AQuery(mContext);
        Bitmap screen = aq.getCachedImage(mTonightShows.get(position).episodes.get(0).episode.images.screen);
        rv.setImageViewBitmap(R.id.imageViewWidgetScreen,screen);

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
        mTonightShows = response;
        Log.d("trakt it", "download complete");
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);

        appWidgetManager.notifyAppWidgetViewDataChanged(mAppWidgetId,R.id.listViewWidgetTonight);
    }
}



