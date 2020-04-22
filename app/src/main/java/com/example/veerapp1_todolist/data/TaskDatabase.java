package com.example.veerapp1_todolist.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Task.class}, version = 1, exportSchema = false)
public abstract class TaskDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();

    private static TaskDatabase instance;

    public static TaskDatabase getDatabase(final Context context){
        if(instance == null){
            synchronized (TaskDatabase.class){ //makes the class a singleton, only one can run at a time
                if(instance == null){
                    instance = Room.databaseBuilder(context.getApplicationContext(), TaskDatabase.class,
                            "database")
                            .fallbackToDestructiveMigration()
                            //TODO: ADD CALLBACK FOR INSTANTIATING
                            .build();
                }
            }
        }
        return instance;
    }

}
