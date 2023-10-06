package com.projects4.aqm.controller;

import com.projects4.aqm.dto.Room;
import com.projects4.aqm.interfaces.RoomDao;
import com.projects4.aqm.model.Model;

import java.sql.SQLException;
import java.util.List;

public class RoomDaoImplementation implements RoomDao {

    Model model;

    public RoomDaoImplementation(){
        model = new Model();
    }

    @Override
    public Room get(String id) throws SQLException {
        return model.get(id);
    }

    @Override
    public List<Room> getAll() {
        return model.getAll();
    }

    @Override
    public void insert(Room room) throws SQLException {
        model.insert(room);
    }

    @Override
    public void update(Room room) throws SQLException {
        model.update(room);
    }

    @Override
    public void delete(String id) throws SQLException {
        model.delete(id);
    }

}
