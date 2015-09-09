package com.rtmap.library.net;

import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.rtmap.library.Const;
import com.rtmap.library.ioc.IocContainer;
import com.rtmap.library.net.cache.CacheManager;
import com.rtmap.library.net.cache.CachePolicy;
import com.rtmap.library.utils.LogUtils;
import com.rtmap.library.utils.NetworkUtils;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * 网络任务处理 默认有的 code <br/>
 * netErrorButCache 网络访问超时,但是使用了缓存 <br/>
 * netCanceled 长传文件时取消了任务<br/>
 * noNetError 没有可用的网络<br/>
 * netError 其他网络故障
 */
public class CNet {

    private String url = null;
    private Map<String, Object> params = new HashMap<String, Object>();
    private Map<String, File> files = new HashMap<String, File>();

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";


    private String method = "POST";

    Boolean isCanceled = false;

    NetTask task;
    String progressMsg;

    CacheManager cacheManager;

    CachePolicy cachePolicy = CachePolicy.POLICY_NOCACHE;

    // 最后一次访问网络花费的时间
    private static int lastSpeed = 10;

    GlobalParams globalParams;

    public CNet() {
        this(null);
    }

    public CNet(String url) {
        this(url, null);
    }

    public CNet(String url, Map<String, Object> params) {
        super();
        if (url != null) {
            this.url = url.trim();
        }
        if (params != null) {
            this.params.putAll(params);
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 清空
     */
    public void clear() {
        params.clear();
        if (globalParams != null) {
            Map<String, String> globalparams = globalParams.getGlobalParams();
            this.params.putAll(globalparams);
        }
        files.clear();
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    /**
     * 使用缓存
     *
     * @param policy
     */
    public void useCache(CachePolicy policy) {
        this.cachePolicy = policy;
        if (cachePolicy != CachePolicy.POLICY_NOCACHE) {
            if (cacheManager == null) {
                cacheManager = new CacheManager();
            }
        }
    }

    /**
     * 使用緩存
     */
    public void useCache(Boolean userCache) {
        if (userCache) {
            this.cachePolicy = CachePolicy.POLICY_ON_NET_ERROR;
            if (cachePolicy != CachePolicy.POLICY_NOCACHE) {
                if (cacheManager == null) {
                    cacheManager = new CacheManager();
                }
            }
        } else {
            this.cachePolicy = CachePolicy.POLICY_NOCACHE;
        }
    }

    /**
     * 使用緩存
     */
    public void useCache() {
        this.cachePolicy = CachePolicy.POLICY_ON_NET_ERROR;
        if (cachePolicy != CachePolicy.POLICY_NOCACHE) {
            if (cacheManager == null) {
                cacheManager = new CacheManager();
            }
        }
    }


    /**
     * 添加参数
     *
     * @param key
     * @param value
     * @return
     */
    public CNet addParam(String key, Object value) {
        if (value instanceof TextView) {
            TextView text = (TextView) value;
            this.params.put(key.trim(), text.getText().toString());
        } else {
            this.params.put(key.trim(), value);
        }
        return this;
    }

    public CNet addParams(Map<String, Object> params) {
        this.params.putAll(params);
        return this;
    }

    public CNet setMethod(String mehtod) {
        this.method = mehtod;
        return this;
    }

    public CNet get(NetTask task) {
        this.method = METHOD_GET;
        execuse(task);
        return this;
    }

    public CNet get(boolean dialog, NetTask task) {
        this.method = METHOD_GET;
        return dialog ? execuseInDialog(task) : execuse(task);
    }

    public CNet get(boolean dialog, String msg, NetTask task) {
        this.method = METHOD_GET;
        if (!TextUtils.isEmpty(msg)) {
            setProgressMsg(msg);
        }
        return dialog ? execuseInDialog(task) : execuse(task);
    }

    public CNet doGetInDialog(NetTask task) {
        this.method = METHOD_GET;
        execuseInDialog(task);
        return this;
    }

    public CNet post(NetTask task) {
        this.method = METHOD_POST;
        execuse(task);
        return this;
    }

    public CNet doPostInDialog(NetTask task) {
        this.method = METHOD_POST;
        execuseInDialog(task);
        return this;
    }

    /**
     * post方法访问 ,同时打开对话框
     *
     * @param task
     * @return
     */
    public CNet doPostInDialog(String msg, NetTask task) {
        if (!TextUtils.isEmpty(msg)) {
            setProgressMsg(msg);
        }
        this.method = METHOD_POST;
        execuseInDialog(task);
        return this;
    }

    /**
     * 执行网络访问
     *
     * @param task
     * @return
     */
    public CNet execuse(NetTask task) {
        this.task = task;
        boolean isCacheOk = false;
        //添加全局参数
        globalParams = IocContainer.getShare().get(GlobalParams.class);
        if (globalParams != null) {
            Map<String, String> globalparams = globalParams.getGlobalParams();
            params.putAll(globalparams);
        }
        //read cache
        if (cachePolicy == CachePolicy.POLICY_CACHE_ONLY
                || cachePolicy == CachePolicy.POLICY_CACHE_AndRefresh
                || cachePolicy == CachePolicy.POLICY_BEFORE_AND_AFTER_NET) {
            if (cacheManager != null) {
                String result = cacheManager.get(url, params);
                if (result != null) {
                    Response response = new Response(result);
                    response.isCache(true);
                    try {
                        CNet.this.task.doInBackground(response);
                        CNet.this.task.onSuccess(response,
                                NetTask.TRANSFER_DOUI_ForCache);
                        // 缓存有数据就返回
                        isCacheOk = true;
                        if (CNet.this.task.dialog != null) {
                            CNet.this.task.dialog.dismiss();
                        }
                    } catch (Exception e) {
                    }

                    if (cachePolicy == CachePolicy.POLICY_CACHE_ONLY) {
                        return this;
                    }
                }
            }
        }

        final boolean isCacheOkf = isCacheOk;// 是否使用了缓存
        boolean hasNet = NetworkUtils.isNetworkAvailable();
        if (hasNet) {
            final long begin = System.currentTimeMillis();
            HttpManager.getInstance().execute(url, params, method, new Callback() {

                @Override
                public void onResponse(com.squareup.okhttp.Response result) throws IOException {

                    if (result.isSuccessful()) {
                        Response response = new Response(result.body().string());
                        response.isCache(false);
                        String code = response.getCode();
                        CNet.this.task.transfer(response,
                                NetTask.TRANSFER_CODE);
                        try {
                            CNet.this.task.doInBackground(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (Const.SUCCESS_CODE.equals(code)) {
                            if (cacheManager != null
                                    && cachePolicy != CachePolicy.POLICY_NOCACHE) {
                                if (response.getJo() != null) {
                                    cacheManager.create(url, params,
                                            response.result);
                                }
                            }
                        }

                        if (!isCanceled) {
                            // 当没有使用缓存或者缓存策略是请求后更新
                            if (!isCacheOkf
                                    || cachePolicy == CachePolicy.POLICY_BEFORE_AND_AFTER_NET) {
                                CNet.this.task.transfer(response,
                                        NetTask.TRANSFER_DOUI);
                            }
                        }

                    } else {
                        throw new IOException("Unexpected code " + result);
                    }

                    long end = System.currentTimeMillis();
                    long time = end - begin;
                    LogUtils.d(CNet.this.url + " method: "
                            + method + ", params: " + params + ", result: "
                            + result.toString() + ",time:" + time);
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    onNetError(e, isCacheOkf);
                }
            });
        } else {
            onNoNet(isCacheOkf);
        }


        return this;
    }


    private String execuseOnCache() {


        return null;
    }

    private void onNoNet(Boolean hasUserCache) {

        if (cachePolicy == CachePolicy.POLICY_ON_NET_ERROR) {
            if (cacheManager != null) {
                String result = cacheManager.get(url, params);
                if (result != null) {
                    Response response = new Response(result);
                    response.isCache(true);
                    CNet.this.task.doInBackground(response);
                    CNet.this.task.transfer(response,
                            NetTask.TRANSFER_DOUI_ForCache);

                    LogUtils.d("CNet" + CNet.this.url + " method: "
                            + method + " params: " + params + " result: "
                            + response.toString() + "cache:true" + "@onNoNet");
                }
            }
        }

        String errorjson = "{'success':false,'msg':'没有可用的网络','code':'noNetError'}";
        Response response = new Response(errorjson);
        CNet.this.task.transfer(response, NetTask.TRANSFER_DOERROR);
    }

    /**
     * 处理网路异常
     *
     * @param e
     */
    private void onNetError(Exception e, Boolean hasUserCache) {
        lastSpeed = HttpManager.DEFAULT_SOCKET_TIMEOUT + 1;
        // 网络访问出错
        if (e instanceof UnknownHostException) {
            Log.e("DNet", "域名不对可能是没有配置网络权限");
        }
        boolean isFromCache = false;
        if (cacheManager != null
                && cachePolicy == CachePolicy.POLICY_ON_NET_ERROR) {
            String result = cacheManager.get(url, params);
            if (result != null) {
                isFromCache = true;
                Response response = new Response(result);
                response.isCache(true);
                CNet.this.task.doInBackground(response);
                CNet.this.task.transfer(response,
                        NetTask.TRANSFER_DOUI_ForCache);

                LogUtils.d("CNet" + CNet.this.url + " method: "
                        + method + " params: " + params + " result: "
                        + response.toString() + "cache:true" + "@onNetError");
            }
        }
        // 同时提示网络问题
        String errorjson;
        if (isFromCache) {
            errorjson = "{'success':false,'msg':'当前网络信号不好,使用缓存数据','code':'netErrorButCache'}";
        } else {
            errorjson = "{'success':false,'msg':'当前网络信号不好','code':'netError'}";
        }
        Response response = new Response(errorjson);
        response.addBundle("e", e);
        CNet.this.task.transfer(response, NetTask.TRANSFER_DOERROR);
    }

    /**
     * 执行同时打开对话框
     *
     * @param task
     * @return
     */
    public CNet execuseInDialog(NetTask task) {
        String msg = progressMsg;
        if (TextUtils.isEmpty(msg)) {
            msg = method.toUpperCase().equals(METHOD_GET) ? "加载中..." : "提交中...";
        }
//        IDialog dialoger = IocContainer.getShare().get(IDialog.class);
//        if (dialoger != null) {
//            Dialog dialog = dialoger
//                    .showProgressDialog(task.mContext, msg);
//            task.dialog = dialog;
//        }
        execuse(task);
        return this;
    }


    /**
     * 取消访问 如果访问没有开始就永远不会启动访问<br/>
     * 如果访问已经启动 如果isInterrupt 为 true 则访问会被打断 , 否则 会线程继续运行 取消时必定会调用 task
     * 的onCancel方法
     *
     * @return
     */
    public Boolean cancel(Boolean isInterrupt) {
        this.isCanceled = true;
        if (task != null) {
            task.onCancelled();
        }
        return true;
    }

    /**
     * 当网络访问没启动或被取消都返回 false
     *
     * @return
     */
    public Boolean isCanceled() {
        if (isCanceled != null) {
            return isCanceled;
        }
        return false;
    }

    public int TRANSFER_UPLOADING = -40000;


    public CNet addFile(String name, File file) {
        files.put(name, file);
        return this;
    }

    public void upload(NetTask task) {
    }


    /**
     * 文件上传, 支持大文件的上传 和文件的上传进度更新 task inui response 的bundle参数 uploading true
     * 上传中,false 上传完毕 ; process 上传进度 0-100 cancel 方法可以取消上传
     *
     * @param url
     * @param name
     * @param file
     * @param task
     */
    public void upload(final String name, final File file, NetTask task) {
        addFile(name, file);
        upload(task);
    }


    public void setProgressMsg(String progressMsg) {
//		this.progressMsg = progressMsg;
    }
}
