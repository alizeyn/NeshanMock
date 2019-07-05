package ir.alizeyn.neshanmock.core;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

/**
 * @author alizeyn
 * Created at 4/8/19
 */
public class BaseApplication extends MultiDexApplication {

    public static Context context;

    @Override
    public void onCreate() {
        MultiDex.install(this);
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getBaseApplicationContext() {
        return context;
    }
}
