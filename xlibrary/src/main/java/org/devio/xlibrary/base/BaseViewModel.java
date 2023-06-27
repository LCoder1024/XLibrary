package org.devio.xlibrary.base;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleObserver;

import org.devio.xlibrary.widget.UIChangeLiveData;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BaseViewModel extends AndroidViewModel implements LifecycleObserver, Consumer<Disposable> {

    private UIChangeLiveData uiChangeLiveData;
    //管理RxJava，主要针对RxJava异步操作造成的内存泄漏
    private CompositeDisposable mCompositeDisposable;

    public BaseViewModel(@NonNull Application application) {
        super(application);
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void accept(Disposable disposable) {
        addSubscribe(disposable);
    }

    protected void addSubscribe(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }

    public UIChangeLiveData getUIChangeLiveData() {
        if (uiChangeLiveData == null) {
            uiChangeLiveData = new UIChangeLiveData();
        }
        return uiChangeLiveData;
    }

    public void showDialog() {
        uiChangeLiveData.showDialogEvent.call();
    }

    public void dismissDialog() {
        uiChangeLiveData.dismissDialogEvent.call();
    }

    public void getFailure(Throwable throwable) {
        uiChangeLiveData.failureEvent.postValue(throwable);
    }

    public <T> ObservableTransformer<T, T> loading() {
        return loading(true);
    }

    public <T> ObservableTransformer<T, T> loading(boolean isLoading) {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> {
                    if (isLoading) {
                        BaseViewModel.this.showDialog();
                    }
                })
                .doOnDispose(() -> {
                    if (isLoading) {
                        BaseViewModel.this.dismissDialog();
                    }
                })
                .doOnNext(t -> {
                    if (isLoading) {
                        BaseViewModel.this.dismissDialog();
                    }
                })
                .doOnError(throwable -> {
                    if (isLoading) {
                        BaseViewModel.this.dismissDialog();
                    }
                }).doOnComplete(() -> {
                    if (isLoading) {
                        BaseViewModel.this.dismissDialog();
                    }
                });
    }

}
