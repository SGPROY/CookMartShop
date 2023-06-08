package com.cookmart.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cookmart.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


public class AboutFragment extends Fragment {

    private TextView location, about, contact;
    private ProgressDialog progressDialog;
    private ImageView storeImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        location = view.findViewById(R.id.frag_about_locations);
        about = view.findViewById(R.id.frag_about_store_about);
        contact = view.findViewById(R.id.frag_about_contact_us);
        progressDialog = new ProgressDialog(getContext());
        storeImage = view.findViewById(R.id.frag_about_store_image);
        progressDialog.setMessage("Espere mientras estamos obteniendo los datos.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.cancel();
        progressDialog.show();
        getStoreAboutDetails();
        return view;
    }

    private void getStoreAboutDetails() {
        FirebaseDatabase.getInstance().getReference("storeInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HashMap<String, String> hashMap = (HashMap<String, String>) snapshot.getValue();
                    location.setText(hashMap.get("storeAddress"));
                    about.setText(hashMap.get("about"));
                    Picasso.get().load(hashMap.get("storeImage")).placeholder(R.drawable.store_image_url).into(storeImage);
                    contact.setText(hashMap.get("Email : " + hashMap.get("storeEmail")));
                } else {
                    Toast.makeText(getContext(), "Estará actualizada pronto", Toast.LENGTH_SHORT).show();
                }
                progressDialog.cancel();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}