package com.go4.application;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.appcompat.app.AppCompatActivity;
import com.go4.application.historical.SuburbHistoricalActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    FirebaseUser user;

    ActivityResultLauncher<Intent> getUser = registerForActivityResult(new StartActivityForResult(), r -> {
        if (r.getResultCode() == RESULT_OK){
            assert r.getData() != null;
            user = r.getData().getParcelableExtra("User", FirebaseUser.class);
        }});

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart(){
        super.onStart();
        //FirebaseAuth.getInstance().signOut();
        getUser.launch(new Intent(this, FirebaseLoginActivity.class));


        startActivity(new Intent(this, SuburbHistoricalActivity.class));
        finish();
    }
}
