package com.example.chris_paterson.geocrime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CrimeAdapter extends ArrayAdapter<Crime>{
    private Context context;
    private List<Crime> crimes;

    public CrimeAdapter(Context context, int resource, List<Crime> objects) {
        super(context, resource, objects);
        this.context = context;
        this.crimes = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;

        // ensure we have a view to work with.
        if(null == itemView) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            itemView = layoutInflater.inflate(R.layout.crime_list, parent, false);
        }

        // find the note to work with
        Crime currentCrime = crimes.get(position);

        // fill the view
        TextView category = (TextView) itemView.findViewById(R.id.category);
        TextView street = (TextView) itemView.findViewById(R.id.street);
        TextView date = (TextView) itemView.findViewById(R.id.date);
        TextView outcome = (TextView) itemView.findViewById(R.id.outcome);

        category.setText("Category: " + toTitle(currentCrime.getCategory()));
        street.setText("Area: " + currentCrime.getStreet());
        date.setText("Date: " + currentCrime.getDate());
        outcome.setText("Outcome: " + currentCrime.getOutcome());

        return itemView;
    }

    private String toTitle(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }
}
