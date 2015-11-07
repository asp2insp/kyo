package com.josiahgaskin.kyo;

import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class RawQueryFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private BaseAdapter mAdapter;
    private ArrayAdapter<String> mAutocompleteAdapter;
    private ArrayList<String> mAutocompletes = new ArrayList<>();
    private ArrayList<List<String>> mQueryResults = new ArrayList<>();
    private AutoCompleteTextView mQueryText;

    public RawQueryFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static RawQueryFragment newInstance(int sectionNumber) {
        RawQueryFragment fragment = new RawQueryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.raw_query, container, false);
        mQueryText = (AutoCompleteTextView) rootView.findViewById(R.id.editText);
        mQueryText.setAdapter(mAutocompleteAdapter);
        rootView.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runQuery();
            }
        });
        ListView listView = (ListView) rootView.findViewById(R.id.listView);
        listView.setAdapter(mAdapter);
        TextView empty = (TextView) inflater.inflate(R.layout.empty, null);
        listView.setEmptyView(empty);
        return rootView;
    }

    private void runQuery() {
        SQLiteDatabase db = DatabaseProvider.getDb();
        String query = mQueryText.getText().toString();
        if (!query.isEmpty() && db != null) {
            mQueryResults.clear();
            try {
                // Try compiling to check the query
                db.compileStatement(query);

                // If it's valid, save it
                updateAutocomplete(query);
                // Actually execute
                Cursor c = db.rawQuery(query, new String[]{});
                if (c != null) {
                    int cols = c.getColumnCount();
                    List<String> header = new ArrayList<>(cols);
                    for (int i = 0; i < cols; i++ ) {
                        header.add(c.getColumnName(i));
                    }
                    mQueryResults.add(header);
                    if (c.moveToFirst()) {
                        do {
                            List<String> row = new ArrayList<>(cols);
                            for (int i = 0; i < cols; i++) {
                                row.add(c.getString(i));
                            }
                            mQueryResults.add(row);
                        } while (c.moveToNext());
                    }
                    c.close();
                }
            } catch (SQLException e) {
                mQueryResults.add(Collections.singletonList(e.getLocalizedMessage()));
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    private void updateAutocomplete(String query) {
        History.add(query);
        mAutocompletes.add(query);
        mAutocompleteAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        mAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return mQueryResults.size();
            }

            @Override
            public Object getItem(int position) {
                return mQueryResults.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = activity.getLayoutInflater().inflate(R.layout.query_result_row, null);
                }
                LinearLayout row = (LinearLayout) convertView.findViewById(R.id.row);
                row.removeAllViews();
                List<String> rowData = mQueryResults.get(position);
                for (String c : rowData) {
                    TextView tv = (TextView) activity.getLayoutInflater().inflate(R.layout.col, null);
                    tv.setText(c);
                    row.addView(tv);
                }
                return convertView;
            }
        };
        mAutocompletes.clear();
        mAutocompletes.addAll(History.getAll());
        mAutocompleteAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, mAutocompletes);

        ((Home) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
}
