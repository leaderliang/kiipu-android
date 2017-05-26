package com.mycreat.kiipu.retrofit;

import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.model.Collections;
import com.mycreat.kiipu.model.LoginInfo;
import com.mycreat.kiipu.model.UserInfo;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

/**
 * 接口参数封装
 * @author leaderliang
 */
public interface RetrofitService {

    /**
     * 该接口适用于主界面菜单点击请求
     * collection_id
     * 全部      ：collection_id = 0 或不传 collection_id
     * 收件箱    ：collection_id = -1
     * 其他item  ：直接传 collection_id
     */
    @GET(RetrofitApi.BOOK_MARKS_LIST)
    Call<List<Bookmark>> getBookmarkList(@Header("Authorization") String value,
                                         @Query("count") Integer number,
                                         @Query("since_id") String id,
                                         @Query("collection_id") String collectionId);

    @DELETE(RetrofitApi.DELETE_BOOK_MARKS+"{id}")
    Call<Bookmark> deleteBookmark(@Header("Authorization") String value,
                                  @Path("id") String id);

    /**
     * 当POST请求时，@FormUrlEncoded和@Field简单的表单键值对。两个需要结合使用，否则会报错
     */
    @FormUrlEncoded
    @POST(RetrofitApi.BOOK_MARKS_LOGIN)
    Call<LoginInfo> loginBookmark(@Field("token") String accessToken,
                                  @Field("uid") String userId);

    @FormUrlEncoded
    @POST(RetrofitApi.BOOK_MARKS_LOGIN)
    Call<LoginInfo> loginBookmarks(@FieldMap Map<String, String> params);

    @GET(RetrofitApi.COLLECTION_LIST)
    Call<List<Collections>> getCollectionList(@Header("Authorization") String value);

    @FormUrlEncoded
    @POST(RetrofitApi.CREATE_COLLECTION)
    Call<Collections> creatCollection(@Header("Authorization") String value,
                                      @Field("name") String collectionName);

    @GET(RetrofitApi.USER_INFO)
    Call<UserInfo> getUserInfo(@Header("Authorization") String value);

    @FormUrlEncoded
    @PATCH(RetrofitApi.MOVE_BOOKMARK+"{bookmark_id}")
    Call<Bookmark> moveBookmark(@Header("Authorization") String value,
                                @Path("bookmark_id") String id,
                                @Field("collection_id") String collectionId);

}
