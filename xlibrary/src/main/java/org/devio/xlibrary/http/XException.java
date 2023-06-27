package org.devio.xlibrary.http;

import android.content.Context;
import android.net.ParseException;

import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.JsonParseException;

import org.devio.xlibrary.OnExceptionClickListener;
import org.devio.xlibrary.utils.XStringUtils;
import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.HttpException;

public class XException {
    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;


    public static String requestHandle(Context context, Throwable e) {
        return requestHandle(context, e, null);
    }

    public static String requestHandle(Context context, Throwable e, OnExceptionClickListener onExceptionClickListener) {
        String msg;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            switch (httpException.code()) {
                case UNAUTHORIZED:
                case FORBIDDEN:
                case NOT_FOUND:
                case REQUEST_TIMEOUT:
                case GATEWAY_TIMEOUT:
                case INTERNAL_SERVER_ERROR:
                case BAD_GATEWAY:
                case SERVICE_UNAVAILABLE:
                default:
                    msg = "服务器出小差了，请稍后再试";
                    break;
            }
        } else if (e instanceof ApiException) {
            //后台异常
            ApiException apiException = (ApiException) e;
            msg = XStringUtils.getStrEmpty(apiException.getMessage());
            if (onExceptionClickListener != null) {
                onExceptionClickListener.OnExceptionClick();
            }
        } else if (e instanceof JsonParseException || e instanceof JSONException || e instanceof ParseException) {
            msg = "数据解析出现异常";
        } else if (e instanceof ConnectException || e instanceof SocketTimeoutException || e instanceof UnknownHostException) {
            msg = "连接失败，请检查网络";
        } else if (e instanceof NumberFormatException) {
            msg = "数字格式化异常";
        } else {
            msg = "访问出错了,请稍后再试";
        }
        ToastUtils.showShort(msg);
        return msg;
    }


}
