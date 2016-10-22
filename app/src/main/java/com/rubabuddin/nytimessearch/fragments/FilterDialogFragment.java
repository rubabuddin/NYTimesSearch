package com.rubabuddin.nytimessearch.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Spinner;

import com.rubabuddin.nytimessearch.R;
import com.rubabuddin.nytimessearch.models.Query;

import org.parceler.Parcels;

import java.util.Calendar;

import static org.parceler.Parcels.unwrap;

/**
 * Created by rubab.uddin on 10/21/2016.
 */

public class FilterDialogFragment extends DialogFragment implements DatePickerFragment.DateListener {

    private Query query;
    private Calendar beginDate;
    private Calendar endDate;
    Button btnSetFilter;
    //@BindView(R.id.btnSetFilter) Button btnSetFilter;
    Button btnClearFilter;
    Spinner spNewsDesk;
    Spinner spSortOrder;
    Button btnSearchStartDate;
    Button btnSearchEndDate;



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
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return inflater.inflate(R.layout.fragment_filter, container);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        btnSetFilter = (Button) view.findViewById(R.id.btnSetFilter);
        //@BindView(R.id.btnSetFilter) Button btnSetFilter;
        btnClearFilter =(Button) view.findViewById(R.id.btnClearFilter);
        spNewsDesk = (Spinner) view.findViewById(R.id.spNewsDesk);
        spSortOrder = (Spinner) view.findViewById(R.id.spOrder);
        btnSearchStartDate = (Button) view.findViewById(R.id.btnSearchStartDate);
        btnSearchEndDate = (Button) view.findViewById(R.id.btnSearchEndDate);
        //ButterKnife.bind(getActivity());
        super.onViewCreated(view, savedInstanceState);

        query = unwrap(getArguments().getParcelable("query"));

        beginDate = query.getBeginDate();
        endDate = query.getEndDate();

        String newsDeskFilters = query.getNewsDeskFilters();
        if (newsDeskFilters != null) {
            for (int i = 0; i < spNewsDesk.getCount(); i++) {
                if (newsDeskFilters.equals(spNewsDesk.getItemAtPosition(i).toString())) {
                    spNewsDesk.setSelection(i);
                }
            }
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
        String newsDeskFilters = spNewsDesk.getSelectedItem().toString();
        String sortOrder = spSortOrder.getSelectedItem().toString();

        query = new Query(query.getQueryStr(), 0, sortOrder, newsDeskFilters, beginDate, endDate);

        FilterListener listener = (FilterListener) getActivity();
        listener.onExitFilter(query);
        dismiss();
    }

    public void onClearFilter(View view) {
        query = new Query(query, 0);
        FilterListener listener = (FilterListener) getActivity();
        listener.onExitFilter(query);
        dismiss();
    }

    @Override
    public void onDateSet(Calendar c, String tag) {
        if(tag.equals("begin_date")) {
            beginDate = c;
        } else if (tag.equals("end_date")) {
            endDate = c;
        }
    }


}