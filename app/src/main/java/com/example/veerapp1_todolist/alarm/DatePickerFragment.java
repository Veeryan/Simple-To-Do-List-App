package com.example.veerapp1_todolist.alarm;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.veerapp1_todolist.R;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {

    private boolean hasCurrentDateListener = false;

    public static DatePickerFragment getInstance(){ //good practice
        return new DatePickerFragment();
    }

    private DateFragmentListener listener;
    private CurrentDateListener currentDateListener;

    public interface DateFragmentListener{ //listener for communication to activity or other fragment
        void onDetachDatePickerFragment();
    }

    public interface CurrentDateListener{//listener to get current date
        int[] setCurrentDate();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR), month = cal.get(Calendar.MONTH), day = cal.get(Calendar.DAY_OF_MONTH);

        if(hasCurrentDateListener){
            int[] date = currentDateListener.setCurrentDate();
            if(date != null) {
                day = date[0];
                month = date[1];
                year = date[2];
            }
            else{
                throw new RuntimeException("Received null currentDateListener.setCurrentDate()");
            }
        }


        //this will create the fragment and show the current date
        DatePickerDialog picker =
                new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);

        //set listener for when the cancel button is clicked
        picker.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onDetachDatePickerFragment();
            }
        });
        return picker;
    }

    //set the listener for the context (in this case the AddTaskActivity)
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof DateFragmentListener){
            listener = (DateFragmentListener) context;
        }
        else{ //good practice
            throw new RuntimeException(context.toString()+" must implement DateFragmentListener");
        }

        if(context instanceof CurrentDateListener){
            currentDateListener = (CurrentDateListener) context;
            hasCurrentDateListener = true;
        }
    }
}
