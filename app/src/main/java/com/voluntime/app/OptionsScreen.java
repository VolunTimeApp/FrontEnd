package com.voluntime.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OptionsScreen extends AppCompatActivity {

    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance("europe-west1");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        findViewById(R.id.option_loading_bar).setVisibility(View.GONE);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String result = extras.getString("data");

            try {
                JSONObject jsonObject = new JSONObject(result).getJSONObject("volunteerPositions");

                TextView title = findViewById(R.id.title1);
                title.setText(jsonObject.getJSONObject("1").getString("name"));

                TextView desc = findViewById(R.id.desc1);
                desc.setText(jsonObject.getJSONObject("1").getString("desc"));

                TextView join = findViewById(R.id.join1);
                join.setOnClickListener((View view) -> {
                    try {
                        joinVolunteerPosition(jsonObject.getJSONObject("1").getString("name"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });

                title = findViewById(R.id.title2);
                title.setText(jsonObject.getJSONObject("2").getString("name"));

                desc = findViewById(R.id.desc2);
                desc.setText(jsonObject.getJSONObject("2").getString("desc"));

                join = findViewById(R.id.join2);
                join.setOnClickListener((View view) -> {
                    try {
                        joinVolunteerPosition(jsonObject.getJSONObject("2").getString("name"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });

                title = findViewById(R.id.title3);
                title.setText(jsonObject.getJSONObject("3").getString("name"));

                desc = findViewById(R.id.desc3);
                desc.setText(jsonObject.getJSONObject("3").getString("desc"));

                join = findViewById(R.id.join3);
                join.setOnClickListener((View view) -> {
                    try {
                        joinVolunteerPosition(jsonObject.getJSONObject("3").getString("name"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });

                title = findViewById(R.id.title4);
                title.setText(jsonObject.getJSONObject("4").getString("name"));

                desc = findViewById(R.id.desc4);
                desc.setText(jsonObject.getJSONObject("4").getString("desc"));

                join = findViewById(R.id.join4);
                join.setOnClickListener((View view) -> {
                    try {
                        joinVolunteerPosition(jsonObject.getJSONObject("4").getString("name"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });

                title = findViewById(R.id.title5);
                title.setText(jsonObject.getJSONObject("5").getString("name"));

                desc = findViewById(R.id.desc5);
                desc.setText(jsonObject.getJSONObject("5").getString("desc"));

                join = findViewById(R.id.join5);
                join.setOnClickListener((View view) -> {
                    try {
                        joinVolunteerPosition(jsonObject.getJSONObject("5").getString("name"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void joinVolunteerPosition(String volunteerName) {
        findViewById(R.id.option_loading_bar).setVisibility(View.VISIBLE);

        findViewById(R.id.join1).setClickable(false);
        findViewById(R.id.join2).setClickable(false);
        findViewById(R.id.join3).setClickable(false);
        findViewById(R.id.join4).setClickable(false);
        findViewById(R.id.join5).setClickable(false);

        Map<String, Object> data = new HashMap<>();
        data.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        data.put("volunteerPosition", volunteerName);

        mFunctions.getHttpsCallable("setVolunteerPosition").call(data)
                .continueWith((Task<HttpsCallableResult> task) -> {
                    if(task.isSuccessful()) {
                        String result = (String) task.getResult().getData();

                        Intent intent = new Intent(OptionsScreen.this, HomeScreen.class);
                        startActivity(intent);
                        finish();

                        return result;
                    } else {
                        Toast.makeText(this, "Something went wrong with joining this position :(", Toast.LENGTH_SHORT).show();

                        findViewById(R.id.option_loading_bar).setVisibility(View.GONE);

                        findViewById(R.id.join1).setClickable(true);
                        findViewById(R.id.join2).setClickable(true);
                        findViewById(R.id.join3).setClickable(true);
                        findViewById(R.id.join4).setClickable(true);
                        findViewById(R.id.join5).setClickable(true);
                    }

                    return null;
                });
    }
}