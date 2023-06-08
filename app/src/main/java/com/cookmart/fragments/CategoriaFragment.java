package com.cookmart.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cookmart.R;
import com.cookmart.SplashScreen;
import com.cookmart.adapters.productoHomeAdapter;
import com.cookmart.models.modelProducto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class CategoriaFragment extends Fragment {

    private RecyclerView recyclerView;
    private productoHomeAdapter adapter;
    private LinearLayout linearLayout;
    private ArrayList<modelProducto> arrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_categoria, container, false);
        recyclerView = view.findViewById(R.id.category_search_recycler);
        arrayList = new ArrayList<>();
        linearLayout = view.findViewById(R.id.frag_category_linear_layout);
        TextView heading = view.findViewById(R.id.textView38);
        heading.setText(SplashScreen.sharedPreferences.getString("categoryHome", "frutasVerduras"));
        ImageView backBtn = view.findViewById(R.id.back_btn_category_fragment);
        backBtn.setOnClickListener(v -> requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.frame_layout, new HomeFragment())
                .commit());
        adapter = new productoHomeAdapter(2, getContext(), arrayList);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);
        getResults();
        return view;
    }

    private void getResults() {
        FirebaseDatabase.getInstance().getReference("productos")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            HashMap<String, HashMap<String, String>> main = (HashMap<String, HashMap<String, String>>) snapshot.getValue();
                            HashMap<String, String> child;
                            arrayList.clear();
                            linearLayout.setVisibility(View.INVISIBLE);
                            for (int i = 0; i < main.size(); i++) {
                                child = main.get(main.keySet().toArray()[i]);
                                if (Objects.requireNonNull(child.get("categoria")).contains(SplashScreen.sharedPreferences.getString("categoryHome", "frutasVerduras"))) {
                                    arrayList.add(new modelProducto(child.get("imgMenor"), child.get("titulo"), child.get("precio"), child.get("unidad"), child.get("descripcion"), child.get("imgMayor"), main.keySet().toArray()[i] + ""));
                                } else if (SplashScreen.sharedPreferences.getString("categoryHome", "frutasVerduras").equals("all")) {
                                    arrayList.add(new modelProducto(child.get("imgMenor"), child.get("titulo"), child.get("precio"), child.get("unidad"), child.get("descripcion"), child.get("imgMayor"), main.keySet().toArray()[i] + ""));
                                }
                            }
                            if (arrayList.size() == 0) {
                                linearLayout.setVisibility(View.VISIBLE);
                            }
                            recyclerView.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}