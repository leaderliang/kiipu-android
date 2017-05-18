package com.mycreat.kiipu.retrofit;

/**
 * Created by leaderliang on 2017/3/31.
 * email: leaderliang.dev@gmail.com
 * TODO
 */
public class RetrofitApi {

    /**
     * Base API
     */
    public static final String BASE_URL = "http://api.kiipu.com";
    /**
     * POST
     * Login
     * token: uid:
     */
    public static final String BOOK_MARKS_LOGIN = BASE_URL + "/auth/weibo";

    /**
     * Get
     * GET BOOK MARKS list
     * /bookmarks?count=10 & since_id=xxx
     */
    public static final String BOOK_MARKS_LIST = BASE_URL + "/bookmarks";
    /**
     * DELETE
     * /bookmarks/:id
     */
    public static final String DELETE_BOOK_MARKS = BASE_URL + "/bookmarks/";
    /**
     * collections
     * 获取用户书签夹
     * 参数：
     * header（access_token）
     */
    public static final String COLLECTION_LIST = BASE_URL + "/collections";
    /**
     * 创建书签
     * 参数：
     * name
     * header（access_token）
     */
    public static final String CREAT_COLLECTION = BASE_URL + "/collections";
    /**
     * user
     * 参数：header : access_token
     */
    public static final String USER_INFO = BASE_URL + "/user";


}
