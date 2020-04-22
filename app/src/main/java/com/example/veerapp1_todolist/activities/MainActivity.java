package com.example.veerapp1_todolist.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.veerapp1_todolist.R;
import com.example.veerapp1_todolist.alarm.AlarmReceiver;
import com.example.veerapp1_todolist.data.Task;
import com.example.veerapp1_todolist.data.TaskViewModel;
import com.example.veerapp1_todolist.data.TaskViewModelFactory;
import com.example.veerapp1_todolist.recyclerview.TaskItemDecoration;
import com.example.veerapp1_todolist.recyclerview.TasksAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.List;

//TODO: SET UP SETTINGS SCREEN
//TODO: SET UP SHARED PREFERENCES

public class MainActivity extends AppCompatActivity {

    private static final int REQ_CODE_ADD = 0, REQ_CODE_EDIT = 1;
    public static final int VERTICAL_SPACING = 4;

    private static final String TAG = "MainActivity_message";

    public static final String EXTRA_NAME = "com.example.veerapp1_todolist.EXTRA_NAME",
            EXTRA_ID = "com.example.veerapp1_todolist.EXTRA_ID",
            EXTRA_DESC = "com.example.veerapp1_todolist.EXTRA_DESC", EXTRA_DAY = "com.example.veerapp1_todolist.EXTRA_DAY",
            EXTRA_YEAR = "com.example.veerapp1_todolist.EXTRA_YEAR", EXTRA_MONTH = "com.example.veerapp1_todolist.EXTRA_MONTH",
            EXTRA_HOUR = "com.example.veerapp1_todolist.EXTRA_HOUR", EXTRA_MINUTE = "com.example.veerapp1_todolist.EXTRA_MINUTE";

    private RecyclerView recyclerView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private TaskViewModel viewModel;
    private TasksAdapter adapter;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w(TAG, "onCreate: MainActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: DEFINE VARIABLES
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.layout_drawer);
        navigationView = findViewById(R.id.view_navigation);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.actionbartoggle_open,
                R.string.actionbartoggle_close);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

            /** NEED 3 THINGS FOR A DRAWER LAYOUT
             * 1. DRAWER LAYOUT AS THE MAIN LAYOUT IN THE RESOURCE LAYOUT XML
             * 2. A NAVIGATION VIEW FOR THE VIEW AND THE MENU ITEMS (SHOWN WHEN SWIPED) --> ATTRS DEFINED IN ACTIVITY_MAIN
             * 3. ACTION BAR DRAWER TOGGLE TO TOGGLE THE DRAWER LAYOUT FROM THE ACTION BAR
             */
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true); //shows the hamburger at top right corner for menu
        actionBarDrawerToggle.syncState();


            /** NEED 3 THINGS FOR A RECYCLER VIEW
             * 1. LAYOUT RECYCLER VIEW ITEM
             * 2. ADAPTER WITH VIEWHOLDER
             * 3. LAYOUT MANAGER WITH DIRECTION AND TYPE
            */
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new TasksAdapter(this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true); //this makes it more efficient (you can read more about this online)

            //Extra RecyclerView Decorations:
        TaskItemDecoration itemDecoration = new TaskItemDecoration(VERTICAL_SPACING);
        recyclerView.addItemDecoration(itemDecoration);


        /** NEED 3 THINGS TO RUN VIEWMODEL
         * 1. LIVEDATA TO BE OBSERVED
         * 2. A VIEWMODEL PROVIDER TO CREATE AND SYNC THE VIEWMODEL WITH THE CURRENT LIFECYCLE AND PASS IN THE VIEWMODEL CLASS
         * 3. OBSERVER TO OBSERVE WHEN IT IS CHANGED
         */
        viewModel = new ViewModelProvider(this, new TaskViewModelFactory(getApplication(),
                sharedPreferences.getString("sort_by", "-1").equals("time"))).get(TaskViewModel.class);


        //TODO: SET LISTENERS
            //ITEM TOUCH HELPER LISTENER TO LISTEN FOR SWIPES ON RECYCLERVIEW
                //Simple callback makes it much more efficient, but lose a lot more feautres
                //for only swiping and dragging, simple callback is enough
                //otherwise use callback instead of simple callback
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int i = viewHolder.getAdapterPosition();

                //Task task = viewModel.getAllTasks().getValue().get(i); --> this works
                Task task = adapter.getTasks().get(i); //--> this also works
                String name = task.getName();

                cancelAlarm(task.getId()); //cancel the alarm
                viewModel.delete(task); //delete from database

                //the live data will automatically notify the adapter of dataset change
                Toast.makeText(MainActivity.this, "Deleting task: "+name, Toast.LENGTH_SHORT).show();
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new TasksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Task task) {
                Intent intent = new Intent(MainActivity.this, EditTaskActivity.class);

                intent.putExtra(EXTRA_NAME, task.getName());
                intent.putExtra(EXTRA_DAY, task.getDay());
                intent.putExtra(EXTRA_MONTH, task.getMonth());
                intent.putExtra(EXTRA_YEAR, task.getYear());
                intent.putExtra(EXTRA_DESC, task.getDescription());
                intent.putExtra(EXTRA_HOUR, task.getHour());
                intent.putExtra(EXTRA_MINUTE, task.getMinute());
                intent.putExtra(EXTRA_ID, task.getId());
                
                startActivityForResult(intent, REQ_CODE_EDIT);
            }
        });

            //TODO: SET THE NAVIGATION MENU AND ITEM ON CLICK LISTENER
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.menu_general:
                        Toast.makeText(MainActivity.this, "You clicked general", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menu_settings:
                        //go to settings
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
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




    //OTHER LISTENERS:

    public void FABclick(View view) { //Floating Action Button on Click Listener
        Intent intent = new Intent(this, AddTaskActivity.class);
        startActivityForResult(intent, REQ_CODE_ADD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_CODE_ADD){
            if(resultCode == RESULT_OK){
                Snackbar.make(findViewById(R.id.layout_drawer), R.string.mainAct_taskSaved,
                        BaseTransientBottomBar.LENGTH_SHORT).show();
            }
            else {
                Snackbar.make(findViewById(R.id.layout_drawer), R.string.mainAct_taskNotSaved,
                        BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == REQ_CODE_EDIT){
            if(resultCode == RESULT_OK){
                Log.w(TAG, "onActivityResult: RESULT_OK");
                Snackbar.make(findViewById(R.id.layout_drawer), R.string.mainAct_taskUpdated,
                        BaseTransientBottomBar.LENGTH_SHORT).show();
            }
            else{
                Log.w(TAG, "onActivityResult: RESULT_CANCELED");
                Snackbar.make(findViewById(R.id.layout_drawer), R.string.mainAct_taskNotUpdated,
                        BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        }

    }



    //MENU METHODS + SETUP
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.menu_clear:
                //TODO: Show a dialog to ask for it
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_error_red_24dp)
                        .setTitle("Clear All Tasks")
                        .setMessage("Are You Sure You Want To Clear All Tasks?");

                alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //user clicked Yes button
                        viewModel.deleteAll();
                        Snackbar.make(findViewById(R.id.layout_drawer),
                                "Cleared All Tasks", BaseTransientBottomBar.LENGTH_SHORT).show();
                    }
                });
                alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //user clicked cancel button
                    }
                });
                alertBuilder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //DEBUGGING:

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(TAG, "onPause: MainActivity");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.w(TAG, "onPostResume: MainActivity");

        viewModel.setSortedByTime(sharedPreferences.getString("sort_by", "-1").equals("time"));
        viewModel.getAllTasks().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                //update recycler view
                adapter.setTasks(tasks); //calls on data set changed inside the method
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(TAG, "onDestroy: MainActivity");
    }
}
