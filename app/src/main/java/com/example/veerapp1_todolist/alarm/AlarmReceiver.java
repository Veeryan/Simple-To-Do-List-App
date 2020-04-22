package com.example.veerapp1_todolist.alarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.veerapp1_todolist.activities.MainActivity;
import com.example.veerapp1_todolist.R;
import com.example.veerapp1_todolist.activities.ViewTaskFromNotification;
import com.example.veerapp1_todolist.data.Repository;
import com.example.veerapp1_todolist.data.Task;


public class AlarmReceiver extends BroadcastReceiver {

    public static final String PRIMARY_CHANNEL_ID = "com.example.veerapp1_todolist.PRIMARY_NOT_CHANNEL";
    private static final String TAG = "AlarmReceiver";

    private Repository repo; //don't need this class to be lifecycle aware

    private int id;
    private String taskName;
    private NotificationManager manager;

    @Override
    public void onReceive(Context context, Intent intent) {
        //get repository:
        repo = new Repository(context);

        //get values
        id = intent.getIntExtra(MainActivity.EXTRA_ID,-1);
        taskName = intent.getStringExtra(MainActivity.EXTRA_NAME);
        Task task = repo.getTaskById(id);

        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "Notificaiton Channel", NotificationManager.IMPORTANCE_HIGH);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.enableLights(true);
            channel.setDescription("Notification From AlarmManager");

            manager.createNotificationChannel(channel);
        }

        //set up intent to ViewTask class
        Intent openAppIntent = new Intent(context, ViewTaskFromNotification.class);
        openAppIntent.putExtra(MainActivity.EXTRA_NAME, taskName);
        openAppIntent.putExtra(MainActivity.EXTRA_DESC, task.getDescription());
        openAppIntent.putExtra(MainActivity.EXTRA_HOUR, task.getHour());
        openAppIntent.putExtra(MainActivity.EXTRA_MINUTE, task.getMinute());
        openAppIntent.putExtra(MainActivity.EXTRA_DAY, task.getDay());
        openAppIntent.putExtra(MainActivity.EXTRA_MONTH, task.getMonth());
        openAppIntent.putExtra(MainActivity.EXTRA_YEAR, task.getYear());
        Log.w(TAG, "Info sent to View Task Activity");

        PendingIntent pintent = PendingIntent.getActivity(context, 0, openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setAutoCancel(true)
                .setContentIntent(pintent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(taskName)
                .setContentText(task.getDescription());

        manager.notify(id, builder.build());
        repo.delete(task);
    }
}
