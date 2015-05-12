package com.example.chris_paterson.onsapp;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends Activity {
    public final String API_KEY = "xBAPV3FSrr";
    ListView display;
    Button submit;
    TextView lonInput;
    TextView latInput;
    double locLon;
    double locLat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = (ListView) findViewById(R.id.display);
        submit = (Button) findViewById(R.id.submit);
        lonInput = (TextView) findViewById(R.id.lon);
        latInput = (TextView) findViewById(R.id.lat);

        // TODO: Stuff for testing, delete.
        lonInput.setText("-1.131592");
        latInput.setText("52.629729");
    }

    public void submit(View view) {
//        setLocation();

        // check to see if they are connected to the network.
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // TODO Get the location via GPS.
            String latLocation = latInput.getText().toString();
            String lonLocation = lonInput.getText().toString();

            // Get the URL.
            String url = createUrl(latLocation, lonLocation);

            // Get the data.
            sendRequest(url);
        } else {
            Toast.makeText(this, "No network connection.", Toast.LENGTH_SHORT);
        }
    }

    private void setLocation() {
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(null != location) {
            locLon = location.getLongitude();
            locLat = location.getLatitude();

            lonInput.setText(location.getLongitude() + "");
            latInput.setText(location.getLatitude() + "");
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
                "&lng=" + lng +
                "&date=2015-03";

        url =
                "https://data.police.uk/api/crimes-street/all-crime?lat=51.5833&lng=-3.0000&date=2015-03";
        return url;
    }

    private void sendRequest(String url) {
        new AsyncGet().execute(url);
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


    /**
     * Gets the data from the police API and populate listview on complete.
     */
    public class AsyncGet extends AsyncTask<String, Void, String> {
        private final String DEBUG_TAG = "AsyncGet";

        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return getData(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String jsonResult) {
            // Get/set arraylist of crimes for that area
            JSONParser jp = new JSONParser(jsonResult);
            ArrayList<Crime> crimes = jp.getCrimes();

            displayCrimes(crimes);
            Log.d(DEBUG_TAG, "completed list");
        }

        private void displayCrimes(ArrayList<Crime> crimes) {
            ArrayAdapter<Crime> adapter = new CrimeAdapter(MainActivity.this, R.layout.crime_list, crimes);
            display.setAdapter(adapter);
        }

        private String getData(String myurl) throws IOException {
            InputStream is = null;

            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(DEBUG_TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is);
            return contentAsString;
        }

        public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            System.out.println(out.toString());   //Prints the string content read from input stream
            reader.close();

            return out.toString();
        }
    }
}
