package com.rtmap.library.net.cache;

import com.rtmap.library.Const;
import com.rtmap.library.db.CooDb;
import com.rtmap.library.db.sqlite.Selector;
import com.rtmap.library.db.sqlite.WhereBuilder;
import com.rtmap.library.exception.DbException;
import com.rtmap.library.ioc.IocContainer;
import com.rtmap.library.utils.MD5;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;


public class CacheManager {

    private CooDb db;

    public CacheManager() {
        super();
        db = CooDb.create(IocContainer.getShare().getApplicationContext(), Const.DATABASE_NAME);
    }

    public void create(String url, Map<String, Object> params, String result) {
        delete(url, params);
        Cache cache = new Cache();
        cache.setKey(buildKey(url, params));
        cache.setResult(result);
        cache.setUpdateTime(System.currentTimeMillis());
        if (cache != null) {
            try {
                db.save(cache);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    public String get(String url, Map<String, Object> params) {
        Cache cache = null;
        try {
            cache = db.findFirst(Selector.from(Cache.class).where("key", "=", buildKey(url, params)));
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (cache != null) {
            return cache.getResult();
        }
        return null;
    }


    public void delete(String url, Map<String, Object> params) {
        try {
            db.delete(Cache.class, WhereBuilder.b("key", "=", buildKey(url, params)));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    /**
     * 删除多少天前的缓存
     *
     * @param dayAgo
     */
    public void deleteByDate(Integer dayAgo) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -dayAgo);
        Date time = calendar.getTime();
        try {
            db.delete(Cache.class, WhereBuilder.b("updateTime", "<", time.getTime()));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    private String buildKey(String url, Map<String, Object> params) {
        if (params != null) {
            url += params.toString();
        }
        try {
            return MD5.encryptMD5(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

}
