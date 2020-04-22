package com.example.veerapp1_todolist.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class TaskViewModelFactory implements ViewModelProvider.Factory {

    private Application app;
    private boolean sortedByTime;

    public TaskViewModelFactory(Application app, boolean sortedByTime) {
        this.app = app;
        this.sortedByTime = sortedByTime;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new TaskViewModel(app, sortedByTime);
    }
}
