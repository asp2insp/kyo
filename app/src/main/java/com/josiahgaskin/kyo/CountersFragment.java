package com.josiahgaskin.kyo;


import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CountersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CountersFragment extends Fragment {
    private GridLayout mContainer;

    private static class Counter {
        String name;
        Integer value;

        public Counter(String name, Integer value) {
            this.name = name;
            this.value = value;
        }
    }

    private ArrayList<Counter> mCounters = new ArrayList<>();

    public CountersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment CountersFragment.
     */
    public static CountersFragment newInstance() {
        CountersFragment fragment = new CountersFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContainer = (GridLayout) inflater.inflate(R.layout.fragment_counters, container, false);
        GridLayout.LayoutParams lp = new GridLayout.LayoutParams(GridLayout.spec(0,1), GridLayout.spec(0,1));
        for (Counter c : mCounters) {
            View button = inflater.inflate(R.layout.counter_button, null);
            button.setBackgroundColor(getColorFromString(c.name));
            ((TextView)button.findViewById(R.id.name)).setText(c.name);
            ((TextView)button.findViewById(R.id.value)).setText(Integer.toString(c.value));
            mContainer.addView(button, lp);
        }
        return mContainer;
    }

    private static int getColorFromString(String s) {
        int hs = s.hashCode();
        if (hs < 0) {
            hs *= -1;
        }
        double p = hs * 1.0/Integer.MAX_VALUE;
        double colord = p * 0xFFFFFF;
        int res = (int) Math.round(colord);
        return res | 0xFF000000;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Find all the counter definitions
        SQLiteDatabase db = DatabaseProvider.getDb();
        LayoutInflater inflater = activity.getLayoutInflater();
        if (db != null) {
            Cursor c = db.rawQuery("SELECT * FROM CounterDefs", new String[]{});
            int nameIndex = c.getColumnIndex("name");
            int valueIndex = c.getColumnIndex("value");
            if (c.moveToFirst()) {
                do {
                    mCounters.add(new Counter(c.getString(nameIndex), c.getInt(valueIndex)));
                } while (c.moveToNext());
            }
        }

        ((Home) activity).onSectionAttached(getString(R.string.title_counters));
    }
}
