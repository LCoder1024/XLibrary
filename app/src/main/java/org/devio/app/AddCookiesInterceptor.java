package org.devio.app;

import android.util.Log;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.AppUtils;

import org.devio.xlibrary.http.XHttpUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * ClassName: AddCookiesInterceptor
 * Author: L
 * Date: 2022/4/21 9:16
 * Description: 拦截器 添加cookie
 */
public class AddCookiesInterceptor implements Interceptor {
    private static final String TAG = "NetworkRequestPrinting";

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder();
        requestBuilder.addHeader("userlogin", "");
        requestBuilder.addHeader("channel", "");
        requestBuilder.addHeader("zfbscene", "android " + AppUtils.getAppVersionName());
        Request request = requestBuilder.build();
        String url = request.url().toString();

        Response response = chain.proceed(request);
        Log.e(TAG, "————————————————————网络请求开始————————————————————");
        Log.e(TAG, "请求链接:" + url);
        Log.e(TAG, "请求参数:" + XHttpUtils.getRequestInfo(original));
        Log.e(TAG, "请求响应:" + XHttpUtils.getResponseInfo(response));
        Log.e(TAG, "————————————————————网络请求结束————————————————————");
        return response;
    }
}
