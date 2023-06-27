package org.devio.xlibrary.http;


import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class NetworkApi {
    private final String baseUrl;
    private Converter.Factory factory;
    private Interceptor cookiesInterceptor;
    private int timeout = 0;
    private final HashMap<String, Retrofit> retrofitMap = new HashMap<>();

    protected NetworkApi(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    protected Retrofit getRetrofit(String serviceName) {
        if (retrofitMap.get(baseUrl + serviceName) != null) {
            return retrofitMap.get(baseUrl + serviceName);
        }
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
//        interceptor.level(HttpLoggingInterceptor.Level.NONE);
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(interceptor);
        timeout = getTimeout();
        clientBuilder.connectTimeout(timeout != 0 ? timeout : 30, TimeUnit.SECONDS);
        clientBuilder.readTimeout(timeout != 0 ? timeout : 30, TimeUnit.SECONDS);
        clientBuilder.writeTimeout(timeout != 0 ? timeout : 30, TimeUnit.SECONDS);
        cookiesInterceptor = getCookiesInterceptor();
        if (cookiesInterceptor != null) {
            clientBuilder.addInterceptor(cookiesInterceptor);
        }
        OkHttpClient client = clientBuilder.build();
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(baseUrl);
        builder.client(client);
        factory = getFactory();
        if (factory == null) {
            builder.addConverterFactory(GsonConverterFactory.create());
        } else {
            builder.addConverterFactory(factory);
        }
        builder.addCallAdapterFactory(RxJava3CallAdapterFactory.create());
        Retrofit retrofit = builder.build();
        retrofitMap.put(baseUrl + serviceName, retrofit);
        return retrofit;
    }

    public Converter.Factory getFactory() {
        return factory;
    }

    public Interceptor getCookiesInterceptor() {
        return cookiesInterceptor;
    }

    public int getTimeout() {
        return timeout;
    }
}
