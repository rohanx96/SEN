package com.rohanx96.senproto;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.acl.Owner;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.OnClick;
import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * Created by rose on 8/3/16.
 */
public class PlaceOrderActivity  extends AppCompatActivity implements OnConnectionFailedListener{


    //TextView tvdetails1,tvdetails2;
    private GoogleApiClient mGoogleApiClient;
    String TAG="PlacesOrderActivity: ";
    MaterialEditText etweight,etdate,etinfo,etcontact;
    MaterialSpinner itemspinner;
    String postUrl = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);


        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();


        //tvdetails1=(TextView)findViewById(R.id.place_details1);
        //tvdetails2=(TextView)findViewById(R.id.place_details2);

        etweight=(MaterialEditText)findViewById(R.id.etweight);
        etinfo=(MaterialEditText)findViewById(R.id.etinfo);
        etcontact=(MaterialEditText)findViewById(R.id.etcontact);
        itemspinner=(MaterialSpinner)findViewById(R.id.itemspinner);
        etdate=(MaterialEditText)findViewById(R.id.etdate);
        etdate.setKeyListener(null);

        //entries for items' spinner
        List<String> nameslist=new ArrayList<String>();
        nameslist.add("Grains");
        nameslist.add("Cotton");
        nameslist.add("Tiles");
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nameslist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemspinner.setAdapter(adapter);


        PlaceAutocompleteFragment from = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_fragment1);
        from.setHint("From");
        from.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i("TAG", "Place Selected: " + place.getName());

                // Format the returned place's details and display them in the TextView.
                /*tvdetails1.setText(formatPlaceDetails(getResources(), place.getName(), place.getId(),
                        place.getAddress(), place.getPhoneNumber(), place.getWebsiteUri()));

                //Toast.makeText(MainActivity.this, place.getName(), Toast.LENGTH_SHORT).show();
                CharSequence attributions = place.getAttributions();
                if (!TextUtils.isEmpty(attributions)) {
                    tvdetails1.setText(Html.fromHtml(attributions.toString()));
                } else {
                    //tvdetails.setText("Nothing");
                }*/
            }

            @Override
            public void onError(Status status) {
                Log.e("TAG", "onError: Status = " + status.toString());

                Toast.makeText(PlaceOrderActivity.this, "Place selection failed: " + status.getStatusMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });


        PlaceAutocompleteFragment to = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_fragment2);
        to.setHint("To");
        to.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i("TAG", "Place Selected: " + place.getName());

                // Format the returned place's details and display them in the TextView.
                /*tvdetails2.setText(formatPlaceDetails(getResources(), place.getName(), place.getId(),
                        place.getAddress(), place.getPhoneNumber(), place.getWebsiteUri()));

                //Toast.makeText(MainActivity.this, place.getName(), Toast.LENGTH_SHORT).show();
                CharSequence attributions = place.getAttributions();
                if (!TextUtils.isEmpty(attributions)) {
                    tvdetails2.setText(Html.fromHtml(attributions.toString()));
                } else {
                    //tvdetails.setText("Nothing");
                }*/
            }

            @Override
            public void onError(Status status) {
                Log.e("TAG", "onError: Status = " + status.toString());

                Toast.makeText(PlaceOrderActivity.this, "Place selection failed: " + status.getStatusMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });



        Calendar newCalendar = Calendar.getInstance();
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        final DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                etdate.setText(dateFormatter.format(newDate.getTime()));    //=================>date format
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        etdate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    datePickerDialog.show();
                }
            }
        });
        etdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

    }






    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e("TAG", res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(PlaceOrderActivity.this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.place_order_button)
    public void placeOrder(){
        PostHttp http = new PostHttp();
        http.execute(postUrl);
    }

    class PostHttp extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            try {
                url = new URL(params[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            DataOutputStream dStream = null;
            try {
                dStream = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(createJsonString()); //Writes out the string to the underlying output stream as a sequence of bytes
                dStream.flush(); // Flushes the data output stream.
                dStream.close(); // Closing the output stream.
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getApplicationContext(),"Completed Post request",Toast.LENGTH_LONG);
        }
    }

    public String createJsonString(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("weight", etweight.getText());
            jsonObject.accumulate("contact", etcontact.getText());
            jsonObject.accumulate("date", etdate.getText());
            jsonObject.accumulate("info",etinfo.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
