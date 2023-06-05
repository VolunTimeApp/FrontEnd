package com.voluntime.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashScreen extends AppCompatActivity {

    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance("europe-west1");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {
            redirectUser(user);
        } else {
            Intent intent = new Intent(SplashScreen.this, LoginScreen.class);
            startActivity(intent);
            finish();
        }
    }

    private void redirectUser(FirebaseUser user) {
        Map<String, Object> data = new HashMap<>();
        data.put("uid", user.getUid());

        mFunctions.getHttpsCallable("getVolunteerPosition").call(data)
                .continueWith((Task<HttpsCallableResult> task) -> {
                    if(task.isSuccessful()) {
                        String result = (String) task.getResult().getData();

                        if(result == null || new JSONObject(result).isNull("volunteerPosition")) {
                            Intent intent = new Intent(SplashScreen.this, InfoScreen.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(SplashScreen.this, HomeScreen.class);
                            startActivity(intent);
                            finish();
                        }

                        return result;
                    }

                    return null;
                });
    }
}