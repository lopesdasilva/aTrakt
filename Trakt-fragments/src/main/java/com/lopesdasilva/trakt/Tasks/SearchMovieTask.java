package com.lopesdasilva.trakt.Tasks;

import android.os.AsyncTask;
import android.widget.Toast;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.Movie;

import java.util.List;

import android.support.v4.app.FragmentActivity;
/**
 * Created by lopesdasilva on 03/06/13.
 */
public class SearchMovieTask extends AsyncTask<Void, Void, List<Movie>> {

    private final ServiceManager manager;
    private final String searchQuery;
    private final FragmentActivity activity;
    private Exception e= null;
    private onSearchComplete listener;


    public SearchMovieTask(FragmentActivity activity,onSearchComplete listener,ServiceManager manager, String searchQuery){
        this.activity=activity;
        this.listener=listener;
        this.manager=manager;
        this.searchQuery=searchQuery;
    }


    @Override
    protected List<Movie> doInBackground(Void... voids) {
        try{
        return manager.searchService().movies(searchQuery).fire();
        }catch (Exception e){
            this.e=e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Movie> result){
        if(e==null){
            listener.onSearchComplete(result);
        }else{
            Toast.makeText(activity,"Erro downloading movie results",Toast.LENGTH_SHORT).show();
        }

    }
    public interface onSearchComplete{
        void onSearchComplete(List<Movie> result);
    }

}
