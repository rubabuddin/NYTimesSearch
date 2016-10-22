package com.rubabuddin.nytimessearch.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by rubab.uddin on 10/22/2016.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public interface DateListener {
        void onDateSet(Calendar c, String tag);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Calendar beginDate = (Calendar) getArguments().getSerializable("begin_date");
        Calendar endDate = (Calendar) getArguments().getSerializable("end_date");

        Calendar c = Calendar.getInstance();
        if (beginDate != null) {
            c = beginDate;
        } else if (endDate != null) {
            c = endDate;
        }

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        DateListener dateListener = (DateListener) getTargetFragment();
        dateListener.onDateSet(c, getTag());
    }
}
