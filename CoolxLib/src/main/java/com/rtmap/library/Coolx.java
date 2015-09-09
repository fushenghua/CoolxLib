package com.rtmap.library;

import android.app.Application;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.rtmap.library.db.CooDb;
import com.rtmap.library.ioc.Ioc;

/**
 * 完成一些系统的初始化的工作
 *
 * @author Administrator
 */
public class Coolx {

    public static void init(Application app) {
        Ioc.initApplication(app);
        
        ImageLoaderConfiguration imageconfig = new ImageLoaderConfiguration.Builder(
                app.getApplicationContext())
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCacheSize(1500000)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .build();
        ImageLoader.getInstance().init(imageconfig);

        //数据库初始化
        CooDb.DaoConfig config = new CooDb.DaoConfig(app);
        config.setDbVersion(Const.DATABASE_VERSION);
        config.setDbName(Const.DATABASE_NAME);
        CooDb.create(config).configDebug(Const.DATABASE_DEBUG);

    }


}
