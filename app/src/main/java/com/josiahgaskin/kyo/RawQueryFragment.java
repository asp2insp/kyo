package com.josiahgaskin.kyo;

import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
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
    private ArrayList<List<String>> mQueryResults = new ArrayList<>();
    private EditText mQueryText;

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
        mQueryText = (EditText) rootView.findViewById(R.id.editText);
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
        ((Home) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
}
