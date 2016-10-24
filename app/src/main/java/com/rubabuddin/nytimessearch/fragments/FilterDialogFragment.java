package com.rubabuddin.nytimessearch.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.rubabuddin.nytimessearch.R;
import com.rubabuddin.nytimessearch.models.Query;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.parceler.Parcels.unwrap;

/**
 * Created by rubab.uddin on 10/21/2016.
 */

public class FilterDialogFragment extends DialogFragment implements DatePickerFragment.DateListener {
    private Query query;
    private Calendar beginDate;
    private Calendar endDate;
    private String sortOrder;
    private String newsDeskFilters;

    Button btnSetFilter;
    Button btnClearFilter;
    CheckBox checkAll;
    CheckBox checkSports;
    CheckBox checkFashion;
    Spinner spSortOrder;
    Button btnSearchStartDate;
    TextView tvStartDate;
    Button btnSearchEndDate;
    TextView tvEndDate;

    public FilterDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public interface FilterListener {
        void onExitFilter(Query query);
    }

    public static FilterDialogFragment newInstance(Query query) {
        FilterDialogFragment frag = new FilterDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("query", Parcels.wrap(query));
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return inflater.inflate(R.layout.fragment_filter, container);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        btnSetFilter = (Button) view.findViewById(R.id.btnSetFilter);
        btnClearFilter =(Button) view.findViewById(R.id.btnClearFilter);

        checkAll = (CheckBox) view.findViewById(R.id.checkbox_all);
        checkSports = (CheckBox) view.findViewById(R.id.checkbox_sports);
        checkFashion = (CheckBox) view.findViewById(R.id.checkbox_fashion);

        spSortOrder = (Spinner) view.findViewById(R.id.spOrder);

        btnSearchStartDate = (Button) view.findViewById(R.id.btnSearchStartDate);
        btnSearchEndDate = (Button) view.findViewById(R.id.btnSearchEndDate);

        tvStartDate = (TextView) view.findViewById(R.id.tvStartDate);
        tvEndDate = (TextView) view.findViewById(R.id.tvEndDate);

        super.onViewCreated(view, savedInstanceState);
        query = unwrap(getArguments().getParcelable("query"));

        //set previous query options
        beginDate = query.getBeginDate();
        endDate = query.getEndDate();
        sortOrder = query.getSortOrder();
        newsDeskFilters = query.getNewsDeskFilters();

        if(beginDate != null){
            tvStartDate.setText(convertDate(beginDate));
        }
        if(endDate != null) {
            tvEndDate.setText(convertDate(endDate));
        }
        if(sortOrder != null){
            if(sortOrder == "Newest"){
                spSortOrder.setSelection(0);
            } else {
                spSortOrder.setSelection(1);
            }
        }
        if(newsDeskFilters != null){
            checkAll.setChecked(false);
            if(newsDeskFilters.contains("sports")){
                checkSports.setChecked(true);
            }
            if(newsDeskFilters.contains("fashion")){
                checkFashion.setChecked(true);
            }
        } else {
            checkAll.setChecked(true);
        }

        String sortOrder = query.getSortOrder();
        if (sortOrder != null) {
            for (int i = 0; i < spSortOrder.getCount(); i++) {
                if (sortOrder.equals(spSortOrder.getItemAtPosition(i).toString())) {
                    spSortOrder.setSelection(i);
                }
            }
        }

        btnSetFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newsDeskFilters = "";
                boolean isAllChecked = checkAll.isChecked();
                boolean isSportsChecked = checkSports.isChecked();
                boolean isFashionChecked = checkFashion.isChecked();

                if(isAllChecked){
                    newsDeskFilters = null;
                } else {
                    if(isSportsChecked && isFashionChecked)
                        newsDeskFilters = "\"sports\"%20\"fashion\"%20\"style\""; //"sports"%20
                    else if(isSportsChecked)
                        newsDeskFilters = "\"sports\""; //"sports"%20
                    else if(isFashionChecked)
                        newsDeskFilters = "\"fashion\"%20\"style\""; //"sports"%20
                }

                query.setNewsDeskFilters(newsDeskFilters);
                onSetFilter(v);
            }
        });

        btnClearFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClearFilter(v);
            }
        });

        btnSearchStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSetStartDate(v);
            }
        });
        btnSearchEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSetEndDate(v);
            }
        });
    }

    public String convertDate(Calendar date){
        SimpleDateFormat format1 = new SimpleDateFormat("MMM dd yyyy");
        String formatted = format1.format(date.getTime());
        return formatted;
    }

    public void onSetStartDate(View view) {
        FragmentManager fm = getFragmentManager();
        DialogFragment df = new DatePickerFragment();
        df.setTargetFragment(FilterDialogFragment.this, 300);
        Bundle args = new Bundle();
        args.putSerializable("begin_date", beginDate);
        df.setArguments(args);
        df.show(fm, "begin_date");
    }

    public void onSetEndDate(View view) {
        FragmentManager fm = getFragmentManager();
        DialogFragment df = new DatePickerFragment();
        df.setTargetFragment(FilterDialogFragment.this, 300);
        Bundle args = new Bundle();
        args.putSerializable("end_date", endDate);
        df.setArguments(args);
        df.show(fm, "end_date");
    }

    public void onSetFilter(View view) {

        String sortOrder = spSortOrder.getSelectedItem().toString();

        query = new Query(query.getQueryStr(), 0, sortOrder, query.getNewsDeskFilters(), beginDate, endDate);
        Log.d("DEBUG", query.toString());
        FilterListener listener = (FilterListener) getActivity();
        listener.onExitFilter(query);
        dismiss();
    }

    public void onClearFilter(View view) {
        query = new Query(query.getQueryStr());
        FilterListener listener = (FilterListener) getActivity();
        listener.onExitFilter(query);
        dismiss();
    }

    @Override
    public void onDateSet(Calendar c, String tag) {
        if(tag.equals("begin_date")) {
            beginDate = c;
            tvStartDate.setText(convertDate(beginDate));

        } else if (tag.equals("end_date")) {
            endDate = c;
            tvEndDate.setText(convertDate(endDate));
        }
    }
}