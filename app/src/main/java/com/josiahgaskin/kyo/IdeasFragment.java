package com.josiahgaskin.kyo;


import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IdeasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IdeasFragment extends Fragment {
    private ArrayList<String> mIdeas = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;
    private ListView listView;

    public IdeasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment IdeasFragment.
     */
    public static IdeasFragment newInstance() {
        IdeasFragment fragment = new IdeasFragment();
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
        listView = (ListView) inflater.inflate(R.layout.fragment_ideas, container, false);
        listView.setAdapter(mAdapter);
        return listView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Find all the counter definitions
        SQLiteDatabase db = DatabaseProvider.getDb();
        if (db != null) {
            Cursor c = db.rawQuery("SELECT * FROM Ideas", new String[]{});
            int index = c.getColumnIndex("value");
            if (c.moveToFirst()) {
                mIdeas.clear();
                do {
                    mIdeas.add(c.getString(index));
                } while (c.moveToNext());
            }
        }
        mAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mIdeas);

        ((Home) activity).onSectionAttached(getString(R.string.title_ideas));
    }
}
