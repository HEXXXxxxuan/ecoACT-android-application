package com.go4.application;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.go4.application.historical.SuburbHistoricalActivity;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseLoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Intent returnIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        returnIntent = new Intent();
    }

    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            // Already logged-in user, return to app flow
            Toast.makeText(this, "Already logged in as " + user.getEmail(), Toast.LENGTH_LONG).show();
            setResult(RESULT_OK);
            returnIntent.putExtra("User", user);
            finish();
        }
        else{
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
                    setResult(RESULT_OK);
                    returnIntent.putExtra("User", user);

                    startActivity(new Intent(this, SuburbHistoricalActivity.class));

                    finish();
                })
                .addOnFailureListener(task -> {
                    Toast.makeText(this, task.getMessage(), Toast.LENGTH_LONG).show();
                    setResult(RESULT_CANCELED);
                    finish();
                });
        });
    }
}