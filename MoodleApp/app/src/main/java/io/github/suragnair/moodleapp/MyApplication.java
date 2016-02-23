package io.github.suragnair.moodleapp;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.List;
import io.github.suragnair.moodleapp.CourseListFragment.Course;

public class MyApplication extends Application
{
    // Volley Queue Global throughout the app
    public static RequestQueue mRequestQueue;

    // Info of the user to check login
    private User MyUser = null;

    // List of courses of the user
    public List<Course> MyCourses = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        // Instantiating Global Volley Queue
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        // Instantiating Cookie Handler
        CookieManager manager = new CookieManager();
        CookieHandler.setDefault(manager);
    }

    public boolean isUserLoggedIn(){
        return MyUser!=null;
    }
    public void setMyUser (User user){
        MyUser = user;
    }
}
