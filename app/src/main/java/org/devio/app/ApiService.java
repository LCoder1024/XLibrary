package org.devio.app;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.POST;

public interface ApiService {
    @POST("sysconfig/app.json")
    Observable<ResponseBody> getHomePageConfig();
}
