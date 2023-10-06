package com.projects4.aqm;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ControlPurifier extends AppCompatActivity {

    TextView status;
    ImageView fan, prev;
    FirebaseDatabase db;

    // ObjectAnimator rotationAnimator;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch aSwitch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_fan);

        prev = findViewById(R.id.back);
        prev.setOnClickListener(view -> finish());

        fan = findViewById(R.id.fan);
        status = findViewById(R.id.status);


        db = FirebaseDatabase.getInstance();
        DatabaseReference fan_db = db.getReference("isFanEnabled");

        aSwitch = findViewById(R.id.switchCompat);
        aSwitch.setOnClickListener(view -> {
            String message;
            if(aSwitch.isChecked()){
                fan_db.setValue("True");
                message = "STATUS : ON";
                aSwitch.setChecked(true);
                status.setText(message);
            }
            else {
                fan_db.setValue("False");
                message = "STATUS : OFF";
                aSwitch.setChecked(false);
                status.setText(message);
            }
        });

        fan_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                assert value != null;

                String message;
                if (value.trim().equals("False")) {
                    message = "STATUS : OFF";
                    aSwitch.setChecked(false);
                } else {
                    message = "STATUS : ON";
                    aSwitch.setChecked(true);
                }
                status.setText(message);

                Log.d("DataChange", "Value is: " + value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Cancelled", "Failed to read value.", error.toException());
            }
        });
    }

    /*
    private void onOn() {
        String message = "STATUS : ON";
        rotateAnimation();
        status.setText(message);
        aSwitch.setChecked(true);
    }

    private void onOff() {
        String message = "STATUS : OFF";
        stopRotationAnimation();
        status.setText(message);
        aSwitch.setChecked(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void rotateAnimation() {
        // Create the ObjectAnimator
        rotationAnimator = ObjectAnimator.ofFloat(fan, "rotation", 0f, 360f);
        rotationAnimator.setDuration(1000);
        rotationAnimator.setRepeatCount(ObjectAnimator.INFINITE);  // Optional: To repeat the animation indefinitely
        rotationAnimator.setInterpolator(new LinearInterpolator()); // Optional: Set the desired interpolator
        // Start the animation
        rotationAnimator.start();
    }

    private void stopRotationAnimation() {
        if (rotationAnimator != null) {
            rotationAnimator.cancel();
        }
    }

     */

}
