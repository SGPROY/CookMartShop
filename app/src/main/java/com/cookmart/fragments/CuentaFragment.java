package com.cookmart.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.cookmart.CheckAuth;
import com.cookmart.R;
import com.cookmart.SplashScreen;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class CuentaFragment extends Fragment {


    private ProgressDialog progressDialog;
    private ImageView profile;
    private Uri imagePath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        CardView logout_btn = view.findViewById(R.id.account_logout_btn);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Espere mientras estamos obteniendo los datos.");
        progressDialog.setCanceledOnTouchOutside(false);
        TextView name = view.findViewById(R.id.frag_account_name);
        TextView email = view.findViewById(R.id.frag_account_email);
        name.setText(SplashScreen.sharedPreferences.getString("nombre", "Nombre"));
        email.setText(SplashScreen.sharedPreferences.getString("email", "correo@gmail.com"));
        profile = view.findViewById(R.id.frag_account_profileBtn);
        CardView changePictureBtn = view.findViewById(R.id.vector_profile_editBtn);
        ConstraintLayout aboutUsBtn = view.findViewById(R.id.account_delivery_about_btn);
        ConstraintLayout contactUsBtn = view.findViewById(R.id.account_delivery_contact_btn);
        ConstraintLayout deliveryAddressBtn = view.findViewById(R.id.account_delivery_address_btn);
        ConstraintLayout ordersBtn = view.findViewById(R.id.account_orders_btn);
        ConstraintLayout notificationBtn = view.findViewById(R.id.frag_account_notificationBtn);
        FirebaseDatabase.getInstance().getReference("usuarios")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("imagenPerfil").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String profileImage = (String) snapshot.getValue();
                    Picasso.get().load(profileImage).placeholder(R.drawable.sliding_image_two).into(profile);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        changePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(getContext()).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent i = new Intent();
                        i.setType("image/*");
                        i.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(i, "Selecciona tu imagen de perfil"), 131);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }

                }).check();
            }
        });
        notificationBtn.setOnClickListener(v -> requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.frame_layout, new NotificacionFragment())
                .commit());
        aboutUsBtn.setOnClickListener(v -> requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.frame_layout, new AboutFragment())
                .commit());
        contactUsBtn.setOnClickListener(v -> {
            progressDialog.show();
            FirebaseDatabase.getInstance().getReference("storeInfo")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                progressDialog.cancel();
                                HashMap<String, String> hashMap = (HashMap<String, String>) snapshot.getValue();
                                Intent email = new Intent(Intent.ACTION_SENDTO);
                                email.setData(Uri.parse("mailto:" + hashMap.get("storeEmail")));
                                email.putExtra(Intent.EXTRA_SUBJECT, "Consulta");
                                email.putExtra(Intent.EXTRA_TEXT, "Por favor no dude en contactar con nosotros en cualquier momento.");
                                startActivity(email);
                            } else {
                                progressDialog.cancel();
                                Toast.makeText(getContext(), "OH, Ha habido algun error.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        });
        deliveryAddressBtn.setOnClickListener(v -> requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.frame_layout, new DireccionFragment())
                .commit());
        ordersBtn.setOnClickListener(v -> requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.frame_layout, new PedidosFragment())
                .commit());
        logout_btn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getActivity(), "Se ha cerrado sesión correctamente", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), CheckAuth.class));
        });
        return view;
    }

    private void uploadPictureToDb() {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Porfavor espera...");
        progressDialog.setMessage("Estamos actualizando tus datos.");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        FirebaseStorage.getInstance().getReference("usuarios").child("imagenPerfil")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .putFile(imagePath).addOnCompleteListener(task -> FirebaseStorage.getInstance().getReference("usuarios").child("imagenPerfil")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .getDownloadUrl().addOnSuccessListener(uri -> {
                            FirebaseDatabase.getInstance().getReference("usuarios")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("imagenPerfil").setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(@NonNull Void aVoid) {
                                    progressDialog.cancel();
                                    Toast.makeText(getContext(), "Actualizado Correctamente.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 131 && resultCode == RESULT_OK && data != null) {
            imagePath = data.getData();
            try {
                Bitmap b = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imagePath);
                uploadPictureToDb();
                profile.setImageBitmap(b);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}