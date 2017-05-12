package com.mycreat.kiipu.retrofit;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitClient {

    private RetrofitClient() {}

    private static class ClientHolder {
        private static Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitApi.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //增加返回值为Gson的支持(以实体类返回)
                .addConverterFactory(GsonConverterFactory.create())
                //直接返回String类型需引入
//                .addConverterFactory(ScalarsConverterFactory.create)
                .build();
    }

    public static Retrofit getInstance() {
        return ClientHolder.retrofit;
    }



}
