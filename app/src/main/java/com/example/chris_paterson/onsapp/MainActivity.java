package com.example.chris_paterson.onsapp;

import android.app.Activity;
import android.content.Context;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends Activity {
    public final String API_KEY = "xBAPV3FSrr";
    ListView display;
    Button submit;
    TextView lon;
    TextView lat;
    ArrayList<Crime> crimes;
    double locLon;
    double locLat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = (ListView) findViewById(R.id.display);
        submit = (Button) findViewById(R.id.submit);
        lon = (TextView) findViewById(R.id.lon);
        lat = (TextView) findViewById(R.id.lat);
    }

    public void submit(View view) {
        setLocation();
        /*
        // check to see if they are connected to the network.
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // TODO Get the location via GPS.
            String latLocation = lat.getText().toString();
            String lonLocation = lon.getText().toString();

            // Get the URL.
            String url = createUrl(latLocation, lonLocation);

            // Get the data.
            String jsonResult = sendRequest(url);

            // Get/set arraylist of crimes for that area
            JSONParser jp = new JSONParser(jsonResult);
            this.crimes = jp.getCrimes();

            for (Crime c : crimes) {
                Log.d("outcome", c.getOutcome());
            }

            ArrayAdapter<Crime> adapter = new CrimeAdapter(MainActivity.this, R.layout.crime_list, crimes);
            display.setAdapter(adapter);

        } else {
            Toast.makeText(this, "No network connection.", Toast.LENGTH_SHORT);
        }*/


    }

    private void setLocation() {
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(null != location) {
            locLon = location.getLongitude();
            locLat = location.getLatitude();

            lon.setText(location.getLongitude() + "");
            lat.setText(location.getLatitude() + "");
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            locLon = location.getLongitude();
            locLat = location.getLatitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    private String createUrl(String lat, String lng) {
        String url = "https://data.police.uk/api/crimes-at-location?" +
                "&lat=" + lat +
                "&lng=" + lng;

        return url;
    }

    private String sendRequest(String url) {
        RequestHelper rh = new RequestHelper(url);
        return rh.getResult();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
}
