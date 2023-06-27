package org.devio.xlibrary.http;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public class XHttpUtils {
    /**
     * 打印请求消息
     *
     * @param request 请求的对象
     */
    public static String getRequestInfo(Request request) {
        if (request == null) {
            return "";
        }
        RequestBody requestBody = request.body();
        if (requestBody == null) {
            return "";
        }
        try {
            Buffer bufferedSink = new Buffer();
            requestBody.writeTo(bufferedSink);
            Charset charset = StandardCharsets.UTF_8;
            return bufferedSink.readString(charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 打印返回消息
     *
     * @param response 返回的对象
     */
    public static String getResponseInfo(Response response) {
        String str = "";
        if (response == null || !response.isSuccessful()) {
            return str;
        }
        ResponseBody responseBody = response.body();
        if (responseBody != null) {
            long contentLength = responseBody.contentLength();
            BufferedSource source = responseBody.source();
            try {
                source.request(Long.MAX_VALUE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Buffer buffer = source.getBuffer();
            Charset charset = StandardCharsets.UTF_8;
            if (contentLength != 0) {
                return buffer.clone().readString(charset);
            }
        }
        return "";
    }


}
