package com.example.veerapp1_todolist.alarm;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.veerapp1_todolist.R;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {

    public static TimePickerFragment getInstance(){return new TimePickerFragment();}

    private TimePickerListener listener;
    private CurrentTimeListener currentTimeListener;
    private boolean hasCurrentTimeListener = false;

    public interface TimePickerListener{
        void onDetachTimePicker();
    }

    public interface CurrentTimeListener{
        int[] setCurrentTime();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY), minute = c.get(Calendar.MINUTE);

        if(hasCurrentTimeListener){
            int[] time = currentTimeListener.setCurrentTime();
            if(time != null){
                minute = time[0];
                hour = time[1];
            }
            else{
                throw new RuntimeException("Received null currentTimeListener.setCurrentTime()");
            }
        }

        TimePickerDialog picker =
                new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener)getActivity(), hour, minute, false);

        //set listener for cancel click
        picker.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onDetachTimePicker();
            }
        });

        return picker;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof TimePickerListener){ //send the information to the context
            listener = (TimePickerListener) context;
        }
        else {
            throw new RuntimeException(context+" must implement TimePickerListener");
        }

        if(context instanceof CurrentTimeListener){
            currentTimeListener = (CurrentTimeListener) context;
            hasCurrentTimeListener = true;
        }

    }
}
