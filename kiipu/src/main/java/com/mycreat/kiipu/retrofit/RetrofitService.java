package com.mycreat.kiipu.retrofit;

import com.mycreat.kiipu.model.Bookmark;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;


public interface RetrofitService {

    @GET(RetrofitApi.BOOK_MARKS_LIST)
    Call<List<Bookmark>> getBookmarkList(@Query("count") Integer number, @Query("since_id") String id);

    @DELETE(RetrofitApi.DELETE_BOOK_MARKS)
    Call<Bookmark> deleteBookmark(@Query(":id") String id);

}
