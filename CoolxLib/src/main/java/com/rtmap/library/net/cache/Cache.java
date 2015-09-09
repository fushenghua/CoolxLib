package com.rtmap.library.net.cache;

import com.rtmap.library.db.annotation.Id;
import com.rtmap.library.db.annotation.Table;

@Table(name = "Cache")
public class Cache {

    @Id
    public Integer id;
    public String key;
    public String result;
    public Long updateTime;


    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}
