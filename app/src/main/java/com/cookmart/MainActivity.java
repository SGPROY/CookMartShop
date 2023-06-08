package com.cookmart;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.cookmart.fragments.CuentaFragment;
import com.cookmart.fragments.CarritoFragment;
import com.cookmart.fragments.DespensaFragment;
import com.cookmart.fragments.HomeFragment;
import com.cookmart.fragments.PedidoFallido;
import com.cookmart.fragments.PedidoRealizado;
import com.cookmart.fragments.BuscarFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


    public static BottomNavigationView bottomNavigationView;

    long pressedTime;

    @Override
    public void onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            finishAffinity();
            System.exit(0);
        } else {
            Toast.makeText(getBaseContext(), "Presiona atras para salir", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_shop);
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> FirebaseDatabase.getInstance().getReference("usuarios")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("fcmToken").setValue(s));
        if (!Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).isEmailVerified()) {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
            View customLayout = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_verify_email_adress, null);
            bottomSheetDialog.setContentView(customLayout);
            bottomSheetDialog.show();
            CardView cardView = customLayout.findViewById(R.id.layout_verify_email_cardView);
            cardView.setOnClickListener(v -> FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(@NonNull Void aVoid) {
                    bottomSheetDialog.dismiss();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this, LoginScreen.class));
                    Toast.makeText(MainActivity.this, "Correo electrónico de verificación enviado con éxito.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show()));
        } else {
            FirebaseDatabase.getInstance().getReference("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("direccionesEnvio")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                View vobj = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_add_direccion, null);
                                builder.setView(vobj);
                                AlertDialog alertDialog = builder.create();
                                alertDialog.setCanceledOnTouchOutside(false);
                                new CountDownTimer(5000, 5000) {

                                    @Override
                                    public void onTick(long millisUntilFinished) {

                                    }

                                    @Override
                                    public void onFinish() {
                                        alertDialog.show();
                                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        CardView cancelBtn = vobj.findViewById(R.id.dialog_add_cancelBtn);
                                        CardView addBtn = vobj.findViewById(R.id.dialog_add_addBtn);
                                        TextView name = vobj.findViewById(R.id.dialog_user_name);
                                        name.setText(SplashScreen.sharedPreferences.getString("nombre", "Nombre"));
                                        cancelBtn.setOnClickListener(v -> alertDialog.cancel());
                                        addBtn.setOnClickListener(v -> {
                                            alertDialog.cancel();
                                            Intent intent = new Intent(MainActivity.this, AddEditDireccion.class);
                                            intent.putExtra("IdDireccion", UUID.randomUUID().toString());
                                            startActivity(intent);
                                        });
                                    }
                                }.start();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


        }
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            String deviceToken = task.getResult();
            FirebaseDatabase.getInstance().getReference("usuarios")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("fcmToken").setValue(deviceToken);
        });

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        if (getIntent().getStringExtra("from") != null) {
            if (getIntent().getStringExtra("from").equals("fromPaymentListenerClassSuccess")) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.frame_layout, new PedidoRealizado())
                        .commit();
            } else {
                getSupportFragmentManager()
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.frame_layout, new PedidoFallido())
                        .commit();
            }

        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.frame_layout, new HomeFragment())
                    .commit();
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_shop:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.frame_layout, new HomeFragment())
                            .commit();
                    return true;
                case R.id.nav_search:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.frame_layout, new BuscarFragment())
                            .commit();
                    return true;
                case R.id.nav_despensa:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.frame_layout, new DespensaFragment())
                            .commit();
                    return true;
                case R.id.nav_cart:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.frame_layout, new CarritoFragment())
                            .commit();
                    return true;
                case R.id.nav_account:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.frame_layout, new CuentaFragment())
                            .commit();
                    return true;
                default:
                    return false;
            }
        });
    }


}