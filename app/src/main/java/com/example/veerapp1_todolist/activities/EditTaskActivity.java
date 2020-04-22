package com.example.veerapp1_todolist.activities;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.example.veerapp1_todolist.R;
import com.example.veerapp1_todolist.alarm.AlarmReceiver;
import com.example.veerapp1_todolist.alarm.DatePickerFragment;
import com.example.veerapp1_todolist.alarm.TimePickerFragment;
import com.example.veerapp1_todolist.data.Task;
import com.example.veerapp1_todolist.data.TaskViewModel;
import com.example.veerapp1_todolist.data.TaskViewModelFactory;

import java.text.DateFormat;
import java.util.Calendar;

public class EditTaskActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener,
        DatePickerFragment.DateFragmentListener, TimePickerFragment.TimePickerListener,
        DatePickerFragment.CurrentDateListener, TimePickerFragment.CurrentTimeListener {

    private static final String TAG = "EditTaskActivity";

    private EditText etName, etDesc;
    private TextView tvSelectDate, tvSelectTime;

    private int dayOfMonth = -1, month = -1, year = -1;
    private int hourOfDay = -1, minute = -1;
    private String name = "", description = "";
    private int id;

    private TaskViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        setTitle("Edit");

        //instantiate views:
        etDesc = findViewById(R.id.etTaskDescription); etName = findViewById(R.id.etTaskName);
        tvSelectDate = findViewById(R.id.tvSelectDate); tvSelectTime = findViewById(R.id.tvSelectTime);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        name = b.getString(MainActivity.EXTRA_NAME);
        description = b.getString(MainActivity.EXTRA_DESC);
        month = b.getInt(MainActivity.EXTRA_MONTH);
        dayOfMonth = b.getInt(MainActivity.EXTRA_DAY);
        year = b.getInt(MainActivity.EXTRA_YEAR);
        hourOfDay = b.getInt(MainActivity.EXTRA_HOUR);
        minute = b.getInt(MainActivity.EXTRA_MINUTE);
        id = b.getInt(MainActivity.EXTRA_ID, -1);

        etName.setText(name); etDesc.setText(description);
        setDateAndTime();

        //set up viewmodel
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean sortedByTime = sharedPreferences.getString("sort_by", "-1").equals("time");
        viewModel = new ViewModelProvider(this, new TaskViewModelFactory(getApplication(),
                sortedByTime)).get(TaskViewModel.class);
    }

    private void setDateAndTime() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.YEAR, year);
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);

        String formattedDate = DateFormat.getDateInstance().format(c.getTime());
        String formattedTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());

        tvSelectDate.setText(formattedDate); tvSelectTime.setText(formattedTime);
    }


    public void saveTask(View view) {
        name = etName.getText().toString(); description = etDesc.getText().toString();

        Intent reply_intent = new Intent();
        if(id != -1){
            Task task = new Task(name, description, minute, hourOfDay, year, month, dayOfMonth);
            task.setId(id);
            viewModel.update(task); //update task in database

            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year); c.set(Calendar.MONTH, month); c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            c.set(Calendar.HOUR_OF_DAY, hourOfDay); c.set(Calendar.MINUTE, minute); c.set(Calendar.SECOND, 0);
            startAlarm(c, task); //update the alarm

            setResult(RESULT_OK, reply_intent);
        }
        else{
            setResult(RESULT_CANCELED, reply_intent);
        }
        finish();
    }

    public void selectTime(View view) {
        tvSelectTime.setEnabled(false);
        DialogFragment picker = TimePickerFragment.getInstance();
        picker.show(getSupportFragmentManager(), "Time Picker");
    }

    public void selectDate(View view) {
        tvSelectDate.setEnabled(false);
        DialogFragment picker = DatePickerFragment.getInstance();

        picker.show(getSupportFragmentManager(), "Date Picker");
    }


    //SET LISTENERS

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        this.year = year; this.month = month; this.dayOfMonth = dayOfMonth;

        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, month); c.set(Calendar.DAY_OF_MONTH, dayOfMonth); c.set(Calendar.YEAR, year);
        String formattedDate = DateFormat.getDateInstance().format(c.getTime());

        tvSelectDate.setEnabled(true);
        tvSelectDate.setText(formattedDate);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.hourOfDay = hourOfDay; this.minute = minute;

        //tvSelectTime.setText(hourOfDay + " and "+ minute);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MINUTE, minute); c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        String formattedTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());

        tvSelectTime.setEnabled(true);
        tvSelectTime.setText(formattedTime);
    }

    @Override
    public void onDetachDatePickerFragment() {
        tvSelectDate.setEnabled(true);
    }

    @Override
    public void onDetachTimePicker() {
        tvSelectTime.setEnabled(true);
    }

    @Override
    public int[] setCurrentDate() {
        return new int[]{dayOfMonth, month, year}; //order is day, month, year
    }

    @Override
    public int[] setCurrentTime() {
        return new int[]{minute, hourOfDay}; //order is minute, hour
    }


    //ALARM MANAGER + METHODS SETUP
    public void startAlarm(Calendar c, Task task){
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);

        intent.putExtra(MainActivity.EXTRA_ID, task.getId());
        intent.putExtra(MainActivity.EXTRA_NAME, task.getName());

        PendingIntent pintent = PendingIntent.getBroadcast(this, task.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            assert alarmManager != null;
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pintent);
        }
        else{
            assert alarmManager != null;
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pintent);
        }
    }

    public void cancelAlarm(int alarmId){
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pintent = PendingIntent.getBroadcast(this, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        assert alarmManager != null;
        alarmManager.cancel(pintent);
    }

/*    public void updateAlarm(Calendar c, Task task){
        cancelAlarm(task.getId());
        startAlarm(c, task);
    }*/
}
