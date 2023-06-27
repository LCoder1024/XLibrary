package org.devio.app;

import org.devio.xlibrary.http.NetworkApi;

import okhttp3.Interceptor;

/**
 *
 */
public class AppNetworkApi extends NetworkApi {

    private static AppNetworkApi sAppNetworkApi;

    public static AppNetworkApi getInstance() {
        if (sAppNetworkApi == null) {
            synchronized (AppNetworkApi.class) {
                if (sAppNetworkApi == null) {
                    sAppNetworkApi = new AppNetworkApi();
                }
            }
        }
        return sAppNetworkApi;
    }

    protected AppNetworkApi() {
        super(XConstant.BASE_URL);
    }


    public <T> T getApiService(Class<T> service) {
        return getInstance().getRetrofit(service.getName()).create(service);
    }

    @Override
    public int getTimeout() {
        return 10;
    }

    @Override
    public Interceptor getCookiesInterceptor() {
        return new AddCookiesInterceptor();
    }
}
