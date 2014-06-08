/*
 * BTC-e client
 *     Copyright (C) 2014  QuarkDev Solutions <quarkdev.solutions@gmail.com>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.QuarkLabs.BTCeClient.fragments;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.QuarkLabs.BTCeClient.ListTypes;
import com.QuarkLabs.BTCeClient.OrdersAdapter;
import com.QuarkLabs.BTCeClient.R;
import com.QuarkLabs.BTCeClient.loaders.OrdersLoader;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TransHistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<JSONObject> {
    private static final int LOADER_ID = 0;
    private static final int WEEK = 7 * 24 * 60 * 60 * 1000;
    private final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private OrdersAdapter mAdapter;
    private ListView mListView;
    private Date mStartDateValue;
    private Date mEndDateValue;
    private ProgressBar mLoadingView;
    private TextView mNoItems;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if we have StartDate and EndDate selected before
        if (savedInstanceState != null) {
            try {
                mStartDateValue = mSimpleDateFormat.parse(savedInstanceState.getString("startDate"));
                mEndDateValue = mSimpleDateFormat.parse(savedInstanceState.getString("endDate"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            Calendar calendar = Calendar.getInstance();
            mStartDateValue = new Date(calendar.getTimeInMillis() - WEEK);
            mEndDateValue = new Date(calendar.getTimeInMillis());
        }

        //creating bundle for loader
        Bundle bundle = new Bundle();
        bundle.putString("startDate", String.valueOf(mStartDateValue.getTime() / 1000));
        bundle.putString("endDate", String.valueOf(mEndDateValue.getTime() / 1000));
        getLoaderManager().initLoader(LOADER_ID, bundle, this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getActionBar().setTitle(getResources().getStringArray(R.array.NavSections)[4]);
        return inflater.inflate(R.layout.fragment_trans_history, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new OrdersAdapter(getActivity(), ListTypes.Transactions);
        mListView = (ListView) getView().findViewById(R.id.TransHistoryContainer);

        final EditText startDate = (EditText) getView().findViewById(R.id.StartDateValue);
        final EditText endDate = (EditText) getView().findViewById(R.id.EndDateValue);
        startDate.setText(mSimpleDateFormat.format(mStartDateValue));
        endDate.setText(mSimpleDateFormat.format(mEndDateValue));
        Button makeQuery = (Button) getView().findViewById(R.id.TransHistoryMakeQueryButton);
        View.OnClickListener showDatePicker = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView makeQuery = (TextView) v;
                Date date = null;
                try {
                    date = mSimpleDateFormat.parse(makeQuery.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int year = 1999;
                int month = 0;
                int day = 1;
                if (date != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                }


                DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar calendar = Calendar.getInstance();
                                if (makeQuery.getId() == R.id.StartDateValue) {
                                    calendar.setTime(mStartDateValue);
                                    calendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                                    mStartDateValue = calendar.getTime();
                                } else {
                                    calendar.setTime(mEndDateValue);
                                    calendar.set(year, monthOfYear, dayOfMonth, 23, 59, 59);
                                    mEndDateValue = calendar.getTime();
                                }

                                makeQuery.setText(mSimpleDateFormat.format(calendar.getTime()));
                            }
                        }, year, month, day
                );
                datePickerDialog.getDatePicker().setCalendarViewShown(false);
                datePickerDialog.show();
            }
        };
        startDate.setOnClickListener(showDatePicker);
        endDate.setOnClickListener(showDatePicker);
        makeQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("startDate", String.valueOf(mStartDateValue.getTime() / 1000));
                bundle.putString("endDate", String.valueOf(mEndDateValue.getTime() / 1000));
                getLoaderManager().restartLoader(LOADER_ID, bundle, TransHistoryFragment.this);
                mListView.setAdapter(null);
                mNoItems.setVisibility(View.GONE);
                mListView.setEmptyView(mLoadingView);

            }
        });
        mLoadingView = (ProgressBar) getView().findViewById(R.id.Loading);
        mNoItems = (TextView) getView().findViewById(R.id.NoItems);
        mListView.setEmptyView(mLoadingView);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.actions_refresh_filter, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public Loader<JSONObject> onCreateLoader(int id, Bundle args) {
        if (mNoItems != null) {
            mNoItems.setVisibility(View.GONE);
        }
        if (mLoadingView != null) {
            mListView.setEmptyView(mLoadingView);
        }
        return new OrdersLoader(getActivity(), args, ListTypes.Transactions);
    }

    @Override
    public void onLoadFinished(Loader<JSONObject> loader, JSONObject data) {
        if (data == null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.GeneralErrorText), Toast.LENGTH_LONG)
                    .show();
            mNoItems.setText(getResources().getString(R.string.OoopsError).toUpperCase());
            mListView.setEmptyView(mNoItems);
            mLoadingView.setVisibility(View.GONE);
        } else if (data.optInt("success") == 0) {
            mNoItems.setText(data.optString("error").toUpperCase());
            mListView.setEmptyView(mNoItems);
            mLoadingView.setVisibility(View.GONE);
        } else {
            mAdapter.updateEntries(data);
            mListView.setAdapter(mAdapter);
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("startDate", mSimpleDateFormat.format(mStartDateValue));
        outState.putString("endDate", mSimpleDateFormat.format(mEndDateValue));
    }

    @Override
    public void onLoaderReset(Loader<JSONObject> loader) {
        mListView.setAdapter(null);
        mNoItems.setVisibility(View.GONE);
        mListView.setEmptyView(mLoadingView);
    }
}