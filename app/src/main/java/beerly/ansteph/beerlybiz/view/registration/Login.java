package beerly.ansteph.beerlybiz.view.registration;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import beerly.ansteph.beerlybiz.R;
import beerly.ansteph.beerlybiz.api.Routes;
import beerly.ansteph.beerlybiz.api.columns.BeerColumns;
import beerly.ansteph.beerlybiz.api.columns.EstablishmentColumns;
import beerly.ansteph.beerlybiz.api.columns.UserColumns;
import beerly.ansteph.beerlybiz.app.GlobalRetainer;
import beerly.ansteph.beerlybiz.helper.SessionManager;
import beerly.ansteph.beerlybiz.model.Beer;
import beerly.ansteph.beerlybiz.model.Establishment;
import beerly.ansteph.beerlybiz.model.User;
import beerly.ansteph.beerlybiz.view.profile.Home;

public class Login extends AppCompatActivity {

    EditText edtEmail, edtPassword;

    SessionManager sessionManager;
    GlobalRetainer mGlobalRetainer;

    String mEmail, mPassword;

    CheckBox chkSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mGlobalRetainer = (GlobalRetainer) getApplicationContext();
        sessionManager = new SessionManager(getApplicationContext());

        edtEmail = (EditText) findViewById(R.id.editEmail) ;
        edtPassword = (EditText) findViewById(R.id.editPass) ;

        chkSession = (CheckBox) findViewById(R.id.chkSession);

    }


    public  void onLoginClicked(View view){

        String email = edtEmail.getText().toString();
        String pwd =    edtPassword.getText().toString();

        if(!email.isEmpty()&& !pwd.isEmpty()) {
            try {
                getEstablishmentData(email, pwd);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



        //
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
                        mEmail = email; mPassword=password;

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
            if(chkSession.isChecked())
                sessionManager.createLoginSession(String.valueOf(est.getId()),mEmail,mEmail,mPassword);

            mGlobalRetainer.set_grEstablishment(est);
            startActivity(new Intent(getApplicationContext(), Home.class));
        }




    }





}
