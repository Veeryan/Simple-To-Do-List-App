package com.example.veerapp1_todolist.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.veerapp1_todolist.R;

import java.text.DateFormat;
import java.util.Calendar;

public class ViewTaskFromNotification extends AppCompatActivity {

    private static final String TAG = "ViewTaskFromNotificatio";

    private TextView tvName, tvDesc;
    private TextView tvSelectDate, tvSelectTime;
    
    private int dayOfMonth, month, year;
    private int hourOfDay, minute;
    private String name, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task_from_notification);
        setTitle("View");
        //instantiate views:
        tvDesc = findViewById(R.id.etTaskDescription); tvName = findViewById(R.id.etTaskName);
        tvSelectDate = findViewById(R.id.tvSelectDate); tvSelectTime = findViewById(R.id.tvSelectTime);

        Bundle b = getIntent().getExtras();
        assert b != null;
        
        name = b.getString(MainActivity.EXTRA_NAME);
        description = b.getString(MainActivity.EXTRA_DESC);
        dayOfMonth = b.getInt(MainActivity.EXTRA_DAY);
        month = b.getInt(MainActivity.EXTRA_MONTH);
        year = b.getInt(MainActivity.EXTRA_YEAR);
        hourOfDay = b.getInt(MainActivity.EXTRA_HOUR);
        minute = b.getInt(MainActivity.EXTRA_MINUTE);
        
        tvName.setText(name); tvDesc.setText(description);
        setDateAndTime();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
