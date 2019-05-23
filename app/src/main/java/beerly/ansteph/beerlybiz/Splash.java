package beerly.ansteph.beerlybiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.crashlytics.android.Crashlytics;

import beerly.ansteph.beerlybiz.helper.SessionManager;
import beerly.ansteph.beerlybiz.view.profile.Home;
import beerly.ansteph.beerlybiz.view.registration.Login;
import io.fabric.sdk.android.Fabric;

public class Splash extends AppCompatActivity {

    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);
        sessionManager = new SessionManager(getApplicationContext());
        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(4000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    /**
                     * Call this function whenever you want to check user login
                     * This will redirect user to Login is he is not
                     * logged in
                     * */
                    sessionManager.checkLogin();
                    //Intent intent = new Intent(getApplicationContext(), Login.class);
                    //startActivity(intent);



                }
            }
        };
        timerThread.start();

    }
}
