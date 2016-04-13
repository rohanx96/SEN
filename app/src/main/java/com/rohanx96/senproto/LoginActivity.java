package com.rohanx96.senproto;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.msg91.sendotp.library.internal.Iso2Phone;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_CONTACTS;

public class LoginActivity extends AppCompatActivity {


    MaterialEditText etemail,etpass;
    Button loginButton,newuser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etemail = (MaterialEditText) findViewById(R.id.etemail);
        etpass = (MaterialEditText) findViewById(R.id.etpassword);
        loginButton = (Button) findViewById(R.id.login);
        newuser = (Button) findViewById(R.id.newuser);


        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!validateEmailPassword())return;

                if(isConnectingToInternet()){



                    String email=etemail.getText().toString();
                    String password=etpass.getText().toString();

                    new UserLoginTask(email,password).execute();
                }
                else{
                    SweetAlertDialog d=new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE);
                    d.setCancelable(false);
                    d.setTitleText("No Internet Connection")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    finish();
                                }
                            })
                            .setContentText("Connect to an internet connection and try again.")
                            .show();
                }


            }
        });


        newuser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
                finish();
            }
        });




    }

    private boolean validateEmailPassword() {
        String p=etpass.getText().toString().trim();
        String e=etemail.getText().toString().trim();

        if(p.equals("") || e.equals("") || !isValidEmail(e)){
            if(p.equals(""))etpass.validateWith(new RegexpValidator("This field is required.", "\\d+"));
            if(e.equals(""))etemail.validateWith(new RegexpValidator("This field is required.", "\\d+"));
            if(!isValidEmail(e))etemail.validateWith(new RegexpValidator("Email invalid.", "\\d+"));
            return false;
        }return true;
    }

    private boolean validateEmail() {
        String e=etemail.getText().toString().trim();

        if(e.equals("") || !isValidEmail(e)){
            if(e.equals(""))etemail.validateWith(new RegexpValidator("This field is required.", "\\d+"));
            if(!isValidEmail(e))etemail.validateWith(new RegexpValidator("Email invalid.", "\\d+"));
            return false;
        }return true;
    }


    public boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private SweetAlertDialog pDialog;

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        String mName,mPhone;

        JSONArray jarray;

        UserLoginTask(String email, String password) {
            mEmail = email.trim();
            mPassword = password.trim();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#BF4F3B"));
            pDialog.setTitleText("Logging in...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.


            HttpResponse getresponse = null;
            try {
                HttpClient client = new DefaultHttpClient();
                String URL = "http://rahulkumarwp.pythonanywhere.com/logistics_api/api/userlist/?format=json";
                HttpGet httpget = new HttpGet();
                httpget.setURI(new URI(URL));
                getresponse = client.execute(httpget);

                String get_json_string = EntityUtils.toString(getresponse.getEntity());
                Log.d("LoginTask", getresponse.getStatusLine().getStatusCode() + " " + get_json_string);

                jarray = new JSONArray(get_json_string);

                for(int i=0;i<jarray.length();i++){
                    JSONObject obj=jarray.getJSONObject(i);
                    if(obj.getString("email_id").equals(mEmail) && obj.getString("password").equals(mPassword)){
                        mName=obj.getString("name");
                        mPhone=obj.getString("contact_num");
                        return true;
                    }
                }


            } catch (URISyntaxException | IOException | JSONException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {


            /*if (pDialog.isShowing()){
                pDialog.dismiss();
            }*/



            if(success) {
                /*new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Login Successful")
                                //.setContentText("You clicked the button!")
                        .show();*/

                SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
                editor.putString("email", mEmail);
                editor.putString("password", mPassword);
                editor.putString("name", mName);
                editor.putString("contact", mPhone);
                editor.apply();

                if (pDialog.isShowing()){
                    pDialog
                            .setTitleText("Login Successful")
                            .setContentText("Logged in as "+mName)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    pDialog.dismissWithAnimation();
                                    Intent i=new Intent(LoginActivity.this,MainActivity.class);
                                    startActivity(i);
                                    finishAffinity();
                                }
                            })
                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                }


            }
            else {
                if (pDialog.isShowing()){
                    pDialog
                            .setTitleText("Login Unsuccessful")
                            .setContentText("Something went wrong.")
                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                }

                /*new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Login Unsuccessful")
                                //.setContentText("Something went wrong!")
                        .show();*/
            }
        }

        @Override
        protected void onCancelled() {

        }
    }
}

