package com.cookmart.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cookmart.R;
import com.cookmart.SplashScreen;
import com.cookmart.adapters.homeAdapterShimmer;
import com.cookmart.adapters.productoHomeAdapter;
import com.cookmart.models.modelProducto;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class HomeFragment extends Fragment {

    private ArrayList<modelProducto> latestproductarraylist;
    private productoHomeAdapter latestProductAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        EditText editText = view.findViewById(R.id.editText);
        editText.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.frame_layout, new BuscarFragment())
                    .commit();
        });
        CardView searchBtn = view.findViewById(R.id.cardView_search_main);
        ConstraintLayout searchBtnConstraint = view.findViewById(R.id.constraint_serach_btn);
        TextView name = view.findViewById(R.id.frag_home_name);
        TextView seeAllBtn = view.findViewById(R.id.frag_home_see_all_btn);
        searchBtnConstraint.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.frame_layout, new BuscarFragment())
                    .commit();
        });
        try {
            name.setText("Hola, " + SplashScreen.sharedPreferences.getString("nombre", "").split(" ")[0]);

        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        searchBtn.setOnClickListener(v -> requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.frame_layout, new BuscarFragment())
                .commit());
        MaterialCardView dairyMaterialCardView = view.findViewById(R.id.category_dairy_products_btn);
        MaterialCardView fruitsMaterialCardView = view.findViewById(R.id.category_fruits_veges_btn);
        MaterialCardView bakeryMaterialCardView = view.findViewById(R.id.category_bakery_snacks_btn);
        MaterialCardView beveragesMaterialCardView = view.findViewById(R.id.category_beverages_btn);
        MaterialCardView oilsMaterialCardView = view.findViewById(R.id.category_oils_ghee_btn);
        MaterialCardView meatsMaterialCardView = view.findViewById(R.id.category_meat_fishes_btn);
        RecyclerView shimmerRecycler = view.findViewById(R.id.latest_product_recycler_shimmer);
        shimmerRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        shimmerRecycler.setAdapter(new homeAdapterShimmer(getContext(), 5, 1));
        latestproductarraylist = new ArrayList<>();

        RecyclerView latestProductRecycler = view.findViewById(R.id.latest_product_recycler);

        latestProductRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        latestProductAdapter = new productoHomeAdapter(1, getContext(), latestproductarraylist);

        latestProductRecycler.setAdapter(latestProductAdapter);

        getLatestproducts();
        dairyMaterialCardView.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.frame_layout, new CategoriaFragment())
                    .commit();
            SplashScreen.sharedPreferences.edit().putString("categoryHome", "envasados").apply();
        });
        meatsMaterialCardView.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.frame_layout, new CategoriaFragment())
                    .commit();
            SplashScreen.sharedPreferences.edit().putString("categoryHome", "carnesypescados").apply();
        });
        oilsMaterialCardView.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.frame_layout, new CategoriaFragment())
                    .commit();
            SplashScreen.sharedPreferences.edit().putString("categoryHome", "aceitesyvinagres").apply();
        });
        bakeryMaterialCardView.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.frame_layout, new CategoriaFragment())
                    .commit();
            SplashScreen.sharedPreferences.edit().putString("categoryHome", "reposteria").apply();

        });
        beveragesMaterialCardView.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.frame_layout, new CategoriaFragment())
                    .commit();
            SplashScreen.sharedPreferences.edit().putString("categoryHome", "bebidas").apply();

        });
        fruitsMaterialCardView.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.frame_layout, new CategoriaFragment())
                    .commit();
            SplashScreen.sharedPreferences.edit().putString("categoryHome", "frutasVerduras").apply();

        });

        seeAllBtn.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.frame_layout, new CategoriaFragment())
                    .commit();
            SplashScreen.sharedPreferences.edit().putString("categoryHome", "all").apply();
        });
        return view;
    }

    private void getLatestproducts() {
        FirebaseDatabase.getInstance().getReference("productos")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        HashMap<String, HashMap<String, String>> main = (HashMap<String, HashMap<String, String>>) snapshot.getValue();
                        HashMap<String, String> child;
                        Set<String> stringSet = main.keySet();
                        for (int i = 0; i < (main != null ? main.size() : 0); i++) {
                            child = main.get(stringSet.toArray()[i]);
                            latestproductarraylist.add(new modelProducto(child.get("imgMenor"), child.get("titulo"),
                                    child.get("precio")
                                    , child.get("unidad"),
                                    child.get("descripcion"),
                                    child.get("imgMayor"),
                                    main.keySet().toArray()[i] + ""));
                        }
                        latestProductAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}