package com.example.veerapp1_todolist.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao //DAO must be an interface or an abstract class
public interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Task task);

    @Delete
    void delete(Task task);

    @Update
    void update(Task task);

    @Query("SELECT * FROM tasks WHERE id = :id")
    Task getTaskById(int id);

    @Query("SELECT * FROM tasks ORDER BY year ASC, month ASC, day ASC, hour ASC, minute ASC")
    LiveData<List<Task>> getAllTasksSortedByTime();

    @Query("SELECT * FROM tasks ORDER BY name ASC")
    LiveData<List<Task>> getAllTasksSortedByName();

    @Query("DELETE FROM tasks")
    void deleteAll();

    //TODO: All delete all data (clear querry)

}
