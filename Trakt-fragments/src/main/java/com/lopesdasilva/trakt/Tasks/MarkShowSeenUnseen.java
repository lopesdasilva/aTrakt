package com.lopesdasilva.trakt.Tasks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowEpisode;

/**
 * Created by lopesdasilva on 30/05/13.
 */
public class MarkShowSeenUnseen extends AsyncTask<Void,Void,Void>{

    private final ServiceManager manager;
    private final OnMarkShowSeenUnseenCompleted listener;
    private final TvShow show_info;
    private final int position;
    private final FragmentActivity activity;
    private Exception e = null;

    public MarkShowSeenUnseen(FragmentActivity activity, OnMarkShowSeenUnseenCompleted listener, ServiceManager manager, TvShow show, int position){
        this.activity=activity;
        this.manager=manager;
        this.show_info=show;
        this.listener=listener;
        this.position=position;
    }




    @Override
    protected Void doInBackground(Void... voids) {
         try {
            if (episode_info.watched) {
                Log.d("Trakt Fragments", "Changing to unseen");
                return manager.showService().episodeUnseen(show_info.imdbId).episode(episode_info.season, episode_info.number).fire();
            } else {
                Log.d("Trakt Fragments", "Changing to seen");
                return manager.showService().episodeSeen(show_info.imdbId).episode(episode_info.season, episode_info.number).fire();
            }
        } catch (Exception e) {
            this.e = e;
            return null;
        }
    }
    @Override
    protected void onPostExecute(Void response) {
        if(e==null){
            listener.OnMarkShowSeenUnseenCompleted(position);
        }else{
            Log.d("Trakt","Erro marking Seen/Unseen");


            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("Error marking episode")
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                                new MarkShowSeenUnseen(activity,listener,manager,show_info,position).execute();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(activity,"Cancel",Toast.LENGTH_SHORT).show();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();


        }

    }

    public interface OnMarkShowSeenUnseenCompleted {
        void OnMarkShowSeenUnseenCompleted(int position);
    }
}
