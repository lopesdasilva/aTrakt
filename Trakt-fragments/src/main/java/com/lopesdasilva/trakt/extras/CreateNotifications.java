package com.lopesdasilva.trakt.extras;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.TvEntity;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.activities.EpisodeActivity;
import com.lopesdasilva.trakt.broadcasts.CheckInReceiver;

/**
 * Created by lopesdasilva on 18/05/13.
 */
public class CreateNotifications {

    public static void EpisodeNotification(FragmentActivity activity, TvEntity episode_info, Bitmap episodeScreen) {


        Log.d("Trakt Fragments", episode_info.show.images.poster);
        Bundle arguments = new Bundle();
        arguments.putString("show_imdb", episode_info.show.imdbId);
        arguments.putInt("show_season", episode_info.episode.season);
        arguments.putInt("show_episode", episode_info.episode.number);
        Intent information_episode = new Intent(activity, EpisodeActivity.class);
        information_episode.putExtras(arguments);
        PendingIntent intent = PendingIntent.getActivity(activity, 0, information_episode, PendingIntent.FLAG_ONE_SHOT);

        Notification.Builder build = new Notification.Builder(activity)
                .setContentTitle(episode_info.show.title)
                .setContentText(episode_info.episode.title)
                .setContentInfo("S" + episode_info.episode.season + "E" + episode_info.episode.number)
                .setTicker("You are watching " + episode_info.show.title + " S" + episode_info.episode.season + "E" + episode_info.episode.number + " " + episode_info.episode.title)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(intent);

        NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);


//                //CHECK WHAT IS YOUR ANDROID VERSION TODO: USE ANOTATIONS
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            build.addAction(R.drawable.ic_action_info, "Episode Info", intent);

            Intent cancelIntent_episode = new Intent(activity, CheckInReceiver.class);
            cancelIntent_episode.putExtras(arguments);
            PendingIntent cancelCheckingIntent = PendingIntent.getBroadcast(activity, 0,cancelIntent_episode , PendingIntent.FLAG_UPDATE_CURRENT);
            build.addAction(R.drawable.ic_action_accept, "Cancel CheckIn", cancelCheckingIntent);
            Notification notification = new Notification
                    .BigPictureStyle(build)
                    .bigPicture(episodeScreen)
                    .setBigContentTitle("Your are watching")
                    .setSummaryText(episode_info.show.title)
                    .build();
            mNotificationManager.notify(0, notification);
        } else {
            mNotificationManager.notify(0, build.build());
        }

    }

    public static void MovieNotification(final FragmentActivity activity, final Movie movie, Bitmap movieFanArt) {


//        PendingIntent intent = PendingIntent.getActivity(activity, 0, new Intent(activity, EpisodeActivity.class), 0);
        Notification.Builder build = new Notification.Builder(activity)
                .setContentTitle(movie.title)
                .setContentText(movie.year + "")
                .setContentInfo(movie.runtime + " minutes")
                .setTicker("You are watching " + movie.title + " (" + movie.year + ")")
//                .setLargeIcon(movieFanArt)
//                .setContentIntent(intent)
                .setSmallIcon(R.drawable.ic_launcher);
//

        NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);


//                //CHECK WHAT IS YOUR ANDROID VERSION
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            build.addAction(R.drawable.ic_action_info, "Episode Info", intent);

            Notification notification = new Notification
                    .BigPictureStyle(build)
                    .bigPicture(movieFanArt)
//                    .bigLargeIcon(moviePoster)
                    .setBigContentTitle("Your are watching")
                    .setSummaryText(movie.title)
                    .build();
            mNotificationManager.notify(0, notification);
        } else {
            mNotificationManager.notify(0, build.build());
        }

    }


}
