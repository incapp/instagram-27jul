package com.incapp.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextName;
    private Button buttonRegister;
    private ProgressBar progressBar;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private TextView textViewLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextEmail = findViewById(R.id.editText_email);
        editTextName = findViewById(R.id.editText_name);
        buttonRegister = findViewById(R.id.button_register);
        progressBar = findViewById(R.id.progressBar);
        editTextPassword = findViewById(R.id.editText_password);
        editTextConfirmPassword = findViewById(R.id.editText_confirm_password);
        textViewLogin = findViewById(R.id.textView_login);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void register() {
        final String name = editTextName.getText().toString();
        final String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();

        if (name.isEmpty()) {
            editTextName.setError("Required!");
            editTextName.requestFocus();
        } else if (email.isEmpty()) {
            editTextEmail.setError("Required!");
            editTextEmail.requestFocus();
        } else if (!email.matches(Patterns.EMAIL_ADDRESS.pattern())) {
            editTextEmail.setError("Invalid Email!");
            editTextEmail.requestFocus();
        } else if (password.isEmpty()) {
            editTextPassword.setError("Required!");
            editTextPassword.requestFocus();
        } else if (password.length() < 6) {
            editTextPassword.setError("Password too short!");
            editTextPassword.requestFocus();
        } else if (confirmPassword.isEmpty()) {
            editTextConfirmPassword.setError("Required!");
            editTextConfirmPassword.requestFocus();
        } else if (!password.equalsIgnoreCase(confirmPassword)) {
            editTextConfirmPassword.setError("Passwords do no match!");
            editTextConfirmPassword.requestFocus();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            buttonRegister.setVisibility(View.GONE);

            FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(RegisterActivity.this,
                                    "Login Now", Toast.LENGTH_SHORT).show();

                            //Step 2
                            UserProfileChangeRequest userProfileChangeRequest =
                                    new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name)
                                            .build();

                            authResult.getUser().updateProfile(userProfileChangeRequest);

                            authResult.getUser().sendEmailVerification();

                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            buttonRegister.setVisibility(View.VISIBLE);

                            Toast.makeText(RegisterActivity.this,
                                    e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
