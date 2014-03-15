package com.lopesdasilva.trakt.Tasks;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.TvShow;

import java.util.List;

/**
 * Created by lopesdasilva on 03/06/13.
 */
public class SearchShowTask extends AsyncTask<Void, Void, List<TvShow>> {

    private final ServiceManager manager;
    private final String searchQuery;
    private final FragmentActivity activity;
    private Exception e= null;
    private onSearchComplete listener;


    public SearchShowTask(FragmentActivity activity, onSearchComplete listener, ServiceManager manager, String searchQuery){
        this.activity=activity;
        this.listener=listener;
        this.manager=manager;
        this.searchQuery=searchQuery;
    }


    @Override
    protected List<TvShow> doInBackground(Void... voids) {
        try{
        return manager.searchService().shows(searchQuery).fire();
        }catch (Exception e){
            this.e=e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<TvShow> result){
        if(e==null){
            listener.onSearchComplete(result);
        }else{
            Toast.makeText(activity,"Erro downloading movie results",Toast.LENGTH_SHORT).show();
        }

    }
    public interface onSearchComplete{
        void onSearchComplete(List<TvShow> result);
    }

}
