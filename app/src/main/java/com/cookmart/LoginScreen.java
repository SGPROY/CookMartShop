package com.cookmart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginScreen extends AppCompatActivity {

    private EditText email, password;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        email = findViewById(R.id.login_mail);
        password = findViewById(R.id.login_password);
        password.setTransformationMethod(new PasswordTransformationMethod());
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Porfavor espera..");
        progressDialog.setMessage("Porfavor espera estamos registrandote");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    public void open_register_screen(View view) {
        startActivity(new Intent(LoginScreen.this, RegisterScreen.class));
    }

    public void login_user(View view) {
        if (!email.getText().toString().equals("") && !password.getText().toString().equals("")) {
            progressDialog.show();
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnSuccessListener(authResult -> FirebaseDatabase.getInstance().getReference("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        HashMap<String, String> hashMap = (HashMap<String, String>) snapshot.getValue();
                                        SplashScreen.sharedPreferences.edit().putString("nombre", hashMap.get("nombre")).apply();
                                        SplashScreen.sharedPreferences.edit().putString("email", hashMap.get("email")).apply();
                                        if (hashMap.get("imagenPerfil") != null) {
                                            SplashScreen.sharedPreferences.edit().putString("profile", hashMap.get("profile")).apply();
                                        }
                                        progressDialog.cancel();
                                        Toast.makeText(LoginScreen.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginScreen.this, MainActivity.class));
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    progressDialog.cancel();
                                    Toast.makeText(LoginScreen.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            })).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.cancel();
                    Toast.makeText(LoginScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "El campo en blanco no se puede procesar.", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }



    public void forgot_password(View view) {
        startActivity(new Intent(LoginScreen.this,ForgotPassword.class));
    }
}