package com.mycreat.kiipu.retrofit;

import com.mycreat.kiipu.BuildConfig;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;


public class RetrofitClient {

    private RetrofitClient() {
    }

    private static class ClientHolder {


        private static OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                 //打印日志拦截器
                .addInterceptor(getHttpLoggingInterceptor())
                .connectTimeout(10000, TimeUnit.MILLISECONDS)//设置超时时间
                .retryOnConnectionFailure(true);


        private static Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitApi.BASE_URL)
                .client(httpClientBuilder.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //增加返回值为Gson的支持(以实体类返回)
                .addConverterFactory(GsonConverterFactory.create())
                //直接返回String类型需引入
//                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(new ToStringConverterFactory())
                .build();


        private static Retrofit retrofitTemplate = new Retrofit.Builder()
                .baseUrl(RetrofitApi.BASE_TEMPLATE_URL)
                .client(httpClientBuilder.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //直接返回String类型需引入
                .addConverterFactory(new ToStringConverterFactory())
                .build();
    }

    public static Retrofit getInstance() {
        return ClientHolder.retrofit;
    }

    public static Retrofit getTemplateInstance() {
        return ClientHolder.retrofitTemplate;
    }

    private static Interceptor getHttpLoggingInterceptor(){
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        if(BuildConfig.DEBUG){
            //显示日志
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        }else {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        return httpLoggingInterceptor;
    }


}
