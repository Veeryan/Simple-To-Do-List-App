package com.example.veerapp1_todolist.data;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class Repository {
    private TaskDao dao;
    private LiveData<List<Task>> tasksByTime, tasksByName;

    public Repository(Context application){
        TaskDatabase db = TaskDatabase.getDatabase(application);
        dao = db.taskDao();
        tasksByTime = dao.getAllTasksSortedByTime();
        tasksByName = dao.getAllTasksSortedByName();
    }

    public LiveData<List<Task>> getAllTasksSortedByTime() {
        return tasksByTime;
    }
    public LiveData<List<Task>> getAllTasksSortedByName() {
        return tasksByName;
    }



    //TODO: CREATE A SINGLE (OR MAYBE EVEN MULTIPLE) THREAD EXECUTOR TO MANAGE THE NEW THREADS
    public int insert(Task task){
        try {
            return new AsyncInsertTask(dao).execute(task).get();
        }
        catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return -1; //didn't receive id
    }

    public void delete(Task task){
        new Thread(new DeleteTask(dao, task)).start();
    }

    public void update(Task task){
        new Thread(new UpdateTask(dao, task)).start();
    }

    public Task getTaskById(int id) {
        try {
            return new AsyncGetTaskById(dao).execute(id).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteAll(){
        new Thread(new DeleteAllTasks(dao)).start();
    }



    //need to implement the methods you need in order to interact with the database
    //but need to run each method in a background thread

    public static class AsyncGetTaskById extends AsyncTask<Integer, Void, Task>{
        private TaskDao dao;

        public AsyncGetTaskById(TaskDao dao) {
            this.dao = dao;
        }

        @Override
        protected Task doInBackground(Integer... integers) {
            return dao.getTaskById(integers[0]);
        }
    }

/*    public static class GetTaskByIdRunnable implements Runnable{
        private TaskDao dao;
        private int id;
        private Task task;

        public GetTaskByIdRunnable(TaskDao dao, int id) {
            this.dao = dao;
            this.id = id;
        }

        @Override
        public void run() {
            task = dao.getTaskById(id);
        }
    }*/

    public static class AsyncInsertTask extends AsyncTask<Task, Void, Integer>{

        private TaskDao dao;

        public AsyncInsertTask(TaskDao dao) {
            this.dao = dao;
        }

        @Override
        protected Integer doInBackground(Task... tasks) {
            return (int)(dao.insert(tasks[0]));
        }
    }

    private static class DeleteAllTasks implements Runnable{
        private TaskDao dao;

        public DeleteAllTasks(TaskDao dao) {
            this.dao = dao;
        }

        @Override
        public void run() {
            dao.deleteAll();
        }
    }

    private static class InsertTask implements Runnable {

        private TaskDao dao;
        private Task task;

        InsertTask(TaskDao d, Task t){
            dao = d;
            task = t;
        }

        @Override
        public void run() {
            dao.insert(task);
        }
    }

    public static class UpdateTask implements Runnable{

        private TaskDao dao;
        private Task task;

        public UpdateTask(TaskDao dao, Task task) {
            this.dao = dao;
            this.task = task;
        }

        @Override
        public void run() {
            dao.update(task);
        }
    }



    private static class DeleteTask implements Runnable {

        private TaskDao dao;
        private Task task;

        DeleteTask(TaskDao d, Task t){
            dao = d;
            task = t;
        }

        @Override
        public void run() {
            dao.delete(task);
        }
    }



}
