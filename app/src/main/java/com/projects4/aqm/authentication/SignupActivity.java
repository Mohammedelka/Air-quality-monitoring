package com.projects4.aqm.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.projects4.aqm.DashBoard;
import com.projects4.aqm.R;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

        TextView login;
        EditText name_inp, org_inp, email_inp, password_inp;
        Button signup;
        private FirebaseAuth mAuth;

        FirebaseFirestore db;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_signup);
            name_inp = findViewById(R.id.name);
            org_inp = findViewById(R.id.org);
            email_inp = findViewById(R.id.email);
            password_inp = findViewById(R.id.password);
            signup = findViewById(R.id.signup_button);
            login = findViewById(R.id.signin_button);
            signup.setOnClickListener(this);
            login.setOnClickListener(this);
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
        }

        @Override
        public void onStart() {
            super.onStart();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);
        }

        private void updateUI(FirebaseUser currentUser) {
            if(currentUser != null){
                Toast.makeText(this, currentUser.getEmail(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, DashBoard.class);
                startActivity(intent);
            }
        }

        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.signup_button){
                String name = name_inp.getText().toString(),
                        org = org_inp.getText().toString(),
                        email = email_inp.getText().toString(),
                        password = password_inp.getText().toString();

                if(name.isEmpty() || email.isEmpty() || password.isEmpty()){
                    Toast.makeText(this, "Fill the required fields!",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, task -> {
                                if (task.isSuccessful()) {

                                    Map<String, Object> user = new HashMap<>();
                                    user.put("name", name);
                                    user.put("building", org);
                                    user.put("password", password.hashCode());

                                    db.collection("users")
                                            .document(email).set(user)
                                            .addOnSuccessListener(docRef -> Log.d("ok", "DocumentSnapshot added with ID: " + email))
                                            .addOnFailureListener(e -> Log.w("not ok", "Error adding document", e));

                                    Log.d("Ok", "createUserWithEmail:success");
                                    Toast.makeText(this, "Authentication success.",
                                            Toast.LENGTH_SHORT).show();

                                    FirebaseUser cuser = mAuth.getCurrentUser();
                                    updateUI(cuser);
                                }
                                else {
                                    Log.w("Error", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
            else if(view.getId() == R.id.signin_button) finish();
        }
    }