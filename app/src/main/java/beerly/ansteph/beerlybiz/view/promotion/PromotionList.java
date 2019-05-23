package beerly.ansteph.beerlybiz.view.promotion;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import beerly.ansteph.beerlybiz.R;
import beerly.ansteph.beerlybiz.adapter.PromotionRecyclerAdapter;
import beerly.ansteph.beerlybiz.api.Routes;
import beerly.ansteph.beerlybiz.api.columns.PromotionColumns;
import beerly.ansteph.beerlybiz.app.GlobalRetainer;
import beerly.ansteph.beerlybiz.helper.RecyclerItemTouchHelper;
import beerly.ansteph.beerlybiz.helper.SessionManager;
import beerly.ansteph.beerlybiz.model.Beer;
import beerly.ansteph.beerlybiz.model.Promotion;
import beerly.ansteph.beerlybiz.slidingmenu.DrawerAdapter;
import beerly.ansteph.beerlybiz.slidingmenu.DrawerItem;
import beerly.ansteph.beerlybiz.slidingmenu.MenuPosition;
import beerly.ansteph.beerlybiz.slidingmenu.SimpleItem;
import beerly.ansteph.beerlybiz.slidingmenu.SpaceItem;
import beerly.ansteph.beerlybiz.view.profile.Account;
import beerly.ansteph.beerlybiz.view.profile.EstablishmentProfile;
import beerly.ansteph.beerlybiz.view.profile.Home;
import beerly.ansteph.beerlybiz.view.registration.Login;

public class PromotionList extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener
,SearchView.OnQueryTextListener{

    private  static String TAG = PromotionList.class.getSimpleName();

    //for the slider
    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slidingRootNav;


    //other variables
    ArrayList<Promotion> mPromotionList;
    ArrayList<Promotion> mFilteredList;
    HashMap<Integer, Promotion> mMappedPromos;

    RecyclerView promoRecyclerView;
    PromotionRecyclerAdapter mPromoAdapter;
    RelativeLayout container;
    public SearchView searchView;

    SessionManager sessionManager;
    GlobalRetainer mGlobalRetainer;

    TextView txtNofound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(getApplicationContext(), EditPromotion.class));
            }
        });

        mGlobalRetainer = (GlobalRetainer) getApplicationContext();
        sessionManager = new SessionManager(getApplicationContext());

        txtNofound = (TextView) findViewById(R.id.txtNoFound);

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
                createItemFor(MenuPosition.POS_PROFILE),
                createItemFor(MenuPosition.POS_PROMOTION).setChecked(true),
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

        adapter.setSelected(MenuPosition.POS_PROMOTION);

        //////////**************************End Related to the drawer menu**************************////////

        container  = (RelativeLayout) findViewById(R.id.containerlyt);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        promoRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mPromotionList =  new ArrayList<>();
        mFilteredList = mPromotionList;
        promoRecyclerView.setLayoutManager(mLayoutManager);
        promoRecyclerView.setItemAnimator(new DefaultItemAnimator());
        promoRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mPromoAdapter= new PromotionRecyclerAdapter(mPromotionList, this);
        promoRecyclerView.setAdapter(mPromoAdapter);

        //search view
        searchView = (SearchView)findViewById(R.id.search);
        searchView.setOnQueryTextListener(this);


        try {
            getPromoData(mGlobalRetainer.get_grEstablishment().getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(promoRecyclerView);


        ItemTouchHelper.SimpleCallback itemTouchHelperCallback1 = new  ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP){

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        // attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(promoRecyclerView);




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
            case MenuPosition.POS_PROFILE:intent = new Intent(getApplicationContext(), EstablishmentProfile.class);break;
            case MenuPosition.POS_PROMOTION:break;
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


    ArrayList<Promotion> setupList()
    {
        ArrayList<Promotion>  promos = new ArrayList<>();

        promos.add(new Promotion (1,2,"Carling Black Label" ,"2018-04-13 16:00:00", 	"2018-04-13 21:00:00","active", 10 	));
        promos.add(new Promotion (2,5,"Carling Blue Label Beer","2018-05-13 18:00:00", 	"2018-05-13 23:00:00","active", 19));
        promos.add(new Promotion (3,4,"Castle Lager","2018-04-13 16:00:00", 	"2018-04-13 21:00:00","active", 10));
        promos.add(new Promotion (4,3,"Castle Lite","2018-04-13 16:00:00", 	"2018-04-13 21:00:00","active", 10));
        promos.add(new Promotion (5,11,"Castle Milk Stout","2018-04-13 16:00:00", 	"2018-04-13 21:00:00","active", 10));
        promos.add(new Promotion (6,11,"Hansa Pilsner","2018-04-13 16:00:00", 	"2018-04-13 21:00:00","active", 10));
        promos.add(new Promotion (7,5,"Pilsner Urquell","2018-04-13 16:00:00", 	"2018-04-13 21:00:00","active", 10));
        promos.add(new Promotion (8,7,"Peroni Nastro Azzurro","2018-04-13 16:00:00", 	"2018-04-13 21:00:00","active", 10));
        promos.add(new Promotion (9,12,"Grolsch","2018-04-13 16:00:00", 	"2018-04-13 21:00:00","active", 10));
        promos.add(new Promotion (10,11,"Redd's","2018-04-13 16:00:00", 	"2018-04-13 21:00:00","active", 10));
        promos.add(new Promotion (11,16,"Brutal Fruit","2018-04-13 16:00:00", 	"2018-04-13 21:00:00","active", 10));
        promos.add(new Promotion (12,5,"Flying Fish","2018-04-13 16:00:00", 	"2018-04-13 21:00:00","active", 10));


        // String duration, String task_date, String start, String end, String project, String description, String realduration, String task_break) {
        return  promos;
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        // get the removed item name to display it in snack bar
        String name = mPromotionList.get(viewHolder.getAdapterPosition()).getTitle();

        // backup of removed item for undo purpose
        final Promotion deletedItem = mPromotionList.get(viewHolder.getAdapterPosition());
        final int deletedIndex = viewHolder.getAdapterPosition();


        // remove the item from recycler view
        mPromoAdapter.removeItem(viewHolder.getAdapterPosition());

       // mPrefCount--;
        //String ct = "(" +(3-mPrefCount)+" more)";
        //txtPrefCount.setText(ct);

        // showing snack bar with Undo option
        Snackbar snackbar = Snackbar
                .make(container, name + " removed from list!", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // undo is selected, restore the deleted item
                mPromoAdapter.restoreItem(deletedItem, deletedIndex);

              //  mPrefCount++;
                //String ct = "(" +(3-mPrefCount)+" more)";
                //txtPrefCount.setText(ct);
            }
        });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();

    }


    //////////**************************Related to the search engine **************************////////

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {

        query = query.toLowerCase();
        mFilteredList = new ArrayList<>();

        for(int i=0; i<mPromotionList.size(); i++)
        {
            final String text = mPromotionList.get(i).getTitle().toLowerCase();
            if(text.contains(query)){
                mFilteredList.add(mPromotionList.get(i));
            }
        }


        promoRecyclerView.setLayoutManager(new LinearLayoutManager(PromotionList.this));
        mPromoAdapter = new PromotionRecyclerAdapter(mFilteredList,PromotionList.this);
        promoRecyclerView.setAdapter(mPromoAdapter);
        mPromoAdapter.notifyDataSetChanged(); //data set changed

        return true;
    }

    //////////**************************End Related to the search engine **************************////////


    private void getPromoData(int est_id) throws JSONException
    {
        String url = String.format(Routes.URL_RETRIEVE_PROMO_EST,String.valueOf(est_id));

        // Log.e(TAG , mUser.getUid());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                loadPromo(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        requestQueue.add(jsonArrayRequest);
    }



    void loadPromo(JSONArray profilejsonArray){

        // txtDisplayname= (TextView) findViewById(R.id.txtDisplayname);
        //   txtDateCreated= (TextView) findViewById(R.id.txtDateCreated);
        //  txtPrefUpdate = (TextView) findViewById(R.id.txtPrefUpdate);

        mPromotionList.clear();
        int est_id=0;

        for(int i = 0; i<profilejsonArray.length(); i++)
        {
            try{
                JSONObject profjson = profilejsonArray.getJSONObject(i);

                Promotion promo= new Promotion();
                promo.setId(profjson.getInt(PromotionColumns.ID));
                promo.setEstablishment_id(profjson.getInt(PromotionColumns.ESTABLISMENT_EST_ID));
                promo.setBeer_id(profjson.getInt(PromotionColumns.BEER_BEERID));
                promo.setTitle(profjson.getString(PromotionColumns.TITLE));
                promo.setStart_date(profjson.getString(PromotionColumns.START_DATE));
                promo.setEnd_date(profjson.getString(PromotionColumns.END_DATE));
                promo.setPrice (profjson.getDouble(PromotionColumns.PRICE));
                promo.setStatus(profjson.getString(PromotionColumns.STATUS));

                // JSONObject beerJson = profjson.getJSONObject("beer");

                Beer be = new Beer();
                be.setId(promo.getBeer_id());
                be.setName(profjson.getString(PromotionColumns.BEER_NAME));
                promo.setBeer(be);

                mPromotionList.add(promo);

                //    txtDisplayname .setText(lovers.getFirst_name() +" "+lovers.getLast_name());
                //    txtDateCreated.setText(lovers.getCreated_at());

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }



        if (profilejsonArray.length()==0)
        {
            Promotion promo= new Promotion();
            promo.setId(0);
            promo.setEstablishment_id(mGlobalRetainer.get_grEstablishment().getId());
            promo.setBeer_id(0);
            promo.setTitle("Presently no special");
            promo.setStart_date("");
            promo.setEnd_date("");
            promo.setPrice (0.0);
            promo.setStatus("");

            Beer be = new Beer();
            be.setId(promo.getBeer_id());
            be.setName("No special");
            promo.setBeer(be);


           // mPromotionsList.add(promo);
        }

        if(mPromotionList.size()>0)
        {
            txtNofound.setVisibility(View.GONE);
        }else{txtNofound.setVisibility(View.VISIBLE);}

        mPromoAdapter.notifyDataSetChanged();

       /* try {
            getBeers(mPromotionsList);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }


    public void deletePromotion(final int id) throws JSONException {
        // final ProgressDialog loading = ProgressDialog.show(getApplicationContext(), "Registering", "Please wait... you will soon be part of the class",false,false);
        //Getting user data

        String url=String.format(Routes.URL_EST_PROMO_DELETE, id );

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //  loading.dismiss();

                try{
                    //creating the Json object from the response
                    JSONObject jsonResponse = new JSONObject(response);
                    String status = jsonResponse.getString("status");
                    String msg = jsonResponse.getString("message");


                    if(status.equals("200"))
                    {
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                    }

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

        );

        //  GlobalRetainer.getInstance().addToRequestQueue();
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }
}


