package com.example.veerapp1_todolist.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    //ViewModel is lifecycle aware --> destroying activity and application does not affect the data, force re-query of DB,
    //or cause memory leaks --> making it a great practice

    //ViewModel vs AndroidViewModel --> AndroidViewModel takes Application this way we can have access to the application context.
    //The only difference with AndroidViewModel is it comes with the application context,
    // which is helpful if you require context to get a system service or have a similar requirement

    private Repository repo;
    private LiveData<List<Task>> tasks;
    private boolean sortedByTime;

    public TaskViewModel(@NonNull Application application, boolean sortedByTime) {
        super(application);
        repo = new Repository(application); //application fits in perfectly with repository

/*        if(sortedByTime) tasks = repo.getAllTasksSortedByTime();
        else tasks = repo.getAllTasksSortedByName();*/

        this.sortedByTime = sortedByTime;
    }

    public LiveData<List<Task>> getAllTasks(){
        if(sortedByTime) tasks = repo.getAllTasksSortedByTime();
        else tasks = repo.getAllTasksSortedByName();

        return tasks;
    }

    public void setSortedByTime(boolean sortedByTime) {
        this.sortedByTime = sortedByTime;
    }

    public int insert(Task task){
        return repo.insert(task);
    }

    public void delete(Task task){
        repo.delete(task);
    }

    public void update(Task task){
        repo.update(task);
    }

    public Task getTaskById(int id){ return repo.getTaskById(id); }

    public void deleteAll(){ repo.deleteAll(); }


}
