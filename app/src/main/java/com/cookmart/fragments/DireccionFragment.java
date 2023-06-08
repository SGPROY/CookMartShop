package com.cookmart.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cookmart.AddEditDireccion;
import com.cookmart.R;
import com.cookmart.adapters.addressSettingsAdapter;
import com.cookmart.models.modelDireccion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class DireccionFragment extends Fragment {

    private EditText searchQuery;
    private addressSettingsAdapter adapter;
    private ArrayList<modelDireccion> arrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el diseño de este fragmento
        View view = inflater.inflate(R.layout.fragment_direccion, container, false);

        arrayList = new ArrayList<>();
        ImageView searchBtn = view.findViewById(R.id.frag_address_search_btn);
        ImageView backBtn = view.findViewById(R.id.imageView7);
        searchQuery = view.findViewById(R.id.searchEditTextAddress);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_delivery_address);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        CardView addAddressBtn = view.findViewById(R.id.fragment_address_add_address_btn);
        adapter = new addressSettingsAdapter(getContext(), arrayList);
        recyclerView.setAdapter(adapter);

        getAddress(""); // Obtener todas las direcciones al inicio

        searchBtn.setOnClickListener(v -> getAddress(searchQuery.getText().toString()));

        addAddressBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddEditDireccion.class);
            intent.putExtra("IdDireccion", UUID.randomUUID().toString());
            startActivity(intent);
        });

        backBtn.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.frame_layout, new CuentaFragment())
                    .commit();
        });

        return view;
    }

    private void getAddress(String address) {
        FirebaseDatabase.getInstance().getReference("usuarios")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child("direccionesEnvio")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        arrayList.clear();
                        if (snapshot.exists()) {
                            HashMap<String, HashMap<String, String>> hashMap = (HashMap<String, HashMap<String, String>>) snapshot.getValue();
                            HashMap<String, String> child;

                            for (int i = 0; i < hashMap.size(); i++) {
                                child = hashMap.get(hashMap.keySet().toArray()[i]);

                                // Filtrar las direcciones según la búsqueda
                                if (child.get("nombre").contains(address) || child.get("codpostal").equals(address) || child.get("direccion").contains(address)) {
                                    arrayList.add(new modelDireccion(
                                            child.get("ciudad"),
                                            child.get("nombre"),
                                            (String) hashMap.keySet().toArray()[i],
                                            child.get("codpostal"),
                                            child.get("direccion"),
                                            child.get("landmark"),
                                            child.get("numeroTlf")
                                    ));
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
