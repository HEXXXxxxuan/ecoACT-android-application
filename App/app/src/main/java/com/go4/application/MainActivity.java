package com.go4.application;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.go4.application.historical.SuburbHistoricalActivity;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    public FirebaseUser user;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart(){
        super.onStart();
        firebaseLogin();
        Log.d("Debugging", "User: " + user.getEmail());
        startActivity(new Intent(this, SuburbHistoricalActivity.class));
    }

    private void firebaseLogin(){
        user = mAuth.getCurrentUser();
        if(user!=null){
            // Already logged-in user, return to app flow
            Toast.makeText(this, "Already logged in as " + user.getEmail(), Toast.LENGTH_LONG).show();
        }
        else {
            signIn();
        }
    }

    private void signIn() {
        setContentView(R.layout.activity_firebase_login_ui);
        Button loginSubmit = findViewById(R.id.bt_login);
        loginSubmit.setOnClickListener(view -> {
            String email = ((EditText) findViewById(R.id.lg_username)).getText().toString();
            String pass = ((EditText)findViewById(R.id.lg_password)).getText().toString();
            mAuth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(task -> {
                    FirebaseUser user = task.getUser();
                    assert user != null;
                    Toast.makeText(this, "Successful login as " + user.getDisplayName(), Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(task -> Toast.makeText(this, task.getMessage(), Toast.LENGTH_LONG).show());
        });
    }
}
