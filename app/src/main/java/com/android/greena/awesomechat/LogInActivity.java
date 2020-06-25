package com.android.greena.awesomechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {

    private FirebaseAuth    auth;

    private EditText        emailEditText;
    private EditText        passEditText;
    private EditText        nameEditText;
    private Button          logInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        auth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.emailEditText);
        passEditText = findViewById(R.id.passEditText);
        nameEditText = findViewById(R.id.nameEditText);
        logInButton = findViewById(R.id.logInButton);

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String userName = nameEditText.getText().toString();
                String password = passEditText.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)) {
                    logIn(email, password);
                } else {
                    Toast.makeText(LogInActivity.this, "Please fill in all fields", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void logIn(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LogInActivity.this, "Authentication success", Toast.LENGTH_LONG).show();
                            FirebaseUser user = auth.getCurrentUser();
                            startActivity(new Intent(LogInActivity.this, UserListActivity.class));
//                            updateUI(user);
                        } else {
                            Toast.makeText(LogInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                            // ...
                        }

                        // ...
                    }
                });
    }
}