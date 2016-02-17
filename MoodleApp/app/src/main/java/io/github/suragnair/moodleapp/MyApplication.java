package io.github.suragnair.moodleapp;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.net.CookieHandler;
import java.net.CookieManager;

/**
 * Created by snair on 16/02/16.
 * Instantiating Global Volley Queue
 * Instantiating Cookie Handler
 */


public class MyApplication extends Application
{
    public static RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        CookieManager manager = new CookieManager();
        CookieHandler.setDefault(manager);
    }

    // Getter for RequestQueue or just make it public
}
