package com.lopesdasilva.trakt.alarms;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.CalendarDate;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.Tasks.DownloadDayCalendar;
import com.lopesdasilva.trakt.activities.EpisodesTonightActivity;
import com.lopesdasilva.trakt.extras.UserChecker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by lopesdasilva on 18/05/13.
 */
public class MyAlarmReceiver extends BroadcastReceiver implements DownloadDayCalendar.OnDayCalendarTaskCompleted {
    private final String REMINDER_BUNDLE = "MyReminderBundle";
    private ServiceManager manager;
    private Context context;

    // this constructor is called by the alarm manager.
    public MyAlarmReceiver() {
    }

    // you can use this constructor to create the alarm.
    //  Just pass in the main activity as the context,
    //  any extras you'd like to get later when triggered
    //  and the timeout
    public MyAlarmReceiver(Context context, Bundle extras, int timeoutInHours) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MyAlarmReceiver.class);
        intent.putExtra(REMINDER_BUNDLE, extras);
        PendingIntent pendingIntent =  PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        Calendar time = Calendar.getInstance();
//        time.setTimeInMillis(System.currentTimeMillis());
//        time.add(Calendar.SECOND, 60);
//        alarmMgr.set(AlarmManager.ELAPSED_REALTIME, time.getTimeInMillis(),
//                pendingIntent);


//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, 19);
//        calendar.set(Calendar.MINUTE, 30);
//        calendar.set(Calendar.SECOND, 0);
//
//
//        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);


        // Set the alarm to start at approximately 2:00 p.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 19);

// With setInexactRepeating(), you have to use one of the AlarmManager interval
// constants--in this case, AlarmManager.INTERVAL_DAY.
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),  AlarmManager.INTERVAL_DAY, pendingIntent);

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent!=null && intent.getAction()!=null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.d("Trakt it", "Android reboot rescheduling alarm to get shows tonight");
            Bundle bundle = new Bundle();
            // add extras here..
            MyAlarmReceiver alarm = new MyAlarmReceiver(context, bundle, 24);
        }else {
            this.context = context;
            // here you can get the extras you passed in when creating the alarm
            //intent.getBundleExtra(REMINDER_BUNDLE));
            Log.d("Trakt Fragments", "The alarm has ended");
            Toast.makeText(context, "Alarm went off", Toast.LENGTH_SHORT).show();


            manager = UserChecker.checkUserLogin(context);


            new DownloadDayCalendar(this, manager, new Date()).execute();
        }

    }

    @Override
    public void OnDayCalendarTaskCompleted(List<CalendarDate> response) {
        Bundle arguments = new Bundle();
        if (response.size() != 0) {
            arguments.putSerializable("CalendarTonight", response.get(0));

            Log.d("Trakt", "Alarm a arguments send:" + response.get(0).episodes.size());
            Intent episodes_tonight = new Intent(context, EpisodesTonightActivity.class);
            episodes_tonight.putExtras(arguments);
            PendingIntent intent = PendingIntent.getActivity(context, 0, episodes_tonight, PendingIntent.FLAG_ONE_SHOT);


            if (response.size() != 0)
                if (response.get(0).episodes.size() != 0) {
                    Notification.Builder build = new Notification.Builder(context);
                    if (response.get(0).episodes.size() == 1) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
                        build.setContentTitle(response.get(0).episodes.get(0).show.title + " on tonight")
                                .setContentInfo(dateFormat.format(response.get(0).episodes.get(0).episode.firstAired))
                                .setContentText("S" + response.get(0).episodes.get(0).episode.season + "E" + response.get(0).episodes.get(0).episode.number + " " + response.get(0).episodes.get(0).episode.title)
                                .setTicker(response.get(0).episodes.get(0).show.title + " on tonight " + "S" + response.get(0).episodes.get(0).episode.season + "E" + response.get(0).episodes.get(0).episode.number + " " + response.get(0).episodes.get(0).episode.title);
                    } else {
                        build.setContentTitle("You have " + response.get(0).episodes.size() + " TV shows on tonight")
                                .setContentText("Click to see more")
                                .setTicker("You have " + response.get(0).episodes.size() + " TV shows on tonight");
                    }
                    build
//                .setContentInfo("S" + episode_info.episode.season + "E" + episode_info.episode.number)
//
                            .setContentIntent(intent)
                            .setSmallIcon(R.drawable.ic_launcher);
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//
//                    AQuery aq=
//                    Notification notification = new Notification
//                            .BigPictureStyle(build)
//                            .bigPicture(episodeScreen)
//                            .setBigContentTitle("Your are watching")
//                            .setSummaryText(episode_info.movie.title)
//                            .build();
//                    mNotificationManager.notify(0, notification);
//                } else {
                    mNotificationManager.notify(0, build.build());
//                }
                }
        }
    }
}
