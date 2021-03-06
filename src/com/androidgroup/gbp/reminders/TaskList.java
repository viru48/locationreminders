// TaskList.java
// Greg Paton
// 20 December 2012
// wrapper class to hold a list of tasks
// and provide methods for sorting tasks

package com.androidgroup.gbp.reminders;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.LinkedList;

import android.location.Location;
import android.util.Log;

import com.google.android.maps.GeoPoint;

public class TaskList {

    private LinkedList<Task> tasks;
    

    public TaskList() {
        tasks = new LinkedList<Task>();
    }
    
    public LinkedList<Task> get_tasks() {
        return tasks;
    }
    
    public String[] get_names() {
        int size = tasks.size();
        if (size == 0)
            return null;
        String[] names = new String[size];
        for (int i = 0; i < size; ++i)
            names[i] = tasks.get(i).get_name();
        return names;
    }
    
    // Update a task by the task id
    // if task for id does not exist, add it to list
    public boolean set_by_id(Task _task, int id) {
        boolean ret = false;
        if (id < 0 || _task == null)
            return ret;
        for(Task task : tasks) {
            if (task.get_id() == id) {
                task = _task;
                ret = true;
            }
        }
        if (ret == false) {
            tasks.add(_task);
            ret = true;
        }
        return ret;
    }
    
    public Task get_by_id(int id) {
        if (id < 0)
            return null;
        for (Task task : tasks) {
            if (task.get_id() == id)
                return task;
        }
        return null;
    }
    
    public LinkedList<Task> find_near_location(GeoPoint loc) {
        if (loc == null)
            return null;
        LinkedList<Task> list = new LinkedList<Task>();
        float[] results = new float[2];
        float distance;
        for (Task task : tasks) {
            Location.distanceBetween(task.get_location().getLatitudeE6(), 
                                     task.get_location().getLongitudeE6(), 
                                     loc.getLatitudeE6(), 
                                     loc.getLongitudeE6(), 
                                     results);
            distance = results[0] / 1000000;
            Log.i("LOC", "Distance between" + String.valueOf(distance));
            if (distance <= task.get_remind_distance())
                list.add(task);
        }
        return list;
    }
    
    public void sort_by_due_time() {
        int size = tasks.size();
        if (size == 0)
            return;
        Comparator<Task> comp = new Comparator<Task>() {
            public int compare(Task task1, Task task2) {
                return (int)(task1.get_due_time() - task2.get_due_time());
            }
        };
        java.util.Collections.sort(tasks, comp);
    }
    
    public void sort_by_distance(GeoPoint gp) {
        final GeoPoint loc = gp;
        int size = tasks.size();
        if (size == 0)
            return;
        Comparator<Task> comp = new Comparator<Task>() {
            public int compare(Task task1, Task task2) {
                float[] dist1 = new float[1];
                float[] dist2 = new float[1];
                double task1_lat = task1.get_location_gp().getLatitudeE6();
                double task1_long = task1.get_location_gp().getLongitudeE6();
                double task2_lat = task2.get_location_gp().getLatitudeE6();
                double task2_long = task2.get_location_gp().getLongitudeE6();
                android.location.Location.distanceBetween(loc.getLatitudeE6(), 
                                                          loc.getLongitudeE6(), 
                                                          task1_lat, 
                                                          task1_long, 
                                                          dist1);
                android.location.Location.distanceBetween(loc.getLatitudeE6(), 
                                                          loc.getLatitudeE6(), 
                                                          task2_lat, 
                                                          task2_long, 
                                                          dist2);
                return (int)(dist1[0] - dist2[0]);
            }
        };
        java.util.Collections.sort(tasks, comp);
    }
    
    public void sort_by_name() {
        int size = tasks.size();
        if (size == 0)
            return;
        Comparator<Task> comp = new Comparator<Task>() {
            public int compare(Task task1, Task task2) {
                return task1.get_name().compareToIgnoreCase(task2.get_name());
            }
        };
        java.util.Collections.sort(tasks, comp);
    }
}
