package com.rtmap.library.net;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URLEncoder;
import java.util.Map;

public class HttpManager {

    private static final int DEFAULT_MAX_CONNECTIONS = 30;

    public static final int DEFAULT_SOCKET_TIMEOUT = 30 * 1000;

    public static final int DEFAULT_SOCKET_TIMEOUT_SHORT = 10 * 1000;

    private static final int DEFAULT_SOCKET_BUFFER_SIZE = 8192;

    private static OkHttpClient sHttpClient;
    private static HttpManager mInstance;


    static {
        sHttpClient = new OkHttpClient();
        //cookie enabled
        sHttpClient.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
    }

    public static HttpManager getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpClientManager.class) {
                if (mInstance == null) {
                    mInstance = new HttpManager();
                }
            }
        }
        return mInstance;
    }

    public static void longTimeOut() {
//        HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_SOCKET_TIMEOUT);
//        HttpConnectionParams.setConnectionTimeout(httpParams,
//                DEFAULT_SOCKET_TIMEOUT);
//        ConnManagerParams.setTimeout(httpParams, DEFAULT_SOCKET_TIMEOUT);
    }

    public static void shortTimeOut() {
//        HttpConnectionParams.setSoTimeout(httpParams,
//                DEFAULT_SOCKET_TIMEOUT_SHORT);
//        HttpConnectionParams.setConnectionTimeout(httpParams,
//                DEFAULT_SOCKET_TIMEOUT_SHORT);
//        ConnManagerParams.setTimeout(httpParams, DEFAULT_SOCKET_TIMEOUT_SHORT);
    }


    private HttpManager() {
    }


    public void execute(String url, Map<String, Object> params, String method, Callback callback) {
        if (method.equalsIgnoreCase("POST")) {
            getInstance().enqueuePost(url, params, callback);
        } else {
            if (!"?".contains(url)) {
                url += "?";
            } else {
                if (!url.endsWith("&")) {
                    url += "&";
                }
            }
            url = url + encodeUrl(params);
            getInstance().enqueueGet(url, callback);
        }
    }

    public void enqueueGet(String url, Callback callback) {
        Request request = new Request.Builder().url(url).build();
        Call call = sHttpClient.newCall(request);
        call.enqueue(callback);
    }

    public void enqueuePost(String url, Map<String, Object> params, Callback callback) {
        FormEncodingBuilder builder = new FormEncodingBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            builder.add(entry.getKey(), entry.getValue().toString());
        }
        RequestBody body = builder.build();
        Request request = buildPostRequest(url, body);
        Call call = sHttpClient.newCall(request);
        call.enqueue(callback);
    }


    private Request buildPostRequest(String url, RequestBody body) {
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        return request;
    }

    public static String encodeUrl(Map<String, Object> params) {
        if (params == null || params.size() == 0)
            return "";
        StringBuilder sb = new StringBuilder();
        for (String key : params.keySet()) {
            if (params.get(key) != null) {
                sb.append(key.trim()).append("=").append(URLEncoder.encode((params.get(key).toString())))
                        .append("&");
            }
        }
        return sb.toString();
    }
}
