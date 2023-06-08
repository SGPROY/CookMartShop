package com.cookmart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

//import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterScreen extends AppCompatActivity {

    private EditText name, email, password;
    private ProgressDialog progressDialog;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Porfavor espera..");
        progressDialog.setMessage("Porfavor espera estamos registrandote");
        progressDialog.setCanceledOnTouchOutside(false);
        name = findViewById(R.id.register_name);
        email = findViewById(R.id.register_mail);
        password = findViewById(R.id.register_password);
        password.setTransformationMethod(new PasswordTransformationMethod());
        progressBar = findViewById(R.id.progressBar_register);
    }

    public void open_login_screen_from_register_screen(View view) {
        startActivity(new Intent(RegisterScreen.this, LoginScreen.class));
    }

    public void sign_up_user(View view) {
        if (!email.getText().toString().equals("") && !password.getText().toString().equals("") && !name.getText().toString().equals("")) {
            progressDialog.show();
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnSuccessListener(authResult -> {
                        progressDialog.show();
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("nombre", name.getText().toString());
                        hashMap.put("email", email.getText().toString());
                        FirebaseDatabase.getInstance().getReference("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(hashMap).addOnSuccessListener(aVoid -> {
                            progressDialog.cancel();
                            SplashScreen.sharedPreferences.edit().putString("nombre", hashMap.get("nombre")).apply();
                            SplashScreen.sharedPreferences.edit().putString("email", hashMap.get("email")).apply();
                            Toast.makeText(RegisterScreen.this, "Registrado Correctamente", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterScreen.this, MainActivity.class));
                        }).addOnFailureListener(e -> {
                            progressDialog.cancel();
                            Toast.makeText(RegisterScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }).addOnFailureListener(e -> {
                progressDialog.cancel();
                Toast.makeText(RegisterScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(this, "El campo en blanco no se puede procesar.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }




}