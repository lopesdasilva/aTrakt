package com.lopesdasilva.trakt;



import java.util.List;

import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.TvShow;

import android.app.Application;

public class TVtraktApp extends Application {


    private boolean firstRun=true;
	private ServiceManager manager;
	private String APIKEY="633c56872d241ed0c097829678ea9fbcd15df3c4";
	List<TvShow> trending;

    public boolean isFirstRun() {
        return firstRun;
    }

    public void setFirstRun(boolean firstRun) {
        this.firstRun = firstRun;
    }

    public ServiceManager getManager() {
		return manager;
	}
	public void setManager(ServiceManager manager) {
		this.manager = manager;
	}
	public String getAPIKEY() {
		return APIKEY;
	}
	public List<TvShow> getTrending() {
		return trending;
	}
	public void setTrending(List<TvShow> trending) {
		this.trending = trending;
	}
	
}
