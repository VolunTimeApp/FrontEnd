package com.voluntime.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginScreen extends AppCompatActivity {

    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance("europe-west1");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.move_to_login).setOnClickListener((View view) -> {
            Intent intent = new Intent(LoginScreen.this, RegisterScreen.class);
            startActivity(intent);
            finish();
        });

        Pattern emailPattern = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
        Pattern passwordPattern = Pattern.compile("([A-z]|[0-9])([A-z]|[0-9])([A-z]|[0-9])([A-z]|[0-9])+");

        findViewById(R.id.login_loading_bar).setVisibility(View.GONE);

        findViewById(R.id.login).setOnClickListener((View view) -> {
            findViewById(R.id.login_loading_bar).setVisibility(View.VISIBLE);
            findViewById(R.id.login).setEnabled(false);

            EditText emailInput = findViewById(R.id.login_email);
            EditText passwordInput = findViewById(R.id.login_password);

            String email = String.valueOf(emailInput.getText());
            String password = String.valueOf(passwordInput.getText());

            Matcher emailMatcher = emailPattern.matcher(email);
            Matcher passwordMatcher = passwordPattern.matcher(password);

            if(!emailMatcher.matches() || !passwordMatcher.matches()) {
                Toast.makeText(this, "Email or password are of wrong format", Toast.LENGTH_SHORT).show();
                findViewById(R.id.login).setEnabled(true);
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, (Task<AuthResult> task) -> {
                       if(task.isSuccessful() && task.getResult().getUser() != null) {
                           redirectUser(task.getResult().getUser());
                       } else {
                           Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                           findViewById(R.id.login).setEnabled(true);
                       }
                    });
        });
    }

    private void redirectUser(FirebaseUser user) {
        Map<String, Object> data = new HashMap<>();
        data.put("uid", user.getUid());

        mFunctions.getHttpsCallable("getVolunteerPosition").call(data)
                .continueWith((Task<HttpsCallableResult> task) -> {
                    if(task.isSuccessful()) {
                        String result = (String) task.getResult().getData();

                        if(result == null || new JSONObject(result).isNull("volunteerPosition")) {
                            Intent intent = new Intent(LoginScreen.this, InfoScreen.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(LoginScreen.this, HomeScreen.class);
                            startActivity(intent);
                            finish();
                        }

                        return result;
                    }

                    return null;
                });
    }
}