package com.voluntime.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulationScreen extends AppCompatActivity {

    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance("europe-west1");

    private LinearLayout messageContainer;
    private EditText messageEditText;
    private TextView sendButton;

    private JSONArray messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation);

        messageContainer = findViewById(R.id.messageContainer);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.send);

        messages = new JSONArray();

        sendButton.setOnClickListener((View view) -> {
            String messageContent = messageEditText.getText().toString();

            if (!messageContent.isEmpty()) {
                sendButton.setEnabled(false);

                JSONObject message = new JSONObject();
                try {
                    message.put("role", "user");
                    message.put("content", messageContent);
                    messages.put(message);
                    displayMessage(message);
                    messageEditText.setText("");

                    askForResponse();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void displayMessage(JSONObject message) throws JSONException {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        int margin = getResources().getDimensionPixelSize(R.dimen.chat_bubble_margin);
        int padding = getResources().getDimensionPixelSize(R.dimen.chat_bubble_padding);

        TextView textView = new TextView(this);
        textView.setText(message.getString("content"));
        textView.setTextColor(Color.WHITE);
        textView.setPadding(padding, padding, padding, padding);

        if (message.getString("role").equals("user")) {
            textView.setBackgroundResource(R.drawable.dark_button);
            layoutParams.gravity = Gravity.END;
        } else {
            textView.setBackgroundResource(R.drawable.chat_bubble_other);
            textView.setTextColor(Color.BLACK);
            layoutParams.gravity = Gravity.START;
        }

        layoutParams.setMargins(margin, margin, margin, margin);
        textView.setLayoutParams(layoutParams);

        messageContainer.addView(textView);
    }

    private void askForResponse() {
        // Asking GPT for a response:
        Map<String, Object> volunteerPositionData = new HashMap<>();
        volunteerPositionData.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());

        mFunctions.getHttpsCallable("getVolunteerPosition").call(volunteerPositionData)
                .continueWith((Task<HttpsCallableResult> task) -> {
                    if(task.isSuccessful()) {
                        String result = (String) task.getResult().getData();

                        if(result == null || new JSONObject(result).isNull("volunteerPosition")) {
                            sendButton.setEnabled(true);
                            return null;
                        } else {
                            String volunteerPosition = new JSONObject(result).getString("volunteerPosition");

                            Map<String, Object> data = new HashMap<>();
                            data.put("volunteerPosition", volunteerPosition);
                            data.put("messages", this.messages.toString());

                            mFunctions.getHttpsCallable("simulate").call(data)
                                    .continueWith((Task<HttpsCallableResult> simulateTask) -> {
                                        sendButton.setEnabled(true);

                                        if(simulateTask.isSuccessful()) {
                                            String simulateResult = (String) simulateTask.getResult().getData();

                                            JSONObject jsonObject = new JSONObject(simulateResult);
                                            JSONObject response = jsonObject.getJSONObject("response");
                                            this.messages.put(response);
                                            displayMessage(response);

                                            return result;
                                        } else {
                                            return null;
                                        }
                                    });
                        }

                        return result;
                    }

                    return null;
                });
    }
}