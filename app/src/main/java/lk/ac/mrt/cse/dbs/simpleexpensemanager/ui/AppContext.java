package lk.ac.mrt.cse.dbs.simpleexpensemanager.ui;

import android.app.Application;
import android.content.Context;

/**
 * Created by Pasindu Tennage on 2015-12-04.
 */
public class AppContext extends Application{
    private static Context context;
    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();
    }
    public static Context getContext(){
        return context;
    }

}
