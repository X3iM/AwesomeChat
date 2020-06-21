package com.android.greena.awesomechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";

    private FirebaseAuth    auth;

    private EditText        emailEditText;
    private EditText        passEditText;
    private EditText        checkPassEditText;
    private EditText        nameEditText;
    private TextView        toggleLoginSingUpTextView;
    private Button          signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        auth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.emailEditText);
        passEditText = findViewById(R.id.passEditText);
        checkPassEditText = findViewById(R.id.checkPassEditText);
        nameEditText = findViewById(R.id.nameEditText);
        toggleLoginSingUpTextView = findViewById(R.id.toggleLoginSingUpTextView);
        signUpButton = findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String userName = nameEditText.getText().toString();
                String password = passEditText.getText().toString();
                String checkPassword = checkPassEditText.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(userName)) {
                    if (password.equals(checkPassword))
                        loginSignUpUser(email, passEditText.getText().toString().trim());
                    else
                        Toast.makeText(SignUpActivity.this, "Password is not match", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SignUpActivity.this, "Please add your username and E-mail", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loginSignUpUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Authentication success.", Toast.LENGTH_LONG).show();
                            FirebaseUser user = auth.getCurrentUser();
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            //updateUI(user);
                        } else {
                            Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_LONG).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void toggleLoginMode(View view) {
        startActivity(new Intent(this, LogInActivity.class));
    }
}