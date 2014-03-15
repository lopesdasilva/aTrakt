package com.lopesdasilva.trakt.Tasks;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.CalendarDate;

import java.util.Date;
import java.util.List;

/**
 * Created by lopesdasilva on 27/05/13.
 */
public class DownloadWeekCalendar extends AsyncTask<Void, Void, List<CalendarDate>> {

    private final OnWeekTaskCompleted listener;
    private final FragmentActivity activity;
    private final Date date;
    public ServiceManager manager;
    private Exception e;

    public DownloadWeekCalendar(OnWeekTaskCompleted listener, FragmentActivity activity, ServiceManager manager, Date date) {
        this.activity = activity;
        this.listener = listener;
        this.manager = manager;
        this.date = date;
    }


    @Override
    protected List<CalendarDate> doInBackground(Void... voids) {
        try {
            return manager.userService().calendarShows(manager.username).date(date).fire();
        } catch (Exception e) {
            this.e = e;
            return null;
        }

    }


    @Override
    protected void onPostExecute(final List<CalendarDate> response) {
        if (e == null) {
            listener.onWeekInfoComplete(response);
        } else {
            Toast.makeText(activity, "Error downloading e: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public interface OnWeekTaskCompleted {
        void onWeekInfoComplete(List<CalendarDate> response);
    }
}
