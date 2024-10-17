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

/**
 * Interacts with {@link FirebaseAuth} to login and retrieve cached {@link FirebaseUser}s.
 * <p>Contains an {@link ActivityResultContract} implementation {@link FirebaseLoginActivityResultContract}
 * for type-safe returns</p>
 * <p>Intended use-case launcher in {@link MainActivity} as a <code>private final</code> variable</p>
 *
 * @author u7327620 Ryan Foote
 */
public class FirebaseLoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Intent returnIntent;

    /**
     * Extends {@link ActivityResultContract} API for safety.
     * <p> Typical usage requires a {@link androidx.activity.result.ActivityResultLauncher} to store
     * the resulting contract</p>
     *
     * @author u7327620 Ryan Foote
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
        setContentView(R.layout.activity_firebase_login_ui);
        mAuth = FirebaseAuth.getInstance();
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

    /**
     * Interacts with {@link FirebaseAuth} to handle user sign-ins.
     * <p>Upon successful sign-in, puts {@link FirebaseUser} into {@link #returnIntent} with
     * <code>name</code>: "User" and exits with {@link #RESULT_OK} status</p>
     * <p>Upon a successful sign-in with a null user, exits with {@link #RESULT_CANCELED}.</p>
     * <p>Failure behaviour maintains the login page with a {@link Toast} popup.</p>
     *
     * @author u7327620 Ryan Foote
     */
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
            mAuth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(authResult -> {
                FirebaseUser user = authResult.getUser();
                if (user != null) {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_LONG).show();
                    returnIntent.putExtra("User", user);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
                else {
                    // Error case
                    Log.e("Login", "Successful login but user == null ?!");
                    setResult(RESULT_CANCELED);
                    finish();
                }
            })
            .addOnFailureListener(e -> Toast.makeText(this, "Login failed: "
                                  + e.getMessage(), Toast.LENGTH_LONG).show());
        });
    }
}