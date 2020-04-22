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
import android.widget.Toast;

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

public class AddTaskActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener,
        DatePickerFragment.DateFragmentListener, TimePickerFragment.TimePickerListener {

    private static final String TAG = "AddTaskActivity_Message";
    
    private EditText etName, etDesc;
    private TextView tvSelectDate, tvSelectTime;

    private int dayOfMonth = -1, month = -1, year = -1;
    private int hourOfDay = -1, minute = -1;
    private String name = "", description = "";

    private TaskViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        setTitle("Create");
        
        //define variables
        etName = findViewById(R.id.etTaskName);
        etDesc = findViewById(R.id.etTaskDescription);
        tvSelectDate = findViewById(R.id.tvSelectDate);
        tvSelectTime = findViewById(R.id.tvSelectTime);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean sortedByTime = sharedPreferences.getString("sort_by", "-1").equals("time");
        viewModel = new ViewModelProvider(this, new TaskViewModelFactory(getApplication(),
                sortedByTime)).get(TaskViewModel.class);
    }

    /** NEED 4 THINGS FOR DIALOG PICKER
     * 1. FRAGMENT THAT EXTENDS DIALOG_PICKER SUBCLASS
     * 2. ON_CREATE_DIALOG METHOD IN FRAGMENT THAT RETURNS NEW INSTANCE OF THAT SPECIFIC PICKER
     * 3. INSTANTIATING THAT PICKER IN ACTIVITY WITH A DIALOG CLICKER LISTENER
     * 4. SHOW THAT THROUGH A FRAGMENT MANAGER (IN THIS CASE SUPPORT FRAGMENT MANAGER)
     */
    
    public void selectDate(View view) {
        tvSelectDate.setEnabled(false); //don't allow user to double click
        DialogFragment datePicker = DatePickerFragment.getInstance();
        datePicker.show(getSupportFragmentManager(), "Date Picker");
    }

    public void selectTime(View view) {
        tvSelectTime.setEnabled(false); //don't allow user to double click
        DialogFragment timePicker = TimePickerFragment.getInstance();
        timePicker.show(getSupportFragmentManager(), "Time Picker");
    }

    //on click listener for the button "Add Task"
    public void saveTask(View view) {
        name = etName.getText().toString(); description = etDesc.getText().toString();

        if(name.trim().isEmpty() || description.trim().isEmpty() || dayOfMonth == -1 || month == -1 || year == -1
                || minute == -1 || hourOfDay == -1){
            Toast.makeText(this, R.string.addtask_emptyfield_message, Toast.LENGTH_SHORT).show();
        }
        else{
            Intent reply_intent = new Intent();
            Task task = new Task(name, description, minute, hourOfDay, year, month, dayOfMonth);
            int idPass = viewModel.insert(task); //insert task to database

            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year); c.set(Calendar.MONTH, month); c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            c.set(Calendar.HOUR_OF_DAY, hourOfDay); c.set(Calendar.MINUTE, minute); c.set(Calendar.SECOND, 0);
            startAlarm(c, viewModel.getTaskById(idPass)); //start new alarm

            setResult(RESULT_OK, reply_intent);
            finish();
        }
    }


    //LISTENERS FOR FRAGMENTS:

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


    //show the date text view after cancel has been selected
    @Override
    public void onDetachDatePickerFragment() {
        tvSelectDate.setEnabled(true);
    }

    //show the time text view after cancel has been selected
    @Override
    public void onDetachTimePicker() {
        tvSelectTime.setEnabled(true);
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
