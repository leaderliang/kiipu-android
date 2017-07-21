package com.mycreat.kiipu.rxbus;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 管理 CompositeSubscription
 * <p>
 * Created by YoKeyword on 16/7/19.
 */
public class RxCompositeDisposable {
    private static CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public static boolean isUnsubscribed() {
        return mCompositeDisposable.isDisposed();
    }

    public static void add(Disposable s) {
        if (s != null) {
            mCompositeDisposable.add(s);
        }
    }

    public static void remove(Disposable s) {
        if (s != null) {
            mCompositeDisposable.remove(s);
        }
    }

    public static void clear() {
        mCompositeDisposable.clear();
    }

    public static void dispose() {
        mCompositeDisposable.dispose();
    }

    public static boolean hasSubscriptions() {
        return mCompositeDisposable.size() > 0;
    }
}
