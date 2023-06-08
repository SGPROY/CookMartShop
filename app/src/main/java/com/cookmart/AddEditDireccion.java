package com.cookmart;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddEditDireccion extends AppCompatActivity {

    private TextInputEditText name, addressLine1, city, pincode, phoneNumber;
    private ProgressBar progressBar;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_direccion);

        try {
            // Obtener referencias a los elementos de la interfaz de usuario
            name = findViewById(R.id.add_edit_nombre);
            addressLine1 = findViewById(R.id.add_edit_direccion);
            city = findViewById(R.id.add_edit_ciudad);
            progressBar = findViewById(R.id.progressBar_editAddress);
            pincode = findViewById(R.id.add_edit_codpostal);
            phoneNumber = findViewById(R.id.add_edit_telefono);

            // Establecer los valores de los campos de texto según los datos recibidos en el Intent
            name.setText(getIntent().getStringExtra("nombre"));
            addressLine1.setText(getIntent().getStringExtra("direccion"));
            city.setText(getIntent().getStringExtra("ciudad"));
            pincode.setText(getIntent().getStringExtra("codpostal"));
            phoneNumber.setText(getIntent().getStringExtra("numeroTlf"));

            CardView saveBtn = findViewById(R.id.add_edit_save_btn);
            saveBtn.setOnClickListener(v -> {
                if (esValido()) {
                    progressBar.setVisibility(View.VISIBLE);

                    // Verificar el código de área en la base de datos antes de guardar la dirección
                    FirebaseDatabase.getInstance().getReference("codpostal")
                            .child(pincode.getText().toString())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        try {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            HashMap<String, String> hashMap = new HashMap<>();
                                            hashMap.put("nombre", Objects.requireNonNull(name.getText()).toString());
                                            hashMap.put("direccion", Objects.requireNonNull(addressLine1.getText()).toString());
                                            hashMap.put("ciudad", Objects.requireNonNull(city.getText()).toString());
                                            hashMap.put("codpostal", pincode.getText().toString());
                                            hashMap.put("numeroTlf", Objects.requireNonNull(phoneNumber.getText()).toString());
                                            progressBar.setVisibility(View.VISIBLE);

                                            // Guardar la dirección en la base de datos de Firebase
                                            FirebaseDatabase.getInstance().getReference("usuarios")
                                                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                                    .child("direccionesEnvio").child(getIntent().getStringExtra("IdDireccion"))
                                                    .setValue(hashMap).addOnSuccessListener(aVoid -> {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                startActivity(new Intent(AddEditDireccion.this, MainActivity.class));
                                                Toast.makeText(AddEditDireccion.this, "Guardado exitosamente", Toast.LENGTH_SHORT).show();
                                            });
                                        } catch (Exception e) {
                                            Toast.makeText(AddEditDireccion.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(AddEditDireccion.this, "Lo sentimos, no realizamos entregas a este código postal.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(AddEditDireccion.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(AddEditDireccion.this);
    }

    // Obtener la ubicación actual del usuario
    private void obtenerMiUbicacion() {
        if (ActivityCompat.checkSelfPermission(AddEditDireccion.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(AddEditDireccion.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(location -> {
                if (location != null) {
                    Geocoder geocoder = new Geocoder(AddEditDireccion.this, Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (addresses != null) {
                        Address returnaddress = addresses.get(0);
                        pincode.setText(returnaddress.getPostalCode());
                        addressLine1.setText(returnaddress.getAddressLine(0));
                        city.setText(returnaddress.getLocality());
                        name.setText(SplashScreen.sharedPreferences.getString("nombre", "Nombre Apellidos"));
                    }
                } else {
                    Toast.makeText(this, "¡UPS! Hubo un problema al encontrar tu ubicación. Por favor, ingrésala manualmente.", Toast.LENGTH_SHORT).show();
                }

            }).addOnFailureListener(e -> {
                Toast.makeText(AddEditDireccion.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    // Verificar si los campos de texto contienen valores válidos
    private boolean esValido() {
        boolean temp = true;
        if (name.getText().toString().equals("")) {
            temp = false;
            Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
        } else if (Objects.requireNonNull(addressLine1.getText()).toString().equals("")) {
            temp = false;
            Toast.makeText(this, "La línea de dirección 1 no puede estar vacía.", Toast.LENGTH_SHORT).show();
        } else if (Objects.requireNonNull(city.getText()).toString().equals("")) {
            temp = false;
            Toast.makeText(this, "La ciudad no puede estar vacía.", Toast.LENGTH_SHORT).show();
        } else if (Objects.requireNonNull(pincode.getText()).toString().equals("")) {
            temp = false;
            Toast.makeText(this, "El código postal no puede estar vacío.", Toast.LENGTH_SHORT).show();
        } else if (Objects.requireNonNull(phoneNumber.getText()).toString().equals("")) {
            temp = false;
            Toast.makeText(this, "El número de teléfono no puede estar vacío.", Toast.LENGTH_SHORT).show();
        }
        return temp;
    }

    public void btnUbicarme(View view) {
        Dexter.withContext(AddEditDireccion.this).withPermission(
                Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                obtenerMiUbicacion();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }
}
