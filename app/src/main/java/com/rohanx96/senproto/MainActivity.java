package com.rohanx96.senproto;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

import android.util.Base64;
import android.util.Log;import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class MainActivity extends AppCompatActivity {

    String name;
    TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        login=(TextView)findViewById(R.id.login);

        CardView myOrders = (CardView) findViewById(R.id.myOrders);


        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        name = prefs.getString("name", "");
        if(!(name.equals(""))){
            Log.d("SharedPref",name);
            login.setText("LOGOUT");
            Snackbar snackbar = Snackbar.make(findViewById(R.id.mainsv), "Logged in as " + name, Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        myOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!name.equals("")){
                    Intent i=new Intent(MainActivity.this,MyOrders.class);
                    startActivity(i);
                }
                else{
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.mainsv), "You need to Log In to see your orders.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }


            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.track_order_container)
    public void enterTripID(View view){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        View v=LayoutInflater.from(this).inflate(R.layout.trip_id_dialog, null);
        alertDialog.setCustomTitle(LayoutInflater.from(this).inflate(R.layout.trip_id_dialog_heading, null));
        alertDialog.setView(v);
        alertDialog.create().show();

        final EditText ettripid=(EditText)v.findViewById(R.id.ettripid);
        Button track= (Button) v.findViewById(R.id.track);
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnectingToInternet()){
                    if (!ettripid.getText().toString().equals("")) {
                        Intent i=new Intent(MainActivity.this,TrackOrderActivity.class);
                        i.putExtra("tripid",ettripid.getText().toString().trim());
                        startActivity(i);
                    }
                }
                else{
                    SweetAlertDialog d=new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE);
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

    @OnClick(R.id.place_order_container)
    public void placeOrder(){
        if(!name.equals("")){
            Intent intent = new Intent(this,PlaceOrderActivity.class);
            startActivity(intent);
        }
        else{
            Snackbar snackbar = Snackbar.make(findViewById(R.id.mainsv), "You need to Log In to place an order.", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    @OnClick(R.id.login)
    public void login(){
        if(login.getText().toString().equals("LOGIN")){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }
        else if(login.getText().toString().equals("LOGOUT")){
            SweetAlertDialog pDialogue=new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
            pDialogue.setTitleText("Log Out?")
                    .setContentText("Are you sure you want to log out?")
                    .setConfirmText("Log out")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
                            editor.putString("email", "");
                            editor.putString("password", "");
                            editor.putString("name", "");
                            editor.putString("contact", "");
                            editor.apply();
                            name="";
                            login.setText("LOGIN");
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();


        }
    }

    @OnClick(R.id.contactUs)
    public void contactUs(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        View v=LayoutInflater.from(this).inflate(R.layout.contact_us_dialogue, null);
        alertDialog.setCustomTitle(LayoutInflater.from(this).inflate(R.layout.contact_us_heading, null));
        alertDialog.setView(v);
        alertDialog.create().show();

        CardView cardemail= (CardView) v.findViewById(R.id.cardemail);
        CardView cardphone= (CardView) v.findViewById(R.id.cardphone);

        cardemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"jatinpatel845@gmail.com"});

                try {
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cardphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                        "tel", "9426898134", null));
                startActivity(phoneIntent);
            }
        });


    }

    @OnClick(R.id.faq)
    public void faq(){
        Intent i=new Intent(MainActivity.this,FaqActivity.class);
        startActivity(i);
    }


}
