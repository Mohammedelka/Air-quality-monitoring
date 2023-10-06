package com.projects4.aqm;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.projects4.aqm.controller.RoomDaoImplementation;
import com.projects4.aqm.dto.Room;

import java.sql.SQLException;

public class AddRoom extends AppCompatActivity {
    EditText title, size, capacity;
    Button ok;
    ImageView prev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);

        title = findViewById(R.id.title);
        size = findViewById(R.id.size);
        capacity = findViewById(R.id.capacity);

        prev = findViewById(R.id.back);
        prev.setOnClickListener(view -> finish());

        ok = findViewById(R.id.proceed);
        ok.setOnClickListener(view -> {
            RoomDaoImplementation impl = new RoomDaoImplementation();
            try {
                Room room = new Room(
                        "unused",
                        title.getText().toString(),
                        capacity.getText().toString(),
                        size.getText().toString()
                );
                impl.insert(room);
                Intent intent = new Intent(this, FloorsList.class);
                startActivity(intent);
            } catch (SQLException e) {
                Toast.makeText(this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}