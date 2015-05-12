package com.example.chris_paterson.onsapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONParser {
    private final String json;
    ArrayList<Crime> crimes;

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
            String outcome = obj.getJSONObject("outcome_status").getString("category");

            String street = obj
                    .getJSONObject("location")
                    .getJSONObject("street")
                    .getString("name");

            crime.setCategory(category);
            crime.setDate(date);
            crime.setOutcome(outcome);
            crime.setStreet(street);

            addCrime(crime);
        }
    }

    private void addCrime(Crime crime) {
        this.crimes.add(crime);
    }
}
