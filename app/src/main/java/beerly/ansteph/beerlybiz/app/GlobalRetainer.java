package beerly.ansteph.beerlybiz.app;

import android.app.Application;
import android.content.Context;

import beerly.ansteph.beerlybiz.model.Establishment;
import beerly.ansteph.beerlybiz.model.User;

/**
 * Created by loicstephan on 2018/06/19.
 */

public class GlobalRetainer extends Application{

    public static final String TAG = GlobalRetainer.class
            .getSimpleName();

    private static GlobalRetainer mInstance;
    private static Context mAppContext;

    public Establishment _grEstablishment = new Establishment();
    public User _grUser = new User();

    @Override
    public void onCreate() {
        super.onCreate();

        GlobalRetainer.mAppContext = getApplicationContext();
        mInstance = this;

    }


    public static synchronized GlobalRetainer getInstance(){
        return mInstance;
    }

    public static Context getAppContext() {
        return mAppContext;
    }

    public static void setAppContext(Context mAppContext) {
        GlobalRetainer.mAppContext = mAppContext;
    }


    public Establishment get_grEstablishment() {
        return _grEstablishment;
    }

    public void set_grEstablishment(Establishment _grEstablishment) {
        this._grEstablishment = _grEstablishment;
    }


    public User get_grUser() {
        return _grUser;
    }

    public void set_grUser(User _grUser) {
        this._grUser = _grUser;
    }
}
