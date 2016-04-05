package com.rohanx96.senproto;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import android.widget.Button;
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
import com.rengwuxian.materialedittext.validation.RegexpValidator;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
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
    Button placeOrderButton;

    String source=null,destination=null;

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
        placeOrderButton=(Button)findViewById(R.id.place_order_button);

        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateSource() && validateDestination() && validateItem() && validateWeight() && validateContact() ){
                    PostOrder post = new PostOrder();
                    post.execute();
                }
            }
        });

        //entries for items' spinner
        List<String> nameslist=new ArrayList<String>();
        nameslist.add("Grains");
        nameslist.add("Cotton");
        nameslist.add("Tiles");
        nameslist.add("Drinks");
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

                source = place.getName().toString();
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

                destination = place.getName().toString();

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

    private boolean validateWeight() {
        String n=etweight.getText().toString().trim();
        if(n.equals("")){
            etweight.validateWith(new RegexpValidator("Enter weight of goods to be delivered.", "\\d+"));
            etweight.clearFocus();
            etweight.requestFocus();
            etweight.beginBatchEdit();
            return false;
        }return true;
    }

    private boolean validateContact() {
        String n=etcontact.getText().toString().trim();
        if(n.equals("")){
            etcontact.validateWith(new RegexpValidator("Enter contact number.", "\\d+"));
            etcontact.clearFocus();
            etcontact.requestFocus();
            etcontact.beginBatchEdit();
            return false;
        }return true;
    }



    private boolean validateItem() {
        String build=itemspinner.getSelectedItem().toString().trim();
        if(build.equals(itemspinner.getHint().toString())){
            itemspinner.setError("Select type of goods to be delivered.");
            itemspinner.requestFocus();
            return false;
        }return true;
    }

    private boolean validateSource() {
        if(source==null){
            Toast.makeText(PlaceOrderActivity.this, "Please select a source place.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateDestination() {
        if(destination==null){
            Toast.makeText(PlaceOrderActivity.this, "Please select a destination place.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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


    private ProgressDialog pDialog;
    int count=4;

    class PostOrder extends AsyncTask<Void, Void, Void> {

        String goods,contact,weight;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(PlaceOrderActivity.this);
            pDialog.setMessage("Posting Order...");
            pDialog.setCancelable(false);
            pDialog.show();

            goods=itemspinner.getSelectedItem().toString();
            weight=etweight.getText().toString();
            contact=etcontact.getText().toString();
        }
        @Override
        protected Void doInBackground(Void... params) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://rahulkumarwp.pythonanywhere.com/logistics_api/api/orderslist/?format=json");

            try {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("order_id", String.valueOf(count++)));
                nameValuePairs.add(new BasicNameValuePair("goods_type", goods));
                nameValuePairs.add(new BasicNameValuePair("order_status", "pending"));
                nameValuePairs.add(new BasicNameValuePair("quantity", weight));
                nameValuePairs.add(new BasicNameValuePair("source", source));
                nameValuePairs.add(new BasicNameValuePair("destination", destination));
                nameValuePairs.add(new BasicNameValuePair("contact_num", contact));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                String json_string = EntityUtils.toString(response.getEntity());
                Log.d("PostOrder", json_string);

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }



            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
        }

    }



}
