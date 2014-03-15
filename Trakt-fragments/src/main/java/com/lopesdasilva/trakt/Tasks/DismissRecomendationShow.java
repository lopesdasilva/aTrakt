package com.lopesdasilva.trakt.Tasks;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.DismissResponse;
import com.jakewharton.trakt.entities.TvShow;

public class DismissRecomendationShow extends AsyncTask<Void, Void, DismissResponse> {

    private final FragmentActivity activity;
    private final ServiceManager manager;
    private final TvShow dismiss_show;
    private final onDismissedShowTaskComplete listener;
    private final int position;
    private Exception e;

    public DismissRecomendationShow(onDismissedShowTaskComplete listener, FragmentActivity activity, ServiceManager manager, TvShow dismiss_show, int position) {
        this.listener=listener;
        this.activity=activity;
        this.manager=manager;
        this.dismiss_show=dismiss_show;
        this.position=position;
    }

    @Override
    protected DismissResponse doInBackground(Void... voids) {
        try{
        return manager.recommendationsService().dismissShow(dismiss_show.tvdbId).fire();
        }catch (Exception e){
            this.e=e;
            return null;
        }
    }


    @Override
    protected void onPostExecute(DismissResponse response) {
        if(e==null){
            listener.onDismissedShowTaskComplete(response,position);
        }else{
            Log.d("Trakt","Error Dismissing: "+dismiss_show.title);
        }

    }

    public interface onDismissedShowTaskComplete {
        void onDismissedShowTaskComplete(DismissResponse response,int position);
    }

}