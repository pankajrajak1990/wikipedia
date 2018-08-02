package com.example.techglobal.wikisearch;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Common_data {

    Activity activity;
    Context cc;
    Intent intent;
    public Retrofit retrofit;
    public APIService service;
    Gson gson = new GsonBuilder()
            .setLenient()
            .create();


    String URL = "https://en.wikipedia.org/w/";

    public Common_data(Activity ac) {
        activity = ac;
       // sharedCommonPref = new Shared_Common_Pref(activity);

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(600, TimeUnit.SECONDS)
                .connectTimeout(600, TimeUnit.SECONDS).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(URL).client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        service = retrofit.create(APIService.class);
    }

    public boolean isOnline(View v) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {


        }
        return false;
    }



}
