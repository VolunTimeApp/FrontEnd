package com.voluntime.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class HomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViewById(R.id.logout).setOnClickListener((View view) -> {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(HomeScreen.this, LoginScreen.class);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.explore).setOnClickListener((View view) -> {
            Intent intent = new Intent(HomeScreen.this, InfoScreen.class);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.simulate).setOnClickListener((View view) -> {
            Intent intent = new Intent(HomeScreen.this, SimulationScreen.class);
            startActivity(intent);
        });
    }
}
