package com.example.techglobal.wikisearch;


import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIService {

    @POST("api.php")
    @FormUrlEncoded
    Call<Object> Searchtext(@Field("gpssearch") String gpssearch,
                            @Field("action") String action,
                            @Field("formatversion") String formatversion,
                            @Field("generator") String generator,
                            @Field("gpslimit") String gpslimit,
                            @Field("prop") String prop,
                            @Field("piprop") String piprop,
                            @Field("pithumbsize") String pithumbsize,
                            @Field("pilimit") String pilimit,
                            @Field("wbptterms") String wbptterms,
                            @Field("format") String format
    );




    @POST("api.php")
    @FormUrlEncoded
    Call<Object> wikipage(@Field("pageids") String pageids,
                          @Field("action") String action,
                          @Field("prop") String prop,
                          @Field("inprop") String inprop,
                          @Field("format") String format
    );


}
