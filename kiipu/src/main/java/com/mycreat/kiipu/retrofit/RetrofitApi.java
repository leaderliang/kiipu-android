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
     * Get list
     * /bookmarks?count=10 & since_id=xxx
     */
    public static final String BOOK_MARKS_LIST = BASE_URL + "/bookmarks";
    /**
     * DELETE
     * /bookmarks/:id
     */
    public static final String DELETE_BOOK_MARKS = BASE_URL + "/bookmarks/";

}
