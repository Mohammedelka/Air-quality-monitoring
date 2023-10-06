package com.projects4.aqm.dto;

public class Room {
    // Properties
    private String id;
    private String title;

    private String capacity;
    private String size;



    // Getters & Setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getCapacity() {
        return capacity;
    }
    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }
    public String getSize() {
        return size;
    }
    public void setSize(String size) {
        this.size = size;
    }

    // Constructor
    public Room(String id, String title) {
        this.id = id;
        this.title = title;
    }
    public Room(String id, String title, String capacity, String size) {
        this.id = id;
        this.title = title;
        this.capacity = capacity;
        this.size = size;
    }
}
