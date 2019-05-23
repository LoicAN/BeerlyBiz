package beerly.ansteph.beerlybiz.view.profile;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.thunder413.datetimeutils.DateTimeStyle;
import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import beerly.ansteph.beerlybiz.R;
import beerly.ansteph.beerlybiz.api.Routes;
import beerly.ansteph.beerlybiz.api.columns.EstablishmentColumns;
import beerly.ansteph.beerlybiz.app.GlobalRetainer;
import beerly.ansteph.beerlybiz.helper.SessionManager;
import beerly.ansteph.beerlybiz.model.Establishment;
import beerly.ansteph.beerlybiz.slidingmenu.DrawerAdapter;
import beerly.ansteph.beerlybiz.slidingmenu.DrawerItem;
import beerly.ansteph.beerlybiz.slidingmenu.MenuPosition;
import beerly.ansteph.beerlybiz.slidingmenu.SimpleItem;
import beerly.ansteph.beerlybiz.slidingmenu.SpaceItem;
import beerly.ansteph.beerlybiz.view.promotion.PromotionList;
import beerly.ansteph.beerlybiz.view.registration.Login;

public class EstablishmentProfile extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {

    private  static String TAG = EstablishmentProfile.class.getSimpleName();


    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slidingRootNav;

    ArrayList<Establishment> mEstablishments ;

    Establishment mCurrentEstablishment;

    SessionManager sessionManager;
    GlobalRetainer mGlobalRetainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_establishment_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mGlobalRetainer = (GlobalRetainer) getApplicationContext();
        sessionManager = new SessionManager(getApplicationContext());

        //////////**************************Related to the drawer menu**************************////////

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(MenuPosition.POS_HOME),
                createItemFor(MenuPosition.POS_PROFILE).setChecked(true),
                createItemFor(MenuPosition.POS_PROMOTION),
                createItemFor(MenuPosition.POS_ACCOUNT),

                //   createItemFor(MenuPosition.POS_AFFILIATE),
                new SpaceItem(48),
                createItemFor(MenuPosition.POS_LOGOUT)));
        adapter.setListener(this);


        RecyclerView list = (RecyclerView) findViewById(R.id.list);

        // RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setSelected(MenuPosition.POS_PROFILE);

        //////////**************************End Related to the drawer menu**************************////////

        if(mGlobalRetainer.get_grEstablishment()!=null)
        {
            fillFields(mGlobalRetainer.get_grEstablishment());
        }

        /*try {
            getEstablismentData();
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }



    //////////**************************Related to the drawer menu**************************////////
    private void signOut() {
        // mAuth.signOut();
        //updateUI(null);
        startActivity(new Intent(getApplicationContext(), Login.class));

    }
    @Override
    public void onItemSelected(int position) {
        if (position == MenuPosition.POS_LOGOUT || position == MenuPosition.EF_POS_LOGOUT) {
            signOut();
            //  finish();
        }
        slidingRootNav.closeMenu();

        Intent intent = null;

        switch (position)
        {
            case MenuPosition.POS_HOME:intent = new Intent(getApplicationContext(), Home.class); break;
            case MenuPosition.POS_PROFILE:break;
            case MenuPosition.POS_PROMOTION:intent = new Intent(getApplicationContext(), PromotionList.class);break;
            case MenuPosition.POS_ACCOUNT:intent = new Intent(getApplicationContext(), Account.class);break;



        }

        if(intent!=null)
        {
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.textColorSecondary))
                .withTextTint(color(R.color.textColorPrimary))
                .withSelectedIconTint(color(R.color.colorAccent))
                .withSelectedTextTint(color(R.color.colorAccent));
    }


    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }
    //////////**************************End Related to the drawer menu**************************////////


    private void getEstablismentData() throws JSONException
    {

        //checkConnection();

        String url = String.format(Routes.URL_RETRIEVE_EST);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                loadEstablishment(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        requestQueue.add(jsonArrayRequest);
    }


    private void loadEstablishment(JSONArray estjsonArray)
    {
        ArrayList<Establishment> establishments = new ArrayList<>();

        for(int i = 0; i<estjsonArray.length(); i++)
        {
            try{
                JSONObject estjson = estjsonArray.getJSONObject(i);

                Establishment est  = new Establishment();
                est.setId(estjson.getInt(EstablishmentColumns.EST_ID));
                est.setName(estjson.getString(EstablishmentColumns.NAME));
                est.setAddress(estjson.getString(EstablishmentColumns.ADDRESS));
                est.setLiqour_license(estjson.getString(EstablishmentColumns.LIQUORLICENCE));
                est.setHs_license(estjson.getString(EstablishmentColumns.HSLICENCE));
                est.setLast_inspection_date(estjson.getString(EstablishmentColumns.DATELASTINSPECTION));
                est.setContact_person(estjson.getString(EstablishmentColumns.CONTACTPERSON));
                est.setContact_number(estjson.getString(EstablishmentColumns.CONTACTNUMBER));
                est.setEstablishment_url(estjson.getString(EstablishmentColumns.URL));
                est.setLatitude(estjson.getString(EstablishmentColumns.LATITUDE));
                est.setLongitude(estjson.getString(EstablishmentColumns.LONGITUDE));

                est.setMain_picture_url(estjson.getString(EstablishmentColumns.URLMAINPIC));
                est.setPicture_2_url(estjson.getString("picture_2"));
                // est.set(estjson.getString("hs_license"));
                // est.setName(estjson.getString(""));

                establishments.add(est);


            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        // initViewPager(establishments);
        //initMarker(establishments);
        mEstablishments = establishments;


        fillFields(establishments.get(0));



    }

    void fillFields(Establishment est)
    {
        //filling the corresponding value

        ((EditText)findViewById(R.id.txtEstAccountName)).setText(est.getName());
        ((EditText)findViewById(R.id.txtEstName)).setText(est.getName());

        ((EditText)findViewById(R.id.txtEstAddress)).setText(est.getAddress());
        ((EditText)findViewById(R.id.txtEstLatitude)).setText(est.getLatitude());


        ((EditText)findViewById(R.id.txtEstLongitude)).setText(est.getLongitude());
        ((EditText)findViewById(R.id.txtEstLiquorLicense)).setText(est.getLiqour_license());

        ((EditText)findViewById(R.id.txtEstHS)).setText(est.getHs_license());
        ((EditText)findViewById(R.id.txtEstContactPerson)).setText(est.getContact_person());

        ((EditText)findViewById(R.id.txtEstContactNum)).setText(est.getContact_number());

        String inspDate = "";
        try{
            inspDate=  DateTimeUtils.formatWithStyle(est.getLast_inspection_date(), DateTimeStyle.MEDIUM);
        }catch (Exception e)
        {
            e.printStackTrace();
        }


        ((TextView)findViewById(R.id.txtEstInspectionDate)).setText(inspDate);

        ((EditText)findViewById(R.id.txtEsturl)).setText(est.getEstablishment_url());

    }




    void prepareSaving(){

        String EstAccountName = ((EditText)findViewById(R.id.txtEstAccountName)).getText().toString();
        String EstName = ((EditText)findViewById(R.id.txtEstName)).getText().toString();
        String EstAddress = ((EditText)findViewById(R.id.txtEstAddress)).getText().toString();
        String EstLatitude = ((EditText)findViewById(R.id.txtEstLatitude)).getText().toString();
        String EstLongitude = ((EditText)findViewById(R.id.txtEstLongitude)).getText().toString();
        String EstLiquorLicense = ((EditText)findViewById(R.id.txtEstLiquorLicense)).getText().toString();
        String EstEstHS = ((EditText)findViewById(R.id.txtEstHS)).getText().toString();
        String EstContactPerson = ((EditText)findViewById(R.id.txtEstContactPerson)).getText().toString();
        String EstContactNum = ((EditText)findViewById(R.id.txtEstContactNum)).getText().toString();
        String EstInspectionDate = ((TextView)findViewById(R.id.txtEstInspectionDate)).getText().toString();
        String EstUrl = ((EditText)findViewById(R.id.txtEsturl)).getText().toString();

        mCurrentEstablishment.setAddress(EstAddress);
        mCurrentEstablishment.setLast_inspection_date(EstInspectionDate);
        mCurrentEstablishment.setName(EstName);
        mCurrentEstablishment.setLatitude(EstLatitude);
        mCurrentEstablishment.setLongitude(EstLongitude);
        mCurrentEstablishment.setLiqour_license(EstLiquorLicense);

        mCurrentEstablishment.setHs_license(EstEstHS);
        mCurrentEstablishment.setContact_person(EstContactPerson);
        mCurrentEstablishment.setContact_number(EstContactNum);
       // mCurrentEstablishment.setLast_inspection_date();
        mCurrentEstablishment.setEstablishment_url(EstUrl);



    }



    public void UpdateEstProfile() throws JSONException {
        // final ProgressDialog loading = ProgressDialog.show(getApplicationContext(), "Registering", "Please wait... you will soon be part of the class",false,false);
//Getting user data
       /* final String username = txtUserName.getText().toString().trim();
        final String gender = spnGender.getSelectedItem().toString();
        final String dob =txtBirthdate.getText().toString().trim();
        final  String  home_city = spnCity.getSelectedItem().toString();
        final  String  referral_code = (mRefCode!=null)? mRefCode:"" ;

        final String cocktail  = (chkCocktails.isChecked()) ? "1":"0";
        final String cocktailtype = (spnCocktail.getVisibility() == View.VISIBLE)?spnCocktail.getSelectedItem().toString():"none";
        final String shottype = (spnShot.getVisibility() == View.VISIBLE)?spnShot.getSelectedItem().toString():"none";
        final String shot  = (chkShots.isChecked()) ? "1":"0";
        // final  String passwd = edtPwd.getText().toString().trim();
        //final String origin = spnOrigin.getSelectedItem().toString();*/

        String url = String.format(Routes.URL_UPDATE_LOVER_PROFILE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //  loading.dismiss();

                try{
                    //creating the Json object from the response
                    JSONObject jsonResponse = new JSONObject(response);

                  //  startActivity(new Intent(getApplicationContext(), LoverProfile.class));
                  /*  boolean error = jsonResponse.getBoolean(Routes.ERROR_RESPONSE);
                    String serverMsg = jsonResponse.getString(Routes.MSG_RESPONSE);
                    //if it is success
                    if(!error)
                    {
                        //asking user to confirm OTP
                       // confirmOtp();

                        sessionManager.recordRegistration();

                    }else{ //check for message already existing user
                        Toast.makeText(getApplicationContext(), serverMsg, Toast.LENGTH_LONG).show();

                    }*/


                }catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //   loading.dismiss();
                Toast.makeText(getApplicationContext(), error.getMessage()+"Oups! Could not talk to the server at this time, try again later",Toast.LENGTH_LONG).show();
            }
        }

        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();


                params.put(EstablishmentColumns.EST_ID,String.valueOf(mCurrentEstablishment.getId()) );
                params.put(EstablishmentColumns.NAME, mCurrentEstablishment.getName());
                params.put(EstablishmentColumns.ADDRESS, mCurrentEstablishment.getName());
                params.put(EstablishmentColumns.LIQUORLICENCE, mCurrentEstablishment.getLiqour_license());
                params.put(EstablishmentColumns.HSLICENCE, mCurrentEstablishment.getHs_license());
                params.put(EstablishmentColumns.DATELASTINSPECTION, mCurrentEstablishment.getLast_inspection_date());
                params.put(EstablishmentColumns.CONTACTPERSON, mCurrentEstablishment.getContact_person());
                params.put(EstablishmentColumns.CONTACTNUMBER, mCurrentEstablishment.getContact_number());
                params.put(EstablishmentColumns.URL, mCurrentEstablishment.getEstablishment_url());
                params.put(EstablishmentColumns.LATITUDE, mCurrentEstablishment.getLatitude());
                params.put(EstablishmentColumns.LONGITUDE, mCurrentEstablishment.getLongitude());


                return params;
            }
        };


        //  GlobalRetainer.getInstance().addToRequestQueue();
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }


    public void onDoneClicked(View view){
        startActivity(new Intent(getApplicationContext(),Home.class));
    }

}
