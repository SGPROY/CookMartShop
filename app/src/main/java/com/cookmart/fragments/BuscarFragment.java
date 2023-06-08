package com.cookmart.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cookmart.R;
import com.cookmart.adapters.productoHomeAdapter;
import com.cookmart.models.modelProducto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class BuscarFragment extends Fragment {

    private LinearLayout linearLayout;
    private RecyclerView recyclerView;
    private LinearLayout linearLayoutStatus;
    private ProgressBar progressBar;
    private productoHomeAdapter adapter;
    private ArrayList<modelProducto> arraylist;
    private EditText searchQuery;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_busqueda, container, false);
        arraylist = new ArrayList<>();
        progressBar = view.findViewById(R.id.progressBar5);
        progressBar.setVisibility(View.GONE);
        linearLayout = view.findViewById(R.id.frag_search_layout); // Agregar esta línea para referenciar correctamente el LinearLayout
        linearLayoutStatus = view.findViewById(R.id.frag_search_layout);
        searchQuery = view.findViewById(R.id.search_query_txt);
        searchQuery.setImeOptions(EditorInfo.IME_ACTION_GO);
        ImageView searchBtn = view.findViewById(R.id.imageView2);
        recyclerView = view.findViewById(R.id.searched_products_reccyler);
        adapter = new productoHomeAdapter(2, getContext(), arraylist);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);

        searchBtn.setOnClickListener(v -> {
            if (!searchQuery.getText().toString().equals("")) {
                getQueryResuslts();
            } else {
                Toast.makeText(getContext(), "El campo en blanco no se puede procesar.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }



    private void getQueryResuslts() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase.getInstance().getReference("productos")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            arraylist.clear();
                            linearLayout.setVisibility(View.INVISIBLE);
                            HashMap<String, HashMap<String, String>> main = (HashMap<String, HashMap<String, String>>) snapshot.getValue();
                            HashMap<String, String> child;
                            for (int i = 0; i < main.size(); i++) {
                                child = main.get(main.keySet().toArray()[i]);

                                if (child.get("titulo").toLowerCase().startsWith(searchQuery.getText().toString().toLowerCase())
                                        || child.get("titulo").toLowerCase().endsWith(searchQuery.getText().toString().toLowerCase())
                                        || child.get("descripcion").toLowerCase().contains(searchQuery.getText().toString().toLowerCase())
                                        || child.get("categoria").toLowerCase().contains(searchQuery.getText().toString().toLowerCase())) {
                                    arraylist.add(new modelProducto(child.get("imgMenor"), child.get("titulo"), child.get("precio"), child.get("unidad"), child.get("descripcion"), child.get("imgMayor"), main.keySet().toArray()[i] + ""));
                                }
                            }
                            if (arraylist.size() == 0) {
                                linearLayoutStatus.setVisibility(View.VISIBLE);
                            }
                            recyclerView.setVisibility(View.VISIBLE);
                            linearLayout.setVisibility(View.INVISIBLE);
                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}