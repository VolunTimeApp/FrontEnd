package com.voluntime.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterScreen extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private EditText emailInput, passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findViewById(R.id.move_to_login).setOnClickListener((View view) -> {
            Intent intent = new Intent(RegisterScreen.this, LoginScreen.class);
            startActivity(intent);
            finish();
        });

        Pattern emailPattern = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
        Pattern passwordPattern = Pattern.compile("([A-z]|[0-9])([A-z]|[0-9])([A-z]|[0-9])([A-z]|[0-9])+");

        findViewById(R.id.register_loading_bar).setVisibility(View.GONE);

        findViewById(R.id.register).setOnClickListener((View view) -> {
            findViewById(R.id.register_loading_bar).setVisibility(View.VISIBLE);
            findViewById(R.id.register).setEnabled(false);

            EditText emailInput = findViewById(R.id.register_email);
            EditText passwordInput = findViewById(R.id.register_password);

            String email = String.valueOf(emailInput.getText());
            String password = String.valueOf(passwordInput.getText());

            Matcher emailMatcher = emailPattern.matcher(email);
            Matcher passwordMatcher = passwordPattern.matcher(password);

            if(!emailMatcher.matches() || !passwordMatcher.matches()) {
                Toast.makeText(this, "Email or password are of wrong format", Toast.LENGTH_SHORT).show();
                findViewById(R.id.register).setEnabled(false);
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, (Task<AuthResult> task) -> {
                       if(task.isSuccessful()) {
                           Intent intent = new Intent(RegisterScreen.this, InfoScreen.class);
                           startActivity(intent);
                           finish();
                       } else {
                           Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show();
                           findViewById(R.id.register_loading_bar).setVisibility(View.GONE);
                           findViewById(R.id.register).setEnabled(true);
                       }
                    });
        });
    }
}