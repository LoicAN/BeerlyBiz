package beerly.ansteph.beerlybiz.view.profile;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.flaviofaria.kenburnsview.KenBurnsView;
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
import beerly.ansteph.beerlybiz.api.columns.PromotionColumns;
import beerly.ansteph.beerlybiz.api.columns.UserColumns;
import beerly.ansteph.beerlybiz.app.GlobalRetainer;
import beerly.ansteph.beerlybiz.helper.SessionManager;
import beerly.ansteph.beerlybiz.model.Establishment;
import beerly.ansteph.beerlybiz.model.User;
import beerly.ansteph.beerlybiz.slidingmenu.DrawerAdapter;
import beerly.ansteph.beerlybiz.slidingmenu.DrawerItem;
import beerly.ansteph.beerlybiz.slidingmenu.MenuPosition;
import beerly.ansteph.beerlybiz.slidingmenu.SimpleItem;
import beerly.ansteph.beerlybiz.slidingmenu.SpaceItem;
import beerly.ansteph.beerlybiz.view.promotion.EditPromotion;
import beerly.ansteph.beerlybiz.view.promotion.PromotionList;
import beerly.ansteph.beerlybiz.view.registration.Login;

public class Home extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {

    private  static String TAG = Home.class.getSimpleName();


    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slidingRootNav;

    ArrayList<Establishment> mEstablishments ;
    KenBurnsView mProfilePic;
    Establishment mCurrentEstabliment;

    SessionManager sessionManager;
    GlobalRetainer mGlobalRetainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mGlobalRetainer = (GlobalRetainer) getApplicationContext();
        sessionManager = new SessionManager(getApplicationContext());

        HashMap<String, String> estDetails = sessionManager.getUserDetails();

        String email= estDetails.get(SessionManager.KEY_USERNAME);
        String password= estDetails.get(SessionManager.KEY_PASSWORD);


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
                createItemFor(MenuPosition.POS_HOME).setChecked(true),
                createItemFor(MenuPosition.POS_PROFILE),
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

        adapter.setSelected(MenuPosition.POS_HOME);

        //////////**************************End Related to the drawer menu**************************////////

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), EditPromotion.class));
            }
        });

        mGlobalRetainer = (GlobalRetainer) getApplicationContext();
        sessionManager = new SessionManager(getApplicationContext());

        if(mGlobalRetainer.get_grEstablishment()!=null && mGlobalRetainer.get_grEstablishment().getId()!=0)
        {
            setupUI(mGlobalRetainer.get_grEstablishment());
        }else{

           try {
               getEstablishmentData(email,password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }




    }






    //////////**************************Related to the drawer menu**************************////////
    private void signOut() {
       // mAuth.signOut();
        //updateUI(null);
        sessionManager.logoutUser();
      //  startActivity(new Intent(getApplicationContext(), Login.class));
       // this .finish();

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
            case MenuPosition.POS_HOME: break;
            case MenuPosition.POS_PROFILE:intent = new Intent(getApplicationContext(), EstablishmentProfile.class);break;
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
                est.setId(estjson.getInt(EstablishmentColumns.ESTABLISHMENT_ID));
                est.setName(estjson.getString(EstablishmentColumns.NAME));
                est.setAddress(estjson.getString(EstablishmentColumns.ADDRESS));
                est.setLiqour_license(estjson.getString(EstablishmentColumns.LIQUORLICENCE));
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


        setupUI(mEstablishments.get(0));



    }



    private void getEstablishmentData(final String email, final String password) throws JSONException
    {
        String url = String.format(Routes.URL_EST_LOGIN);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    String status = jsonResponse.getString("status");
                    String msg = jsonResponse.getString("message");


                    if(status.equals("200"))
                    {
                        JSONObject establishment = jsonResponse.getJSONObject("establishment_information");
                        JSONObject user = jsonResponse.getJSONObject("user");


                        loadEstablishmentData(establishment,user);

                        //Toast.makeText(getApplicationContext(), status+ " "+msg+" "+establishment.getString("name"), Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(), "Welcome back!", Toast.LENGTH_LONG).show();


                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };




        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        requestQueue.add(stringRequest);
    }




    private void loadEstablishmentData(JSONObject establishment,JSONObject user)
    {

        //load the establishment data only
        Establishment est = null;
        try {

            est = new Establishment();

            est.setId(establishment.getInt(EstablishmentColumns.ESTABLISHMENT_ID));
            est.setName(establishment.getString(EstablishmentColumns.NAME));
            est.setAddress(establishment.getString(EstablishmentColumns.ADDRESS));
            est.setLiqour_license(establishment.getString(EstablishmentColumns.LIQUORLICENCE));
            est.setContact_person(establishment.getString(EstablishmentColumns.CONTACTPERSON));
            est.setContact_number(establishment.getString(EstablishmentColumns.CONTACTNUMBER));
            est.setEstablishment_url(establishment.getString(EstablishmentColumns.URL));
            est.setLatitude(establishment.getString(EstablishmentColumns.LATITUDE));
            est.setLongitude(establishment.getString(EstablishmentColumns.LONGITUDE));

            est.setMain_picture_url(establishment.getString(EstablishmentColumns.URLMAINPIC));
            est.setPicture_2_url(establishment.getString("picture_2"));
            // est.set(estjson.getString("hs_license"));
            // est.setName(estjson.getString(""));

        }catch (JSONException e)
        {
            e.printStackTrace();
        }

        //load the user data only
        User cUser = null;
        try {

            cUser = new User();

            cUser.setId(user.getInt(UserColumns.ID));
            cUser.setFirstName(user.getString(UserColumns.FIRST_NAME));
            cUser.setLastName(user.getString(UserColumns.LAST_NAME));
            cUser.setUsername(user.getString(UserColumns.USERNAME));
            cUser.setContactNumber(user.getString(UserColumns.CONTACT_NUMBER));
            cUser.setCreated_at(user.getString(UserColumns.CREATED_AT));


            // est.set(estjson.getString("hs_license"));
            // est.setName(estjson.getString(""));

        }catch (JSONException e)
        {
            e.printStackTrace();
        }



        if(cUser!=null)
        {

            mGlobalRetainer.set_grUser(cUser);
            startActivity(new Intent(getApplicationContext(), Home.class));
        }


        if(est!=null)
        {

            mGlobalRetainer.set_grEstablishment(est);
            setupUI(est);
            startActivity(new Intent(getApplicationContext(), Home.class));
        }




    }







    void setupUI(Establishment est)
    {
        TextView txtname = (TextView) findViewById(R.id.txtestName);
        TextView txtAddress = (TextView) findViewById(R.id.txtaddress);
        TextView txtSpeciality = (TextView) findViewById(R.id.txtspeciality);

        mProfilePic = (KenBurnsView) findViewById(R.id.mainPicture);

        String imgUrl= Routes.DOMAIN+est.getMain_picture_url();

        Glide.with(getApplicationContext()).load(imgUrl).
                thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mProfilePic);

        txtname.setText(est.getName());
        txtAddress.setText(est.getAddress());
    }







}
