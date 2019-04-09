package ir.alizeyn.neshanmock.core;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

/**
 * @author alizeyn
 * Created at 4/8/19
 */
public class BaseApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        MultiDex.install(this);
        super.onCreate();
    }
}
