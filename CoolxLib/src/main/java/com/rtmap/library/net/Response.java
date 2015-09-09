package com.rtmap.library.net;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rtmap.library.Const;
import com.rtmap.library.utils.JSONUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Response {

    public String result;

    public boolean isCache;

    /**
     * 数据暂存
     */
    public Map<String, Object> bundle;

    public boolean isCache() {
        return isCache;
    }

    public void isCache(boolean isCache) {
        this.isCache = isCache;
    }

    // 操作是否成功
    public Boolean success;
    // 消息
    public String msg;
    // 错误码
    public String code;
    private JSONObject jo;

    public Response(String result) {
        bundle = new HashMap<String, Object>();
        this.result = result;
        this.success = true;
        // 默认处理结果为 true
        // 有返回success code 登按 返回结果
        if (!TextUtils.isEmpty(result)) {
            // json对象
            if (result.trim().startsWith("{")) {
                try {
                    jo = new JSONObject(result);
                    if (jo.has(Const.response_success)) {
                        success = JSONUtil.getBoolean(jo,
                                Const.response_success);
                    }
                    if (jo.has(Const.response_msg)) {
                        msg = jo.getString(Const.response_msg);
                    }
                    if (jo.has(Const.response_code)) {
                        code = JSONUtil.getString(jo, Const.response_code);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (result.trim().startsWith("[")) {
                // 不处理
            }
        }
    }

    /**
     * 添加传递数据 基本在后台线程添加 前台用getBundle 获取
     *
     * @param key
     * @param obj
     */
    public void addBundle(String key, Object obj) {
        bundle.put(key, obj);
    }

    /**
     * 获取传递数据
     *
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getBundle(String key) {
        return (T) bundle.get(key);
    }

    public String getResult() {
        return result;
    }

    public JSONObject jSON() {
        return jo;
    }

    public JSONArray jsonArray() {
        try {
            return new JSONArray(this.result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T getObject(Class<T> clazz) {
        Gson gson = new Gson();
        T obj = gson.fromJson(result, clazz);
        return obj;
    }


    public <T> List<T> getArrayObject(final Class<T> clazz) {
        Gson gson = new Gson();
        Type type = new ParameterizedType() {
            public Type getRawType() {
                return ArrayList.class;
            }

            public Type getOwnerType() {
                return null;
            }

            public Type[] getActualTypeArguments() {
                Type[] type = new Type[1];
                type[0] = clazz;
                return type;
            }
        };
        List<T> list = gson.fromJson(result, type);
        return list;
    }


    public <T> T getObjectFrom(String prefix) {
        if (jo != null) {
            String str = JSONUtil.getString(jo, prefix);
            Gson gson = new Gson();
            Type type = new TypeToken<T>() {
            }.getType();
            T obj = gson.fromJson(str, type);
            return obj;
        }
        return null;
    }

    public <T> T getObjectFrom(Class<T> clazz, String prefix) {
        String str = JSONUtil.getString(jo, prefix);
        Gson gson = new Gson();
        T obj = gson.fromJson(str, clazz);
        return obj;
    }

    public <T> T getObjectData() {
        return getObjectFrom(Const.response_data);
    }

    public <T> T getObjectData(Class<T> clazz) {
        return getObjectFrom(clazz, Const.response_data);
    }

    public <T> List<T> getArrayFrom(final Class<T> clazz, String prefix) {
        if (jo != null) {
            String str = JSONUtil.getString(jo, prefix);
            Gson gson = new Gson();
            Type type = new ParameterizedType() {
                public Type getRawType() {
                    return ArrayList.class;
                }

                public Type getOwnerType() {
                    return null;
                }

                public Type[] getActualTypeArguments() {
                    Type[] type = new Type[1];
                    type[0] = clazz;
                    return type;
                }
            };
            List<T> list = gson.fromJson(str, type);
            return list;
        }
        return null;
    }

    public <T> List<T> getArrayData(Class<T> clazz) {
        return getArrayFrom(clazz, Const.response_data);
    }

    public JSONArray jSONArrayFrom(String prefix) {
        if (jo != null) {
            return JSONUtil.getJSONArray(jo, prefix);
        } else {
            return jsonArray();
        }
    }


    public JSONArray jSONArrayFromData() {
        return jSONArrayFrom(Const.response_data);
    }

    public JSONObject jSONFromData() {
        return jSONFrom(Const.response_data);
    }


    public JSONObject jSONFrom(String prefix) {
        if (jo != null) {
            return JSONUtil.getJSONObject(jo, prefix);
        }
        return null;
    }

    public JSONObject getJo() {
        return jo;
    }

    public Boolean isSuccess() {
        return success;
    }


    public String getMsg() {
        return msg;
    }


    public String getCode() {
        return code;
    }


}
