package com.example.chris_paterson.onsapp;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends Activity {
    ListView display;
    Button submit;
    TextView lonInput;
    TextView latInput;
    LocationManager locationManager;
    private static final String DEBUG_TAG = "MAIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = (ListView) findViewById(R.id.display);
        submit = (Button) findViewById(R.id.submit);
        lonInput = (TextView) findViewById(R.id.lon);
        latInput = (TextView) findViewById(R.id.lat);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    }

    public void submit(View view) {
         // check to see if they are connected to the network.
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
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

    public void getLocation(View view) {
        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        String lat = String.valueOf(lastLocation.getLatitude());
        String lon = String.valueOf(lastLocation.getLongitude());

        lonInput.setText(lon);
        latInput.setText(lat);

        Log.d("LOCATION_LAT", String.valueOf(lastLocation.getLatitude()));
        Log.d("LOCATION_LON", String.valueOf(lastLocation.getLongitude()));
    }


    private String createUrl(String lat, String lon) {
        String url = "http://data.police.uk/api/crimes-at-location?" +
                "lat=" + lat +
                "&lng=" + lon;
        Log.d(DEBUG_TAG, url);
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
        ArrayList<Crime> crimes = new ArrayList<>();

        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                ArrayList<String> dates = getDates();
                String json = "";
                for(String date : dates) {
                    String result = getData(urls[0] + "&date=" + date);
                    if (!result.equals("[]")) {
                        json += result + "@";
                    }
                }

                return json;
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            } catch (JSONException e) {
                return "Unable to parse JSON.";
            }
        }

        private ArrayList<String> getDates() throws IOException, JSONException {
            ArrayList<String> dates = new ArrayList<>();
            String datesAsJson = getData("https://data.police.uk/api/crimes-street-dates");


            JSONArray jsonArray = new JSONArray(datesAsJson.trim());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                dates.add(obj.getString("date"));
                Log.d(DEBUG_TAG, dates.get(i));
            }

            return dates;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String jsonResults) {
            // Get/set arraylist of crimes for that area
            JSONParser jp = new JSONParser(jsonResults);

            for (Crime crime : jp.getCrimes()) {
                crimes.add(crime);
            }

            displayCrimes(crimes);
            Log.d(DEBUG_TAG, "completed list");
        }

        private void displayCrimes(ArrayList<Crime> crimes) {
            if(crimes.size() > 0) {
                ArrayAdapter<Crime> adapter = new CrimeAdapter(MainActivity.this, R.layout.crime_list, crimes);
                display.setAdapter(adapter);
            } else {
                // TODO Display message saying no crimes in this area.

            }
        }

        private String getData(String myurl) throws IOException {
            InputStream is = null;
            Log.d(DEBUG_TAG, "URL: " + myurl);

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
            reader.close();

            return out.toString();
        }
    }
}
