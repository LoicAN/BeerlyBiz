package beerly.ansteph.beerlybiz.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import beerly.ansteph.beerlybiz.view.profile.Home;
import beerly.ansteph.beerlybiz.view.registration.Login;

/**
 * Created by loicstephan on 2018/06/19.
 */

public class SessionManager {


    SharedPreferences preferences;

    SharedPreferences beerPreferences;
    //Editor for shared preferences
    SharedPreferences.Editor editor;

    SharedPreferences.Editor beerPrefEditor;

    //Context
    Context _context;

    //shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "BeerlyBelovedPref";

    // All Shared Preferences Keys
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private static final String KEY_HAS_REGISTERED= "hasRegistered";

    public static final String KEY_USERNAME = "username";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_APIKEY = "apikey";
    public static final String KEY_ID = "id";

    public SessionManager(Context context)
    {
        this._context =context;
        preferences=_context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor = preferences.edit();

    }



    /**
     * Create login session
     * */
    public void createLoginSession (String id,String username, String email,String password )
    {
        //storing login value as true
        editor.putBoolean(KEY_IS_LOGGED_IN, true);

        editor.putString(KEY_ID, id);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);

        editor.putString(KEY_PASSWORD, password);
        //editor.putString(KEY_APIKEY, apikey);

        editor.commit();
    }


    public void checkLogin(){
        //Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, Login.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            //Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            //Starting Login Activity
            _context.startActivity(i);
        }else{
            Intent i = new Intent(_context, Home.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            //Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            //Starting Login Activity
            _context.startActivity(i);
        }

    }

    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){

        HashMap<String, String> user = new HashMap<>();

        //est id
        user.put(KEY_ID, preferences.getString(KEY_ID,null));
        //est name
        user.put(KEY_USERNAME, preferences.getString(KEY_USERNAME,null));
        //est email
        user.put(KEY_EMAIL, preferences.getString(KEY_EMAIL,null));
        //est password
        user.put(KEY_PASSWORD, preferences.getString(KEY_PASSWORD,null));

        return user;
    }


    /**
     * Clear session details
     * */
    public void logoutUser(){
        //Clearing all the data from Shared Preferences
        editor.clear();
        editor.commit();

        //After logout redirect user to login Activity

        Intent i = new Intent(_context, Login.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //Starting Login Activity
        _context.startActivity(i);

        //
    }


    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN,false);
    }

}
