package com.example.techglobal.wikisearch;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WikipediaPage extends AppCompatActivity {


    Toolbar toolbar;
    WebView webView;
    TextView name;
    String page_id;
    String title;
    ProgressDialog pd;
    APIService service;
    Gson gson = new GsonBuilder()
            .setLenient()
            .create();
    Common_data common_data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wikipedia_page);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        common_data = new Common_data(WikipediaPage.this);
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setHorizontalScrollBarEnabled(false);
        pd = new ProgressDialog(WikipediaPage.this);
        pd.setMessage("loading...");
        pd.setCancelable(true);
        pd.show();
        name = (TextView) findViewById(R.id.activityName);
        page_id = getIntent().getExtras().getString("page_id");
        title = getIntent().getExtras().getString("title");
        if (title.length() > 0)
            name.setText(title);
        apicall(page_id);
    }

    private void apicall(final String page_id) {
        Call call = common_data.service.wikipage(page_id, "query", "info", "url", "json");
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                String response_date = new Gson().toJson(response.body());
                System.out.println("system" + response_date);
                if (response_date != null) {
                    JSONObject jsonObj = null;
                    try {
                        jsonObj = new JSONObject(response_date);
                        JSONObject queryobject = (JSONObject) jsonObj.get("query");
                        JSONObject pagesobject = (JSONObject) queryobject.get("pages");
                        JSONObject pageidobj = (JSONObject) pagesobject.get(page_id);
                        String title = pageidobj.getString("title");
                        System.out.println("title .- " + title);
                        String fullurl = pageidobj.getString("fullurl");
                        System.out.println("fullurl .- " + fullurl);
                        webView.loadUrl(fullurl);
                        pd.dismiss();
                        } catch (JSONException e) {
                        e.printStackTrace();
                        }
                } else {
                    pd.dismiss();
                    Toast.makeText(WikipediaPage.this, "" + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                pd.dismiss();
                Log.e("TAG", "onFailure: " + t.toString());
                // Log error here since request failed
            }
        });


    }


}
