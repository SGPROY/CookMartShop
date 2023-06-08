package com.cookmart.fragments;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cookmart.R;

public class PedidoFallido extends Fragment {

   private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_pedido_fallado, container, false);
        CardView nothanks, tryagain;
        nothanks=view.findViewById(R.id.no_thanks_btn);
        tryagain=view.findViewById(R.id.try_again_btn);
        nothanks.setOnClickListener(view -> requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.frame_layout, new HomeFragment())
                .commit());
        tryagain.setOnClickListener(view -> requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.frame_layout, new CarritoFragment())
                .commit());
        return view;
    }
}