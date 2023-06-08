package com.cookmart.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cookmart.HelperClasses.PedidosObject;
import com.cookmart.R;
import com.cookmart.adapters.adapterPedidos;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PedidosFragment extends Fragment {

    private ConstraintLayout dispatchedBtn, allBtn;
    private adapterPedidos adapter;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;
    private RecyclerView recyclerView;
    private ConstraintLayout onTheWayBtn;
    private ArrayList<PedidosObject> arrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pedidos, container, false);
        recyclerView = view.findViewById(R.id.ordersRecycler);
        arrayList = new ArrayList<>();
        progressBar = view.findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);
        ImageView backBtn = view.findViewById(R.id.frag_orders_back_btn);
        linearLayout = view.findViewById(R.id.fragment_orders_status_layout);
        ConstraintLayout preparingBtn = view.findViewById(R.id.ordersPreparingBtn);
        dispatchedBtn = view.findViewById(R.id.orderDispatchBtn);
        onTheWayBtn = view.findViewById(R.id.orders_onTheWayBtn);
        ConstraintLayout deliveredBtn = view.findViewById(R.id.ordersDeliveredBtn);
        allBtn = view.findViewById(R.id.ordersAllBtn);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new adapterPedidos(arrayList, getContext());
        recyclerView.setAdapter(adapter);
        getMyOrders("all");
        allBtn.setOnClickListener(v -> {
            getMyOrders("all");
            allBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.apptheme));
            preparingBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
            dispatchedBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
            deliveredBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
            onTheWayBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
        });
        onTheWayBtn.setOnClickListener(v -> {
            getMyOrders("enCamino");
            onTheWayBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.apptheme));
            preparingBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
            dispatchedBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
            deliveredBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
            allBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
        });
        deliveredBtn.setOnClickListener(v -> {
            getMyOrders("entregado");
            deliveredBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.apptheme));
            preparingBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
            dispatchedBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
            allBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
            onTheWayBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
        });
        dispatchedBtn.setOnClickListener(v -> {
            getMyOrders("enviado");
            dispatchedBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.apptheme));
            preparingBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
            allBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
            deliveredBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
            onTheWayBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
        });
        preparingBtn.setOnClickListener(v -> {
            getMyOrders("preparando");
            preparingBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.apptheme));
            allBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
            dispatchedBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
            deliveredBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
            onTheWayBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
        });
        backBtn.setOnClickListener(v -> requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.frame_layout, new CuentaFragment())
                .commit());
        return view;
    }

    private void getMyOrders(String status) {
        arrayList.clear();
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        FirebaseDatabase.getInstance().getReference("pedidos").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.getValue(PedidosObject.class).getEstado().equals(status)) {
                                arrayList.add(dataSnapshot.getValue(PedidosObject.class));
                            } else if (status.equals("all")) {
                                arrayList.add(dataSnapshot.getValue(PedidosObject.class));
                            }
                        }
                        if (arrayList.size() == 0) {
                            linearLayout.setVisibility(View.VISIBLE);
                        } else {
                            linearLayout.setVisibility(View.INVISIBLE);
                        }
                        recyclerView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}