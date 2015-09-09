package com.rtmap.cxdemo;

import android.app.Application;

import com.rtmap.library.Const;
import com.rtmap.library.Coolx;

/**
 * Created by silver on 15-9-2.
 */
public class AppContext extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //常量配置
        Const.SUCCESS_CODE = "1";

        //初始化操作
        Coolx.init(this);
    }
}
