package com.example.veerapp1_todolist.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class Task {

    public Task(@NonNull String name, @NonNull String description, int minute, int hour, int year, int month, int day) {
        this.name = name;
        this.description = description;
        this.minute = minute;
        this.hour = hour;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    @NonNull
    @ColumnInfo(name = "name")
    private String name;
    public String getName() { return name; }

    @NonNull
    @ColumnInfo(name = "description")
    private String description;
    public String getDescription() { return description; }

    @NonNull
    @ColumnInfo(name = "minute")
    private int minute;
    public int getMinute() { return minute; }

    @NonNull
    @ColumnInfo(name = "hour")
    private int hour;
    public int getHour() { return hour; }

    @NonNull
    @ColumnInfo(name = "year")
    private int year;
    public int getYear() { return year; }

    @NonNull
    @ColumnInfo(name = "month")
    private int month;
    public int getMonth() { return month; }

    @NonNull
    @ColumnInfo(name = "day")
    private int day;
    public int getDay() { return day;}
}
