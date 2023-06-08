package com.cookmart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;

// Esta clase maneja la pantalla de inicio de la aplicación
public class SplashScreen extends AppCompatActivity {

    // SharedPreferences es utilizado para guardar datos en un archivo local en el dispositivo
    public static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Establecemos el color de la barra de estado de la aplicación
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.apptheme));

        // Inicializamos las SharedPreferences
        sharedPreferences = getSharedPreferences("sharedPreference", MODE_PRIVATE);

        // Creamos un contador para manejar el tiempo que la pantalla de inicio es visible
        new CountDownTimer(2500, 2500) {


            @Override
            public void onTick(long millisUntilFinished) {}

            // Esta función se llama cuando el contador termina
            @Override
            public void onFinish() {
                // Comprobamos si el usuario está autenticado con Firebase
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    // Si el usuario no está autenticado, le enviamos a la pantalla de autenticación
                    startActivity(new Intent(SplashScreen.this, CheckAuth.class));
                } else {
                    // Si el usuario está autenticado, le enviamos a la pantalla principal
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                }
                // Terminamos la actividad de la pantalla de inicio para que no vuelva a aparecer al presionar el botón de retroceso
                finish();
            }
        }.start();  // Iniciamos el contador
    }
}
