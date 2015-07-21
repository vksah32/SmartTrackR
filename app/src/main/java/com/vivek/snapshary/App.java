package com.vivek.snapshary;

import android.app.Application;
import com.parse.Parse;

/**
 * Created by vivek on 7/16/15.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize global stuff for Yourney
        Parse.initialize(this, "4M6qIsmpBhTvtacITOzHcgE1k6uzM757LZa7M82J", "ScrqqlqD7FaK683K30PPHCmDJMfytaaxwOGI9ncc");
    }
}
