package com.example.techglobal.wikisearch;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class MainActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    public LinearLayout nodata;
    View Snackbarview;
    Toolbar toolbar;
    ImageView search, rightimage;
    ArrayList<HashMap<String, String>> dete = new ArrayList<>();
    String backbutton = "open";
    EditText searchedit;
    SwipeRefreshLayout mSwipeRefreshLayout;
    int positionlayout;
    wiki_Adapter adapter;
    TextView activityName;
    TextView searchItem;

    public APIService service;
    Gson gson = new GsonBuilder()
            .setLenient()
            .create();
    private static final String SHOWCASE_ID = "1";
    Common_data common_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        common_data = new Common_data(MainActivity.this);
        search = findViewById(R.id.leftimage);
        mSwipeRefreshLayout = findViewById(R.id.swipe_container);
        searchedit = findViewById(R.id.edt_tool);
        nodata = findViewById(R.id.nodata);
        Snackbarview = findViewById(android.R.id.content);
        searchItem = findViewById(R.id.searchItem);
        activityName = findViewById(R.id.activityName);
        rightimage = findViewById(R.id.rightimage);
        searchItem.setVisibility(View.VISIBLE);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_cancel_white_24dp);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                searchedit.setVisibility(View.VISIBLE);
                searchedit.getBackground().mutate().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
                searchedit.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(android.app.Service.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.showSoftInput(searchedit, 0);
                search.setVisibility(View.GONE);
                activityName.setVisibility(View.GONE);
                rightimage.setVisibility(View.GONE);
                backbutton = "hide";
            }
        });
        recyclerView = findViewById(R.id.accounts_recyler);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    System.out.println("tesnline");
                }
            }
        });
      //  MaterialShowcaseView.resetAll(this);
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500);
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);
        sequence.setConfig(config);
        sequence.addSequenceItem(search, "You can search here !", "GOT IT");
        sequence.start();
    }

    public void search() {
        searchedit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String query = s.toString().toLowerCase();
                System.out.println("counstasdas" + query.length());
                if (query.length() > 0) {
                    if (common_data.isOnline(Snackbarview)) {
                        apicall(searchedit.getText().toString());
                    } else {
                        Toast.makeText(MainActivity.this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                    mSwipeRefreshLayout.setRefreshing(true);
                } else {
                    nodata.setVisibility(View.VISIBLE);
                    dete.clear();
                }
            }

            public void beforeTextChanged(CharSequence query, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence query, int start, int before, int count) {
                System.out.println("counstasdas" + query.length());
                if (query.length() > 0) {
                    apicall(searchedit.getText().toString());
                    mSwipeRefreshLayout.setRefreshing(true);
                } else {
                    nodata.setVisibility(View.VISIBLE);
                    dete.clear();
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case android.R.id.home:
                if (backbutton.equals("hide")) {
                    getSupportActionBar().setHomeAsUpIndicator(null);
                    searchedit.setText("");
                    searchedit.setVisibility(View.GONE);
                    search.setVisibility(View.VISIBLE);
                    activityName.setVisibility(View.VISIBLE);
                    rightimage.setVisibility(View.VISIBLE);
                    Intent intent = MainActivity.this.getIntent();
                    finish();
                    startActivity(intent);
                    backbutton = "open";
                    try {
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
       // MaterialShowcaseView.resetAll(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
       // MaterialShowcaseView.resetAll(this);
    }


    @Override
    public void onBackPressed() {
        finish();
    }


    private synchronized void apicall(String search) {
        nodata.setVisibility(View.GONE);
        Call call = common_data.service.Searchtext(search, "query", "2", "prefixsearch", "7", "pageimages|pageterms", "thumbnail", "50", "10", "description", "json");
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
               // Log.e("TAG", "response 33: " + new Gson().toJson(response.body()));
                dete.clear();
                String response_date = new Gson().toJson(response.body());
                System.out.println("system" + response_date);
                if (response_date != null) {
                    JSONObject jsonObj = null;
                    try {
                        jsonObj = new JSONObject(response_date);
                        JSONObject object = (JSONObject) jsonObj.get("query");
                        JSONArray pages = object.getJSONArray("pages");
                        for (int i = 0; i < pages.length(); i++) {
                            HashMap<String, String> hsmp = new HashMap<>();
                            JSONObject pagesObject = pages.getJSONObject(i);
                            String title = pagesObject.getString("title");
                            String page_id = String.valueOf(pagesObject.getInt("pageid"));
                            hsmp.put("title", title);
                            hsmp.put("page_id", page_id);
                            if (pagesObject.has("thumbnail")) {
                                JSONObject thumbnailobject = (JSONObject) pagesObject.get("thumbnail");
                                String source = thumbnailobject.getString("source");
                                System.out.println("source .- " + source);
                                hsmp.put("url", source);
                            } else {
                                hsmp.put("url", "");
                            }
                            if (pagesObject.has("terms")) {
                                JSONObject description = (JSONObject) pagesObject.get("terms");
                                JSONArray description_array = description.getJSONArray("description");
                                String des = description_array.getString(0);
                                System.out.println("des - " + des);
                                hsmp.put("desc", des);
                            } else {
                                hsmp.put("desc", "");
                            }
                            dete.add(hsmp);
                        }// end of for loop

                        if (dete.size() > 0) {
                            recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            nodata.setVisibility(View.VISIBLE);
                        }
                        if (mSwipeRefreshLayout != null) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                        recyclerView.setHasFixedSize(true);
                        System.out.println("lentgdhquery" + dete.size());
                        adapter = new wiki_Adapter(MainActivity.this, dete, positionlayout);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(MainActivity.this, "" + response.message(), Toast.LENGTH_SHORT).show();
                }

                // Log.e("TAG", "response 33: "+new Gson().toJson(response.body()) );
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e("TAG", "onFailure: " + t.toString());
                // Log error here since request failed
            }
        });


    }


    public class wiki_Adapter extends RecyclerView.Adapter<wiki_Adapter.MyAcViewholder> implements View.OnClickListener {
        private Context context;
        private ArrayList<HashMap<String, String>> acntdta;
        protected int layout;
        ArrayList<HashMap<String, String>> acntdtaList;

        public wiki_Adapter(Context context, ArrayList<HashMap<String, String>> acountlist, int layout) {
            this.acntdta = acountlist;
            this.context = context;
            this.layout = layout;
        }

        @Override
        public MyAcViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.wiki, parent, false);
            return new wiki_Adapter.MyAcViewholder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyAcViewholder holder, final int position) {
            holder.newstitle.setText(acntdta.get(holder.getAdapterPosition()).get("title"));
            holder.newsdesc.setText(acntdta.get(holder.getAdapterPosition()).get("desc"));
            if (acntdta.get(holder.getAdapterPosition()).get("url").length() > 0) {
                Glide.with(context).load(acntdta.get(holder.getAdapterPosition()).get("url"))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.image);
            } else {
                holder.image.setImageAlpha(R.drawable.placeholder);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, WikipediaPage.class);
                    i.putExtra("page_id", acntdta.get(holder.getAdapterPosition()).get("page_id"));
                    i.putExtra("title", acntdta.get(holder.getAdapterPosition()).get("title"));
                    context.startActivity(i);
                }
            });


        }

        @Override
        public int getItemCount() {
            int returnsize;
            returnsize = acntdta.size();
            return returnsize;
        }

        @Override
        public void onClick(View v) {
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        class MyAcViewholder extends RecyclerView.ViewHolder {
            TextView newstitle, newsdesc;
            ImageView image;

            MyAcViewholder(View itemView) {
                super(itemView);
                newstitle = itemView.findViewById(R.id.newstitle);
                newsdesc = itemView.findViewById(R.id.newsdesc);
                image = itemView.findViewById(R.id.thumbnail_news_top);
            }
        }
    }
}
