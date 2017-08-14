package com.example.tomer.agnihotratiming;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    final PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
    String MyPreferences = "Myprefs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
          // to store some personalization variables - run, location, location no.

        SharedPreferences sharedPreferences = getSharedPreferences(MyPreferences, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        TextView tv1 = (TextView) findViewById(R.id.textViewLocation);
        TextView tv2 = (TextView) findViewById(R.id.textViewSunrise);
        TextView tv3 = (TextView) findViewById(R.id.textViewSunset);

        if(sharedPreferences.getInt("Run",0) == 0)
        {  // First Run Preparation
            editor.putInt("Run",1);
            if(editor.commit()==false) {
                Toast.makeText(getApplicationContext(),"Oops ! Issue making changes. ", Toast.LENGTH_SHORT).show();
            }
           // Toast.makeText(getApplicationContext(), " Initializing app for first run ... " , Toast.LENGTH_LONG).show();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            final CharSequence[] items = {
                    "Use Google Places", "Enter Manually", "Do it later"
            };
            alertDialog.setTitle(R.string.title_dialog);
            alertDialog.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        try {

                            startActivityForResult(builder.build(MainActivity.this), 840);
                        } catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException exception) {
                            Toast.makeText(getApplicationContext(), " Oops ! Problem with Google Play Services", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 1:
                        Intent intent = new Intent(getApplicationContext(), ManualEntry.class);
                        startActivityForResult(intent, 420);
                        break;
                    default:
                        break;
                }
                }
                 });
            alertDialog.create();
            alertDialog.show();
            }
        else {
            tv1.setText(sharedPreferences.getString("Location", " Location Not Set "));
            DBhelper dBhelper = new DBhelper(this);
            Date todaydate = Calendar.getInstance().getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            String date1 = simpleDateFormat.format(todaydate);
            Entrydate entrydate = dBhelper.getDate(date1);
            if (entrydate != null) {
                tv2.setText("Sunrise :\n " + entrydate.getSunrise());
                tv3.setText("Sunset: \n " + entrydate.getSunset());
            }
            else {
                tv2.setTextColor(Color.RED);
                tv3.setTextColor(Color.RED);
                tv2.setText("Sunrise :\n " + "Not Found ");
                tv3.setText("Sunset: \n " +  "Not Found ");
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menusettings, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 420) {

            try {
                double lats, longs;
                lats = data.getDoubleExtra("Latitude", 23);
                longs = data.getDoubleExtra("Longitude", 78);
                Log.d(" Raju ", " The lats are " + lats + " " + longs);

                LatLngBounds latLngBounds = new LatLngBounds(new LatLng(lats, longs), new LatLng(lats + 0.1, longs + 0.1));
                builder.setLatLngBounds(latLngBounds);
                startActivityForResult(builder.build(MainActivity.this), 840);
            } catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException exception) {
                Toast.makeText(getApplicationContext(),"Oops ! Issue with Google Play Services ! ", Toast.LENGTH_SHORT);
            }

        } else if (requestCode == 840) {
            Place place = PlacePicker.getPlace(this, data);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            String attributions = (String) place.getAttributions();
            if (attributions == null) {
                attributions = "";
            }
            Log.d(" Raju ", name + "  " + address + " attributions ");
            LatLng latLng = place.getLatLng();
            String adrs = name + " " + address;
            PrepareDB(latLng.latitude, latLng.longitude, adrs);


        }
        else
        {
            Log.d("raju", "REquest code issue");
        }
    }


    public void PrepareDB(Double lats, Double longs, String Address ){

        String MyPreferences = "Myprefs";   // to store some personalization variables - run, location, location no.

        SharedPreferences sharedPreferences = getSharedPreferences(MyPreferences, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Location", Address);
        editor.commit();
        Date todaydate = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String date1 = simpleDateFormat.format(todaydate);

        String querystring = "lat_deg=" +  lats + "&lon_deg=" + longs + "&timeZoneId=Asia%2FKolkata&date=" +  date1 + "&end_date=08%2F24%2F2017";
        Log.d("RAJU", querystring);
        new QuerytoAPI(this).execute(querystring);
    }

}



// Using Google Places in place of Google maps...so following code is temporarily discarded
/*        LatLng latLng = new LatLng(lats, longs);  // Storing Location detains in shared preference using geocoder
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addressList;
        try {
            addressList = geocoder.getFromLocation(lats,longs,1);
            String msg="";
            if(addressList!=null) {
                Log.d("Raju","inside movemap3 adress" );
                String street = addressList.get(0).getAdminArea();
                String city = addressList.get(0).getLocality();
                String count = addressList.get(0).getCountryName();
                String colony = addressList.get(0).getSubLocality();
                msg = msg  + " " + city +  " " + count + " " + street + colony;
                Log.d("RAju", "ADress not null" + msg);
            }
            else{
                Log.d("Raju","adress is null");
            }


        }
        catch (Exception exception)
        {
            Log.d("raju","caught");
        } */



        ///editor.putString("City", city);
        //editor.putString("Locality", city);
        //editor.putString("Country", city);


