package com.lopesdasilva.trakt;


import android.app.Application;

import com.androidquery.callback.BitmapAjaxCallback;
import com.jakewharton.trakt.ServiceManager;

public class TVtraktApp extends Application {


    private boolean firstRun=true;
	private ServiceManager manager;
	private String APIKEY="633c56872d241ed0c097829678ea9fbcd15df3c4";


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

    @Override
    public void onLowMemory(){

        //clear all memory cached images when system is in low memory
        //note that you can configure the max image cache count, see CONFIGURATION
        BitmapAjaxCallback.clearCache();
    }



}
