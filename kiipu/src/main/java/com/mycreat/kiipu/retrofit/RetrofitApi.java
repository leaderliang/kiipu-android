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
    public static final String BASE_URL = "https://api.kiipu.com/";
    /**
     * Base Template API
     */
    public static final String BASE_TEMPLATE_URL = "http://ornb86j70.bkt.clouddn.com/tmpl/";
    /**
     * POST
     * Login
     * token: uid:
     */
    public static final String BOOK_MARKS_LOGIN = BASE_URL + "auth/weibo";
    /**
     * Get
     * GET BOOK MARKS list
     * /bookmarks?count=10 & since_id=xxx
     */
    public static final String BOOK_MARKS_LIST = BASE_URL + "bookmarks";
    /**
     * DELETE
     * /bookmarks/:id
     */
    public static final String DELETE_BOOK_MARKS = BASE_URL + "bookmarks/";
    /**
     * collections
     * 获取用户书签夹
     * 参数：
     * header（access_token）
     */
    public static final String COLLECTION_LIST = BASE_URL + "collections";
    /**
     * 创建书签
     * 参数：
     * name
     * header（access_token）
     */
    public static final String CREATE_COLLECTION = BASE_URL + "collections";
    /**
     * user
     * 参数：
     * header : access_token
     */
    public static final String USER_INFO = BASE_URL + "user";

    /**
     * move bookmarks
     * 参数：
     * header : access_token
     * collection_id : value
     */
    public static final String MOVE_BOOKMARK = BASE_URL + "bookmarks/";

    /**
     * /bookmarks/:id?ext=1&tmpl=1
     */
    public static final String CARD_TEMPLATE = BASE_URL + "bookmarks/";

    /**
     * /collections/:id
     */
    public static final String MODIFY_COLLECTION_NAME = BASE_URL + "collections/";
}
