package com.lopesdasilva.trakt.Tasks;

import android.os.AsyncTask;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.CalendarDate;

import java.util.Date;
import java.util.List;

/**
 * Created by lopesdasilva on 31/05/13.
 */
public class DownloadDayCalendar extends AsyncTask<Void, Void, List<CalendarDate>> {



    private final OnDayCalendarTaskCompleted listener;
    private final ServiceManager manager;
    private final Date date;
    private Exception e;

    public DownloadDayCalendar(OnDayCalendarTaskCompleted listener, ServiceManager manager, Date date) {

        this.listener = listener;
        this.manager = manager;
        this.date = date;
    }

    @Override
    protected List<CalendarDate> doInBackground(Void... voids) {
        try {
            return manager.userService().calendarShows(manager.username).date(date).days(1).fire();
        } catch (Exception e) {
            this.e = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(final List<CalendarDate> response) {
        if (e == null) {
            listener.OnDayCalendarTaskCompleted(response);
        } else {

        }
    }


    public interface OnDayCalendarTaskCompleted {
        void OnDayCalendarTaskCompleted(List<CalendarDate> response);
    }
}
