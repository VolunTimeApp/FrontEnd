package com.voluntime.app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

public class InfoScreen extends AppCompatActivity {

    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance("europe-west1");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(InfoScreen.this, LoginScreen.class);
            startActivity(intent);
            finish();
        }

        EditText ageInput = findViewById(R.id.age);
        EditText hobbiesInput = findViewById(R.id.hobbies);
        EditText locationInput = findViewById(R.id.location);

        TextView availabilityValue = findViewById(R.id.availabilityValue);
        SeekBar availabilityInput = findViewById(R.id.availability);

        availabilityInput.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                availabilityValue.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        findViewById(R.id.info_loading_bar).setVisibility(View.GONE);

        findViewById(R.id.generate).setOnClickListener((View view) -> {
            if(hobbiesInput.getText().toString().isEmpty() || locationInput.getText().toString().isEmpty()
                    || ageInput.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please provide age, hobbies and your location", Toast.LENGTH_SHORT).show();
                return;
            }

            findViewById(R.id.info_loading_bar).setVisibility(View.VISIBLE);
            findViewById(R.id.generate).setEnabled(false);

            int age = Integer.parseInt(ageInput.getText().toString());
            String hobbies = hobbiesInput.getText().toString();
            String location = locationInput.getText().toString();
            int availability = availabilityInput.getProgress();

            Map<String, Object> data = new HashMap<>();
            data.put("age", age);
            data.put("location", location);
            data.put("availability", availability);
            data.put("hobbies", hobbies);

            mFunctions.getHttpsCallable("generateVolunteers").call(data)
                    .continueWith((Task<HttpsCallableResult> task) -> {
                        if(task.isSuccessful()) {
                            String result = (String) task.getResult().getData();

                            Intent intent = new Intent(InfoScreen.this, OptionsScreen.class);
                            intent.putExtra("data", result);
                            startActivity(intent);
                            finish();

                            return result;
                        } else {
                            Toast.makeText(this, "Something went wrong with finding volunteer positions :(", Toast.LENGTH_SHORT).show();
                            findViewById(R.id.info_loading_bar).setVisibility(View.GONE);
                            findViewById(R.id.generate).setEnabled(true);
                        }

                        return null;
                    });
        });
    }
}