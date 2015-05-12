package com.example.chris_paterson.onsapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONParser {
    private final String json;
    ArrayList<Crime> crimes;
    private static final String DEBUG_TAG = "JSONParser";

    public JSONParser(String json) {
        this.json = json;
        crimes = new ArrayList<>();

        try {
            doParse();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Crime> getCrimes() {
        return crimes;
    }

    private void doParse() throws JSONException {
        JSONArray jsonArray = new JSONArray(json.trim());

        for (int i = 0; i < jsonArray.length(); i++) {
            Crime crime = new Crime();
            JSONObject obj = jsonArray.getJSONObject(i);

            String category = obj.getString("category");
            String date = obj.getString("month");

            // API returns null sometimes so we check below.
            String outcome = "";
            if (obj.isNull("outcome_status")) {
                outcome = "No information on outcome.";
            } else {
                outcome = obj.getJSONObject("outcome_status").getString("category");
            }

            String street = obj
                    .getJSONObject("location")
                    .getJSONObject("street")
                    .getString("name");

            crime.setCategory(category);
            crime.setDate(date);
            crime.setStreet(street);
            crime.setOutcome(outcome);

            addCrime(crime);
        }
    }

    private void addCrime(Crime crime) {
        this.crimes.add(crime);
    }
}
