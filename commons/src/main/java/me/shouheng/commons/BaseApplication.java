package me.shouheng.commons;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.squareup.leakcanary.LeakCanary;

/**
 * @author shouh
 * @version $Id: BaseApplication, v 0.1 2018/6/6 21:58 shouh Exp$
 */
public abstract class BaseApplication extends Application {

    private static BaseApplication instance;

    public static BaseApplication getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        MultiDex.install(this);

        LeakCanary.install(this);
    }
}
