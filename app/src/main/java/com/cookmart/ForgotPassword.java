package com.cookmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText email;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        email = findViewById(R.id.forgot_email);
        progressBar = findViewById(R.id.progressBar4);
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
    }

    public void send_forgot_password_email(View view) {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString())
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(ForgotPassword.this, "Check your inbox to see the Password Reset Email.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ForgotPassword.this, LoginScreen.class));
                }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(ForgotPassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    public void open_login_Screen(View view) {
        startActivity(new Intent(ForgotPassword.this, LoginScreen.class));
    }
}