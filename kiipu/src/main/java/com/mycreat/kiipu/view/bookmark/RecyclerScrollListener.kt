package com.mycreat.kiipu.view.bookmark

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.mycreat.kiipu.model.Bookmark
import com.mycreat.kiipu.rxbus.RxBus
import com.mycreat.kiipu.utils.LogUtil
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

import java.util.ArrayList

/**
 * Created by zhanghaihai on 2017/8/15.
 */
class RecyclerScrollListener(private val recyclerView: RecyclerView, private val adapter: BookmarkDetailAdapter) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val lastVisibleItemPosition = (recyclerView!!.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
        val firstVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
        if (lastVisibleItemPosition + 1 == recyclerView.adapter.itemCount) {
            adapter.loadingMore()
        }
    }

    private fun loadAfter(itemId: Int) {

        val observable = Observable.create(ObservableOnSubscribe<List<Bookmark>> { e ->
            LogUtil.d("loadAfter $itemId ... load data")
            Thread.sleep(3000)
            e.onNext(ArrayList<Bookmark>())
        })

        val dispose = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe { bookmarks ->
            adapter.loadedMore()
            adapter.addBookMarks(bookmarks)
        }

        dispose.dispose();
    }

    private fun loadBefore() {
        LogUtil.d("loadBefore ...")
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
    }

}
