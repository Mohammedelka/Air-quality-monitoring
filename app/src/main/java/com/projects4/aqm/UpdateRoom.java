package com.projects4.aqm;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.projects4.aqm.controller.RoomDaoImplementation;
import com.projects4.aqm.dto.Room;

import java.sql.SQLException;
import java.util.Objects;

public class UpdateRoom extends AppCompatActivity {

    TextInputEditText title, size, capacity;
    Button ok;
    ImageView prev;

    String id, text, cp, sz;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_room);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        text = intent.getStringExtra("title");
        cp = intent.getStringExtra("capacity");
        sz = intent.getStringExtra("size");

        RoomDaoImplementation impl = new RoomDaoImplementation();

        title = findViewById(R.id.title);
        size = findViewById(R.id.size);
        capacity = findViewById(R.id.capacity);

        prev = findViewById(R.id.back);
        prev.setOnClickListener(view -> finish());

        ok = findViewById(R.id.proceed);
        ok.setOnClickListener(view -> {
            try {
                impl.update(
                        new Room(
                            id,
                            Objects.requireNonNull(title.getText()).toString(),
                            Objects.requireNonNull(capacity.getText()).toString(),
                            Objects.requireNonNull(size.getText()).toString()
                        )
                );
                startActivity(new Intent(this, FloorsList.class));
            }
            catch (SQLException e) {
                Toast.makeText(this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        title.setText(text);
        capacity.setText(cp);
        size.setText(sz);
    }
}
