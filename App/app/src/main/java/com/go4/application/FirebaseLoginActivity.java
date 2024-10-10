package com.go4.application;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseLoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Intent returnIntent;

    /**
     * Using {@link ActivityResultContract} API for safety.
     * <p>
     *     Typical usage requires a {@link androidx.activity.result.ActivityResultLauncher} to store
     *     the resulting contract
     * </p>
     */
    public static class FirebaseLoginActivityResultContract extends ActivityResultContract <Void, FirebaseUser> {
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Void unused) {
            return new Intent(context, FirebaseLoginActivity.class);
        }
        @Override
        public FirebaseUser parseResult(int i, Intent intent) {
            if(i != FirebaseLoginActivity.RESULT_OK) {
                return null;
            }
            else{
                return intent.getParcelableExtra("User", FirebaseUser.class);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_firebase_login_ui);
        returnIntent = new Intent();
        signIn();
    }

    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            // Already logged-in user, return to app flow
            Toast.makeText(this, "Already logged in as " + user.getEmail(), Toast.LENGTH_LONG).show();
            returnIntent.putExtra("User", user);
            setResult(RESULT_OK, returnIntent);
            finish();
        }
        else{
            signIn();
        }
    }

    private void signIn() {
        Button loginSubmit = findViewById(R.id.bt_login);
        loginSubmit.setOnClickListener(view -> {
            String email = ((EditText) findViewById(R.id.lg_username)).getText().toString();
            String pass = ((EditText) findViewById(R.id.lg_password)).getText().toString();
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (pass.isEmpty()) {
                Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
                return;
            }
            mAuth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_LONG).show();
                        returnIntent.putExtra("User", user);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                    else {
                        Toast.makeText(this, "Successfully logged in as the null user?!?!?!", Toast.LENGTH_LONG).show();
                        Log.e("Login", "Successful login as a null user");
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
        });
    }
}