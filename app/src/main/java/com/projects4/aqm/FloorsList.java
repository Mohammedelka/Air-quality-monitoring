package com.projects4.aqm;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.projects4.aqm.adapters.MyAdapter;
import com.projects4.aqm.controller.RoomDaoImplementation;
import com.projects4.aqm.dto.Room;

import java.util.LinkedList;

public class FloorsList extends AppCompatActivity implements View.OnClickListener {

    FloatingActionButton fab_add;
    RecyclerView roomsRecycler;
    EditText barreRecherche;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    MyAdapter myAdapter;
    LinkedList<Room> rooms;
    ProgressDialog mProgressDialog;
    RoomDaoImplementation cda;

    ImageView prev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floors_list);

        roomsRecycler = findViewById(R.id.list_rooms);
        barreRecherche = findViewById(R.id.search);
        fab_add = findViewById(R.id.fab_add);
        fab_add.setOnClickListener(this);
        prev = findViewById(R.id.back);
        prev.setOnClickListener(this);

        db = FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();

        barreRecherche.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0){
                    myAdapter = new MyAdapter(rooms, FloorsList.this);
                    roomsRecycler.setAdapter(myAdapter);
                }
                if (s.length() != 0) {
                    LinkedList<Room> roomsCopy = new LinkedList<>();
                    System.out.println(s);
                    for (Room room : rooms) {
                        final String title = room.getTitle().toLowerCase();
                        if (title.contains(s.toString().toLowerCase()))
                            roomsCopy.add(room);
                    }
                    myAdapter = new MyAdapter(roomsCopy, FloorsList.this);
                    roomsRecycler.setAdapter(myAdapter);
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        getRooms();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.fab_add){
            startActivity(new Intent(this, AddRoom.class));
        }
        if(view.getId() == R.id.back){
            finish();
        }
    }

    @SuppressLint("StaticFieldLeak")
    void getRooms(){
        new AsyncTask() {
            protected void onPreExecute(){
                rooms = new LinkedList<Room>();
                cda = new RoomDaoImplementation();
                showProgressDialog();
            }

            protected Object doInBackground(Object[] objects) {
                rooms.addAll(cda.getAll());
                return null;
            }

            protected void onProgressUpdate(Integer... progress) {}

            protected void onPostExecute(Object result) {
                roomsRecycler.setHasFixedSize(true);
                LinearLayoutManager layoutManager = new LinearLayoutManager(FloorsList.this);
                roomsRecycler.setLayoutManager(layoutManager);
                myAdapter = new MyAdapter(rooms, FloorsList.this);
                roomsRecycler.setAdapter(myAdapter);
                hideProgressDialog();
            }
        }.execute();

    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading.......");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}


