package com.lopesdasilva.trakt.Tasks;

import android.os.AsyncTask;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.Response;
import com.jakewharton.trakt.enumerations.MediaType;

/**
 * Created by lopesdasilva on 20/05/13.
 */
public class CancelCheckIn extends AsyncTask<Void, Void, Response> {

    private final ServiceManager manager;
    private final MediaType type;
    private OnCheckInCanceled listener;
    private Exception e;

    public CancelCheckIn(OnCheckInCanceled listener, ServiceManager manager, MediaType type){
        this.listener=listener;
        this.manager=manager;
        this.type=type;

    }

    @Override
    protected Response doInBackground(Void... voids) {
        try{
        switch (type) {
            case Movie:
                return manager.movieService().cancelCheckin().fire();
            case TvShow:
                return manager.showService().cancelCheckin().fire();
        }
        }catch(Exception e){
            this.e=e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(final Response response) {
        if(e==null){
            listener.OnCheckInCanceled(response);
        }

    }



    public interface OnCheckInCanceled{
        void OnCheckInCanceled(Response response);
    }
}
