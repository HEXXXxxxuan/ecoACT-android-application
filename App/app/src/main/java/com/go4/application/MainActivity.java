package com.go4.application;

import com.firebase.ui.auth.viewmodel.RequestCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.activity.result.contract.ActivityResultContracts.GetContent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Objects;

import javax.xml.transform.Result;

public class MainActivity extends AppCompatActivity {
    Button loginButton;
    Button logoutButton;
    FirebaseUser user;
    FirebaseAuth mAuth;

    ActivityResultLauncher<Intent> getUser = registerForActivityResult(
            new StartActivityForResult(),
            r -> {
                if(r.getResultCode()== MainActivity.RESULT_OK){
                    user = FirebaseAuth.getInstance().getCurrentUser();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        logoutButton = findViewById(R.id.button_logout);
        loginButton = findViewById(R.id.button_login);

        loginButton.setOnClickListener(view ->
                getUser.launch(new Intent(this, FirebaseLoginActivity.class)));
        logoutButton.setOnClickListener(view->{
            mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            Toast.makeText(this, "Successfully logged out", Toast.LENGTH_SHORT).show();
        });
    }
}