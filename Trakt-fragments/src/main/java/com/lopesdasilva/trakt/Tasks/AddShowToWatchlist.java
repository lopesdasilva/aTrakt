package com.lopesdasilva.trakt.Tasks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.TvShow;

/**
 * Created by lopesdasilva on 30/05/13.
 */
public class AddShowToWatchlist extends AsyncTask<Void, Void, Void> {


    private final ServiceManager manager;
    private final OnAddShowToWatchlistCompleted listener;
    private final TvShow show_info;
    private final int position;
    private final FragmentActivity activity;
    private Exception e = null;

    public AddShowToWatchlist(FragmentActivity activity, OnAddShowToWatchlistCompleted listener, ServiceManager manager, TvShow show, int position){
        this.activity=activity;
        this.manager=manager;
        this.show_info=show;
        this.listener=listener;
        this.position=position;
    }




    @Override
    protected Void doInBackground(Void... voids) {
         try {

                Log.d("Trakt", "Marking "+show_info.tvdbId+" as hated");
             return manager.showService().watchlist().title(show_info.title,show_info.year).fire();


        } catch (Exception e) {
            this.e = e;
            return null;
        }
    }
    @Override
    protected void onPostExecute(Void response) {
        if(e==null){

            listener.OnAddShowToWatchlistCompleted(position);

        }else{
            Log.d("Trakt","Error adding the movie to watchlist");


            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("Error adding the movie to watchlist")
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                                new AddShowToWatchlist(activity,listener,manager,show_info,position).execute();
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

    public interface OnAddShowToWatchlistCompleted {
        void OnAddShowToWatchlistCompleted(int position);
    }
}
