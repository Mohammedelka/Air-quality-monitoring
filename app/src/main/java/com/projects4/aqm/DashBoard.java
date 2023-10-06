package com.projects4.aqm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.projects4.aqm.authentication.LoginActivity;

import java.util.Objects;

public class DashBoard extends AppCompatActivity {

    CardView view1, view2, view3, view4;
    TextView username, org, email;
    ImageView settings;

    FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        view1 = findViewById(R.id.card_view_2);
        view2 = findViewById(R.id.card_view_3);
        view3 = findViewById(R.id.card_view_4);
        view4 = findViewById(R.id.card_view_5);

        username = findViewById(R.id.title_value);
        org = findViewById(R.id.building);
        email = findViewById(R.id.email);

        assert currentUser != null;
        db.collection("users")
                .document(Objects.requireNonNull(currentUser.getEmail())).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        String ms1 = "Address : " + doc.getString("building"),
                                ms2 = "Hello, " + doc.getString("name"),
                                ms3 = "Email : " + currentUser.getEmail();

                        org.setText(ms1);
                        username.setText(ms2);
                        email.setText(ms3);
                    }
                })
                .addOnFailureListener(e -> Log.e("not ok", "Error occurred", e));

        settings = findViewById(R.id.settings);

        view1.setOnClickListener(view -> { startActivity(new Intent(this, FloorsList.class)); });
        view2.setOnClickListener(view -> { startActivity(new Intent(this, RoomMonitor.class)); });
        view3.setOnClickListener(view -> { startActivity(new Intent(this, ControlPurifier.class)); });
        view4.setOnClickListener(view -> { startActivity(new Intent(this, DetectOccupancy.class)); });

        settings.setOnClickListener(view -> {
            Context context = this;
            PopupMenu popup = new PopupMenu(context, view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.settings, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.logout){
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                return false;
            });
            popup.show();
        });
    }
}
