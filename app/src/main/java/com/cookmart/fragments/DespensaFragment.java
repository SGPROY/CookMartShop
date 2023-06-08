package com.cookmart.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cookmart.Constants;
import com.cookmart.R;
import com.cookmart.SplashScreen;
import com.cookmart.adapters.despensaAdapter;
import com.cookmart.adapters.homeAdapterShimmer;
import com.cookmart.models.modelCarrito;
import com.cookmart.models.modelDespensa;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

public class DespensaFragment extends Fragment {

    private RecyclerView recyclerView, shimmerRecycler;
    private ArrayList<modelDespensa> arrayList;
    private ArrayList<modelCarrito> cartList;


    private com.cookmart.adapters.despensaAdapter despensaAdapter;
    private CardView allToCartBtn;
    private LinearLayout linearLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_despensa, container, false);
        recyclerView = view.findViewById(R.id.despensa_recycler);
        linearLayout = view.findViewById(R.id.fragment_despensa_status_layout);
        shimmerRecycler = view.findViewById(R.id.despensa_recycler_shimmer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        shimmerRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        shimmerRecycler.setAdapter(new homeAdapterShimmer(getContext(), 12, 3));
        arrayList = new ArrayList<modelDespensa>();
        cartList = new ArrayList<modelCarrito>();

        getProductosDespensa();
        getProductosCarrito();

        despensaAdapter = new despensaAdapter(getContext(), arrayList, cartList); // Pasar cartList a despensa_adapter
        recyclerView.setAdapter(despensaAdapter);

        return view;
    }

    // Muestra productos de la despensa
    private void getProductosDespensa() {
        try {
            Set<String> stringSet = SplashScreen.sharedPreferences.getStringSet("productos_despensa", null);
            if (stringSet != null) {
                if (stringSet.size() != 0) {
                    for (int i = 0; i < stringSet.size(); i++) {
                        String productId = stringSet.toArray()[i] + "";
                        String productQuantity = SplashScreen.sharedPreferences.getString("despensa_" + productId, "1");
                        FirebaseDatabase.getInstance().getReference("productos")
                                .child(productId).
                                addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            HashMap<String, String> hashMap = (HashMap<String, String>) snapshot.getValue();
                                            arrayList.add(new modelDespensa(Double.parseDouble(Objects.requireNonNull(hashMap.get("precio").replace(Constants.SIGNO_MONEDA, ""))), productId, "Precio 1 " + hashMap.get("unidad"), hashMap.get("titulo"), "Price 1 " + hashMap.get("unidad"), hashMap.get("imgMenor"), hashMap.get("precio"), productQuantity));
                                        }
                                        shimmerRecycler.setVisibility(View.INVISIBLE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        despensaAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    SplashScreen.sharedPreferences.edit().putStringSet("productos_despensa", stringSet).apply();
                } else {
                    linearLayout.setVisibility(View.VISIBLE);
                    shimmerRecycler.setVisibility(View.INVISIBLE);
                }
            } else {
                linearLayout.setVisibility(View.VISIBLE);
                shimmerRecycler.setVisibility(View.INVISIBLE);
            }
        } catch (Exception exception) {
            Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void getProductosCarrito() {
        try {
            Set<String> stringSet = SplashScreen.sharedPreferences.getStringSet("productos_carrito", null);
            if (stringSet != null) {
                if (stringSet.size() != 0) {
                    for (int i = 0; i < stringSet.size(); i++) {
                        int finalI = i;
                        FirebaseDatabase.getInstance().getReference("productos")
                                .child(stringSet.toArray()[i] + "").
                                addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            HashMap<String, String> hashMap = (HashMap<String, String>) snapshot.getValue();
                                            cartList.add(new modelCarrito(Double.parseDouble(Objects.requireNonNull(hashMap.get("precio").replace(Constants.SIGNO_MONEDA, ""))), stringSet.toArray()[finalI] + "", "Price 1 " + hashMap.get("unidad"), hashMap.get("titulo"), "Price 1 " + hashMap.get("unidad"), hashMap.get("imgMenor"), hashMap.get("precio"), "1"));
                                        }
                                        shimmerRecycler.setVisibility(View.INVISIBLE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        despensaAdapter.notifyDataSetChanged();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    SplashScreen.sharedPreferences.edit().putStringSet("productos_carrito", stringSet).apply();
                }
            }
        } catch (Exception exception) {
            Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

}