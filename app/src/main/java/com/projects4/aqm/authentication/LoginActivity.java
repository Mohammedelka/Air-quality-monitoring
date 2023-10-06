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
import com.projects4.aqm.DashBoard;
import com.projects4.aqm.FloorsList;
import com.projects4.aqm.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    TextView signup, re;
    EditText login_email, login_password;
    Button login_button;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        login_button = findViewById(R.id.login_button);
        signup = findViewById(R.id.create_account);
        re = findViewById(R.id.forgot_password);
        login_button.setOnClickListener(this);
        signup.setOnClickListener(this);
        re.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
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
        if(view.getId() == R.id.login_button){
            String email = login_email.getText().toString(), password = login_password.getText().toString();
            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Fill the required fields!",
                        Toast.LENGTH_SHORT).show();
            }
            else {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                Log.d("Ok", "signInWithEmail:success");
                                Toast.makeText(this, "Authentication Success.", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                Log.w("Error", "signInWithEmail:failure", task.getException());
                                Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        });
            }
        }

        else if(view.getId() == R.id.create_account)
            startActivity(new Intent(this, SignupActivity.class));

        else if(view.getId() == R.id.forgot_password)
            Toast.makeText(this, "PASSWORD RESET", Toast.LENGTH_SHORT).show();
    }
}

