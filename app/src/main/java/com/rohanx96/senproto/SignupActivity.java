package com.rohanx96.senproto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
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

public class SignupActivity extends AppCompatActivity {

    MaterialEditText etname,etemail,etphone,etpass;
    Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etname = (MaterialEditText) findViewById(R.id.etname);
        etemail = (MaterialEditText) findViewById(R.id.etemail);
        etphone = (MaterialEditText) findViewById(R.id.etphone);
        etpass = (MaterialEditText) findViewById(R.id.etpassword);
        signupButton = (Button) findViewById(R.id.signup);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validateFields()) return;
                if(isConnectingToInternet()){



                    String name = etname.getText().toString();
                    String email = etemail.getText().toString();
                    String phone = etphone.getText().toString();
                    String password = etpass.getText().toString();

                    new SignupTask(name, email, phone, password).execute();
                }
                else{
                    SweetAlertDialog d=new SweetAlertDialog(SignupActivity.this, SweetAlertDialog.ERROR_TYPE);
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


    private boolean validateFields() {
        String n=etname.getText().toString().trim();
        String c=etphone.getText().toString().trim();
        String p=etpass.getText().toString().trim();
        String e=etemail.getText().toString().trim();

        if(n.equals("") || c.equals("") || p.equals("") || e.equals("") || !isValidEmail(e)){
            if(n.equals(""))etname.validateWith(new RegexpValidator("This field is required.", "\\d+"));
            if(c.equals(""))etphone.validateWith(new RegexpValidator("This field is required.", "\\d+"));
            if(p.equals(""))etpass.validateWith(new RegexpValidator("This field is required.", "\\d+"));
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



    private SweetAlertDialog pDialog;

    public class SignupTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final String mName;
        private final String mPhone;

        JSONArray jarray;
        int statusCode;

        SignupTask(String name,String email, String phone, String password) {
            mName=name.trim();
            mEmail = email.trim();
            mPhone=phone.trim();
            mPassword = password.trim();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new SweetAlertDialog(SignupActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#BF4F3B"));
            pDialog.setTitleText("Signing up...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://rahulkumarwp.pythonanywhere.com/logistics_api/api/userlist/?format=json");

            try {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("contact_num", mPhone));
                nameValuePairs.add(new BasicNameValuePair("email_id", mEmail));
                nameValuePairs.add(new BasicNameValuePair("name", mName));
                nameValuePairs.add(new BasicNameValuePair("password", mPassword));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                String json_string = EntityUtils.toString(response.getEntity());
                statusCode=response.getStatusLine().getStatusCode();
                Log.d("SignupTask", statusCode+" "+json_string);

            } catch (IOException e) {
                // TODO Auto-generated catch block
            }


            if(statusCode==201)return true;
            else return false;

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            /*if (pDialog.isShowing())
                pDialog.dismiss();*/

            if(success) {
                SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
                editor.putString("email", mEmail);
                editor.putString("password", mPassword);
                editor.putString("name", mName);
                editor.putString("contact", mPhone);
                editor.apply();

                if (pDialog.isShowing()){
                    pDialog
                            .setTitleText("Signup Successful")
                            .setContentText("Welcome "+mName)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    pDialog.dismissWithAnimation();
                                    Intent i=new Intent(SignupActivity.this,MainActivity.class);
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
                            .setTitleText("Signup Unsuccessful")
                            .setContentText("Something went wrong.")
                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                }
            }
        }

        @Override
        protected void onCancelled() {

        }
    }



}
