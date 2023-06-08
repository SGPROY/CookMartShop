package com.cookmart.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cookmart.R;
import com.cookmart.adapters.notificationAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class NotificacionFragment extends Fragment {

    private notificationAdapter adapter;
    private LinearLayout linearLayout;
    private ArrayList<String> dates, heading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.notification_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        heading = new ArrayList<>();
        dates = new ArrayList<>();
        linearLayout = view.findViewById(R.id.fragment_notification_status_linear_layout);
        adapter = new notificationAdapter(dates, heading, getContext());
        recyclerView.setAdapter(adapter);
        fetchNotification();
        return view;
    }

    private void fetchNotification() {
        FirebaseDatabase.getInstance().getReference("notificaciones").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dates.clear();
                        heading.clear();
                        if (snapshot.exists()) {
                            HashMap<String, HashMap<String, String>> hashMap = (HashMap<String, HashMap<String, String>>) snapshot.getValue();
                            HashMap<String, String> child;
                            for (int i = 0; i < hashMap.size(); i++) {
                                child = hashMap.get(hashMap.keySet().toArray()[i]);
                                dates.add(child.get("fecha"));
                                heading.add(child.get("titulo"));
                            }
                            if (dates.size() == 0) {
                                linearLayout.setVisibility(View.VISIBLE);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}