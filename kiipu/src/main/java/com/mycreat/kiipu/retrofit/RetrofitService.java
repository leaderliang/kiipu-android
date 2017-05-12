package com.mycreat.kiipu.retrofit;

import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.model.LoginInfo;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;


public interface RetrofitService {

    @GET(RetrofitApi.BOOK_MARKS_LIST)
    Call<List<Bookmark>> getBookmarkList(@Header("Authorization") String value,
                                         @Query("count") Integer number,
                                         @Query("since_id") String id);

    @DELETE(RetrofitApi.DELETE_BOOK_MARKS)
    Call<Bookmark> deleteBookmark(@Query(":id") String id);

    // 当POST请求时，@FormUrlEncoded和@Field简单的表单键值对。两个需要结合使用，否则会报错
    @FormUrlEncoded
    @POST(RetrofitApi.BOOK_MARKS_LOGIN)
    Call<LoginInfo> loginBookmark(@Field("token") String accessToken,
                                  @Field("uid") String userId);

    @FormUrlEncoded
    @POST(RetrofitApi.BOOK_MARKS_LOGIN)
    Call<LoginInfo> loginBookmarks(@FieldMap Map<String, String> params);
}
