package com.meow.dump;

import android.app.Application;

public class MeowApp extends Application {
    private static MeowApp instance;
    
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
    
    public static MeowApp getInstance() {
        return instance;
    }
}
