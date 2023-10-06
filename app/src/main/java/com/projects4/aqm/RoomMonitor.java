package com.projects4.aqm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects4.aqm.controller.RoomDaoImplementation;

import java.sql.SQLException;

public class RoomMonitor extends AppCompatActivity {

    TextView co2_field, temp_field, hum_field, light_field, perc_field, comment;
    ImageView prev, more;
    FirebaseDatabase db;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview);

        // Intent Values 
        String id = getIntent().getStringExtra("id");
        String title = getIntent().getStringExtra("title");
        String cp = getIntent().getStringExtra("capacity");
        String sz = getIntent().getStringExtra("size");

        // ProgressBar
        pb = findViewById(R.id.progress_circular);

        // Back Button
        prev = findViewById(R.id.back);
        prev.setOnClickListener(view -> finish());
        
        // More Button
        more = findViewById(R.id.more);

        try {
            if(title.isEmpty()) more.setVisibility(View.GONE);
        }
        catch (Exception e){
            more.setVisibility(View.GONE);
        }

        more.setOnClickListener(view -> {
            Context context = this;
            PopupMenu popup = new PopupMenu(context, view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.edit_room_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_edit){
                    Toast.makeText(context, "Updating element ...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, UpdateRoom.class);
                    intent.putExtra("id", id);
                    intent.putExtra("title", title);
                    intent.putExtra("capacity", cp);
                    intent.putExtra("size", sz);
                    context.startActivity(intent);
                }
                else if (item.getItemId() == R.id.menu_delete){
                    Toast.makeText(context, "Deleting element ...", Toast.LENGTH_SHORT).show();
                    try {
                        new RoomDaoImplementation().delete(id);
                        finish();
                    } catch (SQLException e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else if(item.getItemId() == R.id.menu_predict){
                    Toast.makeText(context, "Predicting Occupancy ...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, DetectOccupancy.class);
                    intent.putExtra("title", title);
                    intent.putExtra("co2", co2_field.getText().toString());
                    intent.putExtra("temp", temp_field.getText().toString());
                    intent.putExtra("hum", hum_field.getText().toString());
                    intent.putExtra("lux", light_field.getText().toString());
                    startActivity(intent);
                }
                return false;
            });
            popup.show();
        });
        
        // Fields in the Layout
        co2_field = findViewById(R.id.co2_value);
        temp_field = findViewById(R.id.temp_value);
        hum_field = findViewById(R.id.hum_value);
        light_field = findViewById(R.id.lux_value);
        perc_field = findViewById(R.id.perc_value);
        comment = findViewById(R.id.comment_value);

        // Getting Data from Firebase Realtime Database
        db = FirebaseDatabase.getInstance();
        DatabaseReference carb = db.getReference("Co2Level"),
                hum = db.getReference("humidity"),
                temp = db.getReference("temperature"),
                lux = db.getReference("light");

        // Listeners
        carb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Co2Level
                String value = dataSnapshot.getValue(String.class);
                assert value != null;
                double val = Double.parseDouble(value);
                value = (((int) val < 1000) ? String.valueOf((int) val) : "1000+");
                co2_field.setText(value);

                // AQI %
                double progress = val / 1200;
                int progressPercentage = (int) (progress * 100);
                pb.setProgress(progressPercentage);
                String perc = progressPercentage + " %";
                perc_field.setText(perc);

                // Comment_Val
                String message = (progressPercentage < 67) ? "THE AIR QUALITY IS GOOD" :
                        (progressPercentage < 84) ? "THE AIR QUALITY IS MODERATE" : "THE AIR QUALITY IS BAD";
                comment.setText(message);

                // Measures taken
                LayerDrawable layerDrawable = (LayerDrawable) pb.getProgressDrawable();
                if (progressPercentage < 67) {
                    comment.setTextColor(Color.rgb(0, 100, 0));
                    layerDrawable.getDrawable(1)
                            .setColorFilter(Color.rgb(0, 100, 0), PorterDuff.Mode.SRC_IN);
                }
                else if (progressPercentage < 84) {
                    comment.setTextColor(Color.rgb(255, 215, 0));
                    layerDrawable.getDrawable(1)
                            .setColorFilter(Color.rgb(255, 215, 0), PorterDuff.Mode.SRC_IN);
                }
                else {
                    showBuzzingNotification(perc);
                    comment.setTextColor(Color.rgb(139, 0, 0));
                    layerDrawable.getDrawable(1)
                            .setColorFilter(Color.rgb(139, 0, 0), PorterDuff.Mode.SRC_IN);
                }
                Log.d("DataChange", "Value is: " + value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Cancelled", "Failed to read value.", error.toException());
            }
        });
        hum.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                hum_field.setText(value);
                Log.d("DataChange", "Value is: " + value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Cancelled", "Failed to read value.", error.toException());
            }
        });
        temp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                temp_field.setText(value);
                Log.d("DataChange", "Value is: " + value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Cancelled", "Failed to read value.", error.toException());
            }
        });
        lux.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                light_field.setText(value);
                Log.d("DataChange", "Value is: " + value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Cancelled", "Failed to read value.", error.toException());
            }
        });
    }

    private void showBuzzingNotification(String perc) {
        // Create a notification channel
        String channelId = "buzzing_channel";
        CharSequence channelName = "Buzzing Channel";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(channelId, channelName, importance);
        }

        // Configure additional channel settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel.enableVibration(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel.setVibrationPattern(new long[] { 0, 500, 200, 500 });
        }

        // Register the notification channel with the system
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }

        // Create a notification builder
        String message = "AQI : " + perc;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.co2)
                .setContentTitle("Attention Needed!")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(false);

        // Set vibration pattern
        long[] vibrationPattern = {0, 500, 200, 500};
        builder.setVibrate(vibrationPattern);

        // Create a unique notification ID
        int notificationId = 1;
        notificationManager.notify(notificationId, builder.build());
    }

}