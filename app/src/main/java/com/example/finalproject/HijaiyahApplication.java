package com.example.finalproject;

import android.app.Application;

import com.google.android.material.color.DynamicColors;

public class HijaiyahApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Menggunakan dynamic colors Android 12+ jika tersedia
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}