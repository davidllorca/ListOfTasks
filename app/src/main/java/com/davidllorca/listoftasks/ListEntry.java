package com.davidllorca.listoftasks;

/**
 * Model class ListEntry.
 *
 * Task to add at list.
 * Created by David Llorca <davidllorcabaron@gmail.com> on 7/16/14.
 */
public class ListEntry {

    // Attributes
    private int id;
    private String task;
    private String place;
    private String description;
    private int importance;

    // Constructor
    public ListEntry() {
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }
}
