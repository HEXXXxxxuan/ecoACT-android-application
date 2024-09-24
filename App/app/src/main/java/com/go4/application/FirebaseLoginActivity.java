package com.go4.application;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.firebase.ui.auth.IdpResponse;
import com.go4.application.historical.SuburbHistoricalActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;


import java.util.Collections;

public class FirebaseLoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseUser user;;

    ActivityResultLauncher<Intent> getUser = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            r -> {
                if(r.getResultCode()== MainActivity.RESULT_OK){
                    user = FirebaseAuth.getInstance().getCurrentUser();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_login_ui);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            // Already logged-in user, return to app flow
            Toast.makeText(this, "Already logged in as " + user.getEmail(), Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            getUser.launch(new Intent(this, SuburbHistoricalActivity.class));
            // finish();
        }
        else{
            signIn();
        }
    }
    private void signIn() {
        Button loginSubmit = findViewById(R.id.login_submit);
        loginSubmit.setOnClickListener(view -> {
            String email = ((EditText) findViewById(R.id.lg_email)).getText().toString();
            String pass = ((EditText)findViewById(R.id.lg_password)).getText().toString();
            mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert(user!=null);
                        Toast.makeText(this, "Successful login as " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        getUser.launch(new Intent(this, SuburbHistoricalActivity.class));
                        // finish();
                    }
                    else{
                        Toast.makeText(this, "Try Again.", Toast.LENGTH_SHORT).show();
                    }
                });
        });
    }
}