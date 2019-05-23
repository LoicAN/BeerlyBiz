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
import android.widget.EditText;
import android.widget.TextView;

import com.github.thunder413.datetimeutils.DateTimeStyle;
import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;

import beerly.ansteph.beerlybiz.R;
import beerly.ansteph.beerlybiz.app.GlobalRetainer;
import beerly.ansteph.beerlybiz.helper.SessionManager;
import beerly.ansteph.beerlybiz.model.Establishment;
import beerly.ansteph.beerlybiz.model.User;
import beerly.ansteph.beerlybiz.slidingmenu.DrawerAdapter;
import beerly.ansteph.beerlybiz.slidingmenu.DrawerItem;
import beerly.ansteph.beerlybiz.slidingmenu.MenuPosition;
import beerly.ansteph.beerlybiz.slidingmenu.SimpleItem;
import beerly.ansteph.beerlybiz.slidingmenu.SpaceItem;
import beerly.ansteph.beerlybiz.view.promotion.PromotionList;
import beerly.ansteph.beerlybiz.view.registration.Login;

public class Account extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener{
    private  static String TAG = Account.class.getSimpleName();


    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slidingRootNav;

    SessionManager sessionManager;
    GlobalRetainer mGlobalRetainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



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
                createItemFor(MenuPosition.POS_PROMOTION),
                createItemFor(MenuPosition.POS_ACCOUNT).setChecked(true),

                //   createItemFor(MenuPosition.POS_AFFILIATE),
                new SpaceItem(48),
                createItemFor(MenuPosition.POS_LOGOUT)));
        adapter.setListener(this);


        RecyclerView list = (RecyclerView) findViewById(R.id.list);

        // RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setSelected(MenuPosition.POS_ACCOUNT);

        //////////**************************End Related to the drawer menu**************************////////

        mGlobalRetainer = (GlobalRetainer) getApplicationContext();
        sessionManager = new SessionManager(getApplicationContext());

        if(mGlobalRetainer.get_grUser()!=null)
        {
            fillFields(mGlobalRetainer.get_grUser());
        }else{

           /* try {
                getEstablismentData();
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
        }
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
            case MenuPosition.POS_PROMOTION:intent = new Intent(getApplicationContext(), PromotionList.class);break;
            case MenuPosition.POS_ACCOUNT:break;


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



    void fillFields(User user)
    {
        //filling the corresponding value


        ((EditText)findViewById(R.id.txtuserFName)).setText(user.getFirstName());

        ((EditText)findViewById(R.id.txtuserLname)).setText(user.getLastName());
        ((EditText)findViewById(R.id.txtusername)).setText(user.getUsername());



        ((EditText)findViewById(R.id.txtuserContact)).setText(user.getContactNumber());



        String joinDate = "";
        try{
            joinDate=  DateTimeUtils.formatWithStyle(user.getCreated_at(), DateTimeStyle.MEDIUM);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        ((EditText)findViewById(R.id.txtuserJoin)).setText(joinDate);



    }

    public void onDoneClicked(View view){
        startActivity(new Intent(getApplicationContext(),Home.class));
    }

}
