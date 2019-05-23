package beerly.ansteph.beerlybiz.view.promotion;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.github.thunder413.datetimeutils.DateTimeUnits;
import com.github.thunder413.datetimeutils.DateTimeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import beerly.ansteph.beerlybiz.R;
import beerly.ansteph.beerlybiz.adapter.BeerArrayListAdapter;
import beerly.ansteph.beerlybiz.adapter.SimpleArrayListAdapter;
import beerly.ansteph.beerlybiz.api.Routes;
import beerly.ansteph.beerlybiz.api.columns.BeerColumns;
import beerly.ansteph.beerlybiz.api.columns.EstablishmentColumns;
import beerly.ansteph.beerlybiz.api.columns.PromotionColumns;
import beerly.ansteph.beerlybiz.app.GlobalRetainer;
import beerly.ansteph.beerlybiz.helper.SessionManager;
import beerly.ansteph.beerlybiz.model.Beer;
import beerly.ansteph.beerlybiz.model.Promotion;

import beerly.ansteph.beerlybiz.view.promotion.datetimepicker.EndDatePickerFragment;
import beerly.ansteph.beerlybiz.view.promotion.datetimepicker.EndTimePickerFragment;
import beerly.ansteph.beerlybiz.view.promotion.datetimepicker.StartDatePickerFragment;
import beerly.ansteph.beerlybiz.view.promotion.datetimepicker.StartTimePickerFragment;
import gr.escsoft.michaelprimez.searchablespinner.SearchableSpinner;
import gr.escsoft.michaelprimez.searchablespinner.interfaces.IStatusListener;
import gr.escsoft.michaelprimez.searchablespinner.interfaces.OnItemSelectedListener;

public class EditPromotion extends AppCompatActivity {

    Promotion mEditedPromo;
    EditText edtPromoTitle;

    TextView  txtStartDate, txtStartTime, txtEndDate, txtEndTime;
    EditText edtTitle, edtPrice;
    Spinner spnStatus;
    ArrayAdapter<CharSequence> statusAdapter;
    ArrayAdapter<CharSequence> cityAdapter;

    //testing to be replace with beer
    private SearchableSpinner mSearchableSpinner;
    private SimpleArrayListAdapter mSimpleArrayListAdapter;
    private BeerArrayListAdapter   mBeerArrayListAdapter;
    private final ArrayList<String> mStrings = new ArrayList<>();

    ArrayList<Beer> mBeersList ;

    SessionManager sessionManager;
    GlobalRetainer mGlobalRetainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_promotion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mGlobalRetainer = (GlobalRetainer) getApplicationContext();
        sessionManager = new SessionManager(getApplicationContext());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEditedPromo = null;

        edtPromoTitle = (EditText) findViewById(R.id.editPromTitle) ;

        txtStartDate= (TextView) findViewById(R.id.txtstartdateday);
        txtStartTime= (TextView) findViewById(R.id.txtstartdatetime);
        txtEndDate= (TextView) findViewById(R.id.txtenddateday);
        txtEndTime= (TextView) findViewById(R.id.txtenddatetime);

        edtTitle=(EditText)findViewById(R.id.editPromTitle);
        edtPrice=(EditText)findViewById(R.id.editPrice);


        ((TextView)findViewById(R.id.txtEst_name)).setText(mGlobalRetainer.get_grEstablishment().getName());
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
        {
            mEditedPromo = (Promotion) bundle.getSerializable("promo");
            edtPromoTitle.setText(mEditedPromo.getTitle());
            fillFields();
        }else
        {
            mEditedPromo= new Promotion();
            mEditedPromo.setEstablishment_id(mGlobalRetainer.get_grEstablishment().getId());
            prepareDateFields();
            setTitle("Add new special");


        }

        spnStatus  =(Spinner)findViewById(R.id.spnStatus);
        statusAdapter = ArrayAdapter.createFromResource(this,R.array.status, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnStatus.setAdapter(statusAdapter);



        mBeersList = new ArrayList<Beer>();

        try {
            getBeerData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

       // initListValues();

        mSimpleArrayListAdapter = new SimpleArrayListAdapter(this, mStrings);

        mBeerArrayListAdapter = new BeerArrayListAdapter(this, mBeersList);

        mSearchableSpinner = (SearchableSpinner) findViewById(R.id.SearchableSpinner);
       //
        //
       // mSearchableSpinner.setAdapter(mSimpleArrayListAdapter);
        mSearchableSpinner.setAdapter(mBeerArrayListAdapter);
        mSearchableSpinner.setOnItemSelectedListener(mOnItemSelectedListener);
        mSearchableSpinner.setStatusListener(new IStatusListener() {
            @Override
            public void spinnerIsOpening() {
               // mSearchableSpinner1.hideEdit();
                //mSearchableSpinner2.hideEdit();
            }

            @Override
            public void spinnerIsClosing() {

            }
        });

    }


    public void onDoneClicked(View view){
       if(prepareSave()) {

           try {
               storePromotion();
           } catch (JSONException e) {
               e.printStackTrace();
           }
       }


    }

    void prepareDateFields(){
        DateTimeUtils.setTimeZone("CAT");


        Date date = Calendar.getInstance(TimeZone.getTimeZone("Harare")).getTime();

        String startDate = DateTimeUtils.formatWithPattern(date,"yyyy-MM-dd");
        String startTime = DateTimeUtils.formatTime(date);
        String endDate = DateTimeUtils.formatWithPattern(date,"yyyy-MM-dd");

        String endTime = DateTimeUtils.formatTime(date);;

        txtStartDate.setText(startDate);
        txtStartTime.setText(startTime);
        txtEndDate.setText(endDate);
        txtEndTime.setText(endTime);

    }


    void fillFields()
    {
        //filling the corresponding value




        ((EditText)findViewById(R.id.editPromTitle)).setText(mEditedPromo.getTitle());
        ((EditText)findViewById(R.id.editPrice)).setText(String.valueOf(mEditedPromo.getPrice()));

        String startDate = DateTimeUtils.formatWithPattern(mEditedPromo.getStart_date(),"yyyy-MM-dd");
        String startTime = DateTimeUtils.formatTime(mEditedPromo.getStart_date());
        String endDate = DateTimeUtils.formatWithPattern(mEditedPromo.getEnd_date(),"yyyy-MM-dd");

        String endTime = DateTimeUtils.formatTime(mEditedPromo.getEnd_date());



        txtStartDate.setText(startDate);
        txtStartTime.setText(startTime);
        txtEndDate.setText(endDate);
        txtEndTime.setText(endTime);

    }


    boolean prepareSave(){

        //reset error
        edtTitle.setError(null);
        edtPrice.setError(null);

        String title = edtTitle.getText().toString();
        String price = edtPrice.getText().toString();

        String startDate = txtStartDate.getText().toString() +" "+ txtStartTime.getText().toString();
        String endDate = txtEndDate.getText().toString() +" "+ txtEndTime.getText().toString();
        String status = spnStatus.getSelectedItem().toString();

        Date dateStart = DateTimeUtils.formatDate(startDate);
        Date dateEnd = DateTimeUtils.formatDate(endDate);

        boolean isCorrect = true;

        View focusView = null;

        int pos =mSearchableSpinner.getSelectedPosition();

        if(TextUtils.isEmpty(title)){
            edtTitle.setError(getString(R.string.error_field_required));
            focusView = edtTitle;
            isCorrect = false;
        }
        else if (TextUtils.isEmpty(price) ) {
            edtPrice.setError(getString(R.string.error_field_required));
            focusView = edtPrice;
            isCorrect = false;

        }else if(pos<=0)
        {
            isCorrect=false;
            Toast.makeText(this,"Please select a liquor", Toast.LENGTH_LONG).show();
        }
        else if(!isDateDiffPositive(dateEnd,dateStart)){
            isCorrect = false;
            Toast.makeText(this,"The start date can't be after the end date", Toast.LENGTH_LONG).show();
        }else if(!isTimeDiffPositive(dateEnd ,dateStart)){
            isCorrect = false;
            Toast.makeText(this,"The start time can't be after the end time on the same day", Toast.LENGTH_LONG).show();
         }


        if(isCorrect){
            mEditedPromo.setBeer(mBeerArrayListAdapter.getItem(mSearchableSpinner.getSelectedPosition()));
            mEditedPromo.setEnd_date(endDate);
            mEditedPromo.setStart_date(startDate);
            mEditedPromo.setStatus(status);
            mEditedPromo.setTitle(title);
            mEditedPromo.setPrice(Double.parseDouble(price));
        }


       return isCorrect;

      //  mEditedPromo.set
    }




    public boolean isDateDiffPositive(Date date1, Date date2)
    {
       if(DateTimeUtils.getDateDiff(date1,date2, DateTimeUnits.DAYS)>=0)
           return true;

       return false;
    }

    public boolean isTimeDiffPositive(Date date1, Date date2)
    {
        if(DateTimeUtils.getDateDiff(date1,date2, DateTimeUnits.MILLISECONDS)>=0)
            return true;

        return false;
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mSearchableSpinner.isInsideSearchEditText(event)) {
            mSearchableSpinner.hideEdit();
        }

        return super.onTouchEvent(event);
    }

    private OnItemSelectedListener mOnItemSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(View view, int position, long id) {
          //  Toast.makeText(EditPromotion.this, "Item on position " + position + " : " + mBeerArrayListAdapter.getItem(position).getName() + " Selected", Toast.LENGTH_SHORT).show();
                mEditedPromo.setBeer(mBeerArrayListAdapter.getItem(position));
        }

        @Override
        public void onNothingSelected() {
            Toast.makeText(EditPromotion.this, "Nothing Selected", Toast.LENGTH_SHORT).show();
        }
    };


    private void initListValues() {
        mStrings.add("Brigida Kurz");
        mStrings.add("Tracy Mckim");
        mStrings.add("Iesha Davids");
        mStrings.add("Ozella Provenza");
        mStrings.add("Florentina Carriere");
        mStrings.add("Geri Eiler");
        mStrings.add("Tammara Belgrave");
        mStrings.add("Ashton Ridinger");
        mStrings.add("Jodee Dawkins");
        mStrings.add("Florine Cruzan");
        mStrings.add("Latia Stead");
        mStrings.add("Kai Urbain");
        mStrings.add("Liza Chi");
        mStrings.add("Clayton Laprade");
        mStrings.add("Wilfredo Mooney");
        mStrings.add("Roseline Cain");
        mStrings.add("Chadwick Gauna");
        mStrings.add("Carmela Bourn");
        mStrings.add("Valeri Dedios");
        mStrings.add("Calista Mcneese");
        mStrings.add("Willard Cuccia");
        mStrings.add("Ngan Blakey");
        mStrings.add("Reina Medlen");
        mStrings.add("Fabian Steenbergen");
        mStrings.add("Edmond Pine");
        mStrings.add("Teri Quesada");
        mStrings.add("Vernetta Fulgham");
        mStrings.add("Winnifred Kiefer");
        mStrings.add("Chiquita Lichty");
        mStrings.add("Elna Stiltner");
        mStrings.add("Carly Landon");
        mStrings.add("Hans Morford");
        mStrings.add("Shawanna Kapoor");
        mStrings.add("Thomasina Naron");
        mStrings.add("Melba Massi");
        mStrings.add("Sal Mangano");
        mStrings.add("Mika Weitzel");
        mStrings.add("Phylis France");
        mStrings.add("Illa Winzer");
        mStrings.add("Kristofer Boyden");
        mStrings.add("Idalia Cryan");
        mStrings.add("Jenni Sousa");
        mStrings.add("Eda Forkey");
        mStrings.add("Birgit Rispoli");
        mStrings.add("Janiece Mcguffey");
        mStrings.add("Barton Busick");
        mStrings.add("Gerald Westerman");
        mStrings.add("Shalanda Baran");
        mStrings.add("Margherita Pazos");
        mStrings.add("Yuk Fitts");
    }

    public void onStartDateClicked(View view)
    {
        DialogFragment nf = new StartDatePickerFragment();
        nf.show(getSupportFragmentManager(), "Start Date");
    }

    public void onStartTimeClicked(View view)
    {
        DialogFragment nf = new StartTimePickerFragment();
        nf.show(getSupportFragmentManager(),"Start Time");
    }


    public void onEndDateClicked(View view)
    {
        DialogFragment nf = new EndDatePickerFragment();
        nf.show(getSupportFragmentManager(), "End Date");
    }

    public void onEndTimeClicked(View view)
    {
        DialogFragment nf = new EndTimePickerFragment();
        nf.show(getSupportFragmentManager(),"End Time");
    }



    private void getBeerData() throws JSONException
    {
        String url = String.format(Routes.URL_RETRIEVE_BEERS);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                loadBeer(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        requestQueue.add(jsonArrayRequest);
    }



    private void loadBeer(JSONArray beerjsonArray)
    {
        ArrayList<Beer> beers = new ArrayList<>();
        mBeersList.clear();
        mStrings.clear();
        for(int i = 0; i<beerjsonArray.length(); i++)
        {
            try{
                JSONObject itemjson = beerjsonArray.getJSONObject(i);

                Beer br = new Beer();
                br.setId(itemjson.getInt(BeerColumns.BEERID));
                br.setName(itemjson.getString(BeerColumns.NAME));
                br.setVendor(itemjson.getString(BeerColumns.VENDOR));
                br.setDescription(itemjson.getString(BeerColumns.DESCRIPTION));
                br.setPercentage(itemjson.getDouble(BeerColumns.PERCENTAGE));

                // est.set(estjson.getString("hs_license"));
                // est.setName(estjson.getString(""));

                mBeersList.add(br);

                mStrings.add(br.getName());

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }


            // mBeersList =beers;
        mBeerArrayListAdapter.notifyDataSetChanged();
        mSimpleArrayListAdapter.notifyDataSetChanged();

        if(mEditedPromo.getBeer_id()>0){

            int id =mEditedPromo.getBeer_id();

            int pos = getLiquorItemPos(id);

          //  mSearchableSpinner.setSelectedItem(getLiquorItemPos());

            mSearchableSpinner.setSelectedItem(pos);
        }

      //  mBeerAdapter.notifyDataSetChanged();

        //initViewPager(establishments);
        //initMarker(establishments);
    }

    public void storePromotion() throws JSONException {
        // final ProgressDialog loading = ProgressDialog.show(getApplicationContext(), "Registering", "Please wait... you will soon be part of the class",false,false);
        //Getting user data

        String url="";

        if(mEditedPromo.getId()==0){
            url = String.format(Routes.URL_EST_STORE_PROMO);
        }else{
            url = String.format(Routes.URL_EST_PROMO_UPDATE, mEditedPromo.getId());
        }


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
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
                        JSONObject promotion = jsonResponse.getJSONObject("promotion");
                        Toast.makeText(getApplicationContext(), promotion.getString("title")+" "+msg, Toast.LENGTH_LONG).show();

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

        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();


               // params.put(PromotionColumns.ESTABLISHMENT_NAME,mEditedPromo.getEstablishment_id() );

                params.put(PromotionColumns.TITLE,mEditedPromo.getTitle() );
                params.put(PromotionColumns.START_DATE,mEditedPromo.getStart_date() );
                params.put(PromotionColumns.END_DATE,mEditedPromo.getEnd_date() );
                params.put(PromotionColumns.STATUS,mEditedPromo.getStatus() );
                params.put(PromotionColumns.PRICE,String.valueOf(mEditedPromo.getPrice()) );
                params.put(PromotionColumns.ESTABLISMENT_EST_ID,String.valueOf(mEditedPromo.getEstablishment_id()) );
                params.put(PromotionColumns.BEER_BEERID,String.valueOf(mEditedPromo.getBeer().getId()) );


                return params;
            }
        };


        //  GlobalRetainer.getInstance().addToRequestQueue();
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }




    int getLiquorItemPos(int id)
    {

       for(int i =0; i < mBeersList.size();i++)
       {

         if(id==mBeersList.get(i).getId()){

           return i;
         }
       }

        return 0;
    }



}
