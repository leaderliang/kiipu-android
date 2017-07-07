package com.mycreat.kiipu.retrofit;

import com.mycreat.kiipu.model.*;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

/**
 * 接口参数封装类
 * <p>
 * 使用 Retrofit 框架进行数据请求时，需注意：
 * 类似在接口中拼接参数（REST API规范），如 /bookmarks/:id?ext=1&tmpl=1 中的 :id ，
 * 可通过在参数中配置 @Path("id") String id 来实现，切记 @Path 配置的参数不可放在 Query 标签后，否则会出异常。
 *
 * @author leaderliang
 */
public interface RetrofitService {

    /**
     * 该接口也适用于主界面菜单点击请求
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

    @DELETE(RetrofitApi.DELETE_BOOK_MARKS + "{id}")
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
    Call<LoginInfo> loginBookmark(@FieldMap Map<String, String> params);

    @GET(RetrofitApi.COLLECTION_LIST)
    Call<List<Collections>> getCollectionList(@Header("Authorization") String value);

    @FormUrlEncoded
    @POST(RetrofitApi.CREATE_COLLECTION)
    Call<Collections> createCollection(@Header("Authorization") String value,
                                       @Field("name") String collectionName);

    @GET(RetrofitApi.USER_INFO)
    Call<UserInfo> getUserInfo(@Header("Authorization") String value);

    @FormUrlEncoded
    @PATCH(RetrofitApi.MOVE_BOOKMARK + "{bookmark_id}")
    Call<Bookmark> moveBookmark(@Header("Authorization") String value,
                                @Path("bookmark_id") String id,
                                @Field("collection_id") String collectionId);

    /**
     * @param value
     * @param defaultExt
     * @param defaultTmpl
     * @param id
     * @return
     */
    @GET(RetrofitApi.CARD_TEMPLATE + "{id}")
    Call<CardTemplate> getCardTemplateInfo(@Header("Authorization") String value,
                                           @Query("ext") String defaultExt,
                                           @Query("tmpl_ver") String defaultTmpl,
                                           @Query("id") String id);

    /**
     * 第一次时可不传 tmp_ver
     *
     * @param value
     * @param defaultExt
     * @param id
     * @return
     */
    @GET(RetrofitApi.CARD_TEMPLATE + "{id}")
    Call<CardTemplate> getCardTemplateInfo(@Header("Authorization") String value,
                                           @Path("id") String id,
                                           @Query("ext") String defaultExt
    );
    /**
     * 请求模板 html 中的样式数据,在书签列表 bean 中拼凑链接
     * http://static.kiipu.com/tmpl/{tmpl}/{tmpl_ver}.html
     */
    @GET(RetrofitApi.BASE_TEMPLATE_URL + "{htmlPath}")
    Call<String> requestHtml(@Path("htmlPath") String htmlPath);
}
