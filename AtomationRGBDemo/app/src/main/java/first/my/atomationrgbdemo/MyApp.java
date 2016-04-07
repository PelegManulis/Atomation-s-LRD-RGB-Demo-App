package first.my.atomationrgbdemo;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;


import net.atomation.atomationsdk.ble.AtomManager;

/**
 * Created by ganitstoler on 1/13/16.
 */
public class MyApp extends Application {

    static AtomManager mAtomManager;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAtomManager = AtomManager.getInstance();
        mAtomManager.init(this, new BasicAtomDevice(), getString(R.string.token));
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}

