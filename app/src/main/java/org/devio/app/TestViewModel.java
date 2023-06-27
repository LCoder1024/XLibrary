package org.devio.app;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import org.devio.xlibrary.base.BaseViewModel;

import java.io.IOException;

public class TestViewModel extends BaseViewModel {
    public MutableLiveData<String> homePageConfigLiveData = new MutableLiveData<>();
    public TestViewModel(@NonNull Application application) {
        super(application);
    }

    public void getHomePageConfig() {
        addSubscribe(AppNetworkApi.getInstance().getApiService(ApiService.class)
                .getHomePageConfig()
                .compose(loading())
                .subscribe(responseBody -> {
                    try {
                        homePageConfigLiveData.setValue(responseBody.string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }, this::getFailure));
    }

    public MutableLiveData<String> getHomePageConfigLiveData() {
        return homePageConfigLiveData;
    }
}
