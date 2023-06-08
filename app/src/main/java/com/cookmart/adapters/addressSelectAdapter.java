package com.cookmart.adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cookmart.AddEditDireccion;
import com.cookmart.R;
import com.cookmart.SplashScreen;
import com.cookmart.fragments.CarritoFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class addressSelectAdapter extends RecyclerView.Adapter<addressSelectAdapter.viewHolder> {

    private final ArrayList<String> address;
    private final ArrayList<String> addressId;
    private final ArrayList<String> numbers;
    private final ArrayList<String> pinCodes;
    private final Context context;

    public addressSelectAdapter(ArrayList<String> numbers, ArrayList<String> addressId, ArrayList<String> address, ArrayList<String> pinCodes, Context context) {
        this.numbers = numbers;
        this.pinCodes = pinCodes;
        this.address = address;
        this.addressId = addressId;
        this.context = context;
    }

    @NonNull
    @Override
    public addressSelectAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(context).inflate(R.layout.layout_address_recycler, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull addressSelectAdapter.viewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (position == 0) {
            holder.pinCode.setText("");
            holder.address.setText("Añadir nueva Dirección");
            holder.mainLayout.setOnClickListener(v -> context.startActivity(new Intent(context, AddEditDireccion.class)));
        } else if(!pinCodes.get(position-1).equals("c")) {
            holder.address.setText(address.get(position - 1));
            holder.pinCode.setText(pinCodes.get(position - 1));
            holder.mainLayout.setOnClickListener(v -> {
                ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Espere mientras recuperamos los datos.");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                FirebaseDatabase.getInstance().getReference("codpostal")
                        .child(holder.pinCode.getText().toString())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    HashMap<String, String> hashMap = (HashMap<String, String>) snapshot.getValue();
                                    if (hashMap.get("efectivoActivo").equals("false")) {
                                        Toast.makeText(context, "El pago contra reembolso no está disponible para estos códigos postales", Toast.LENGTH_SHORT).show();
                                        CarritoFragment.bottomSheetDialogAddress.dismiss();
                                        CarritoFragment.isCodAvailable = false;
                                    } else {
                                        CarritoFragment.bottomSheetDialogAddress.dismiss();
                                        CarritoFragment.bottomSheetDialogAddress.dismiss();
                                        CarritoFragment.isCodAvailable = true;
                                    }
                                    SplashScreen.sharedPreferences.edit().putString("numeroTlf", numbers.get(position - 1)).apply();
                                    SplashScreen.sharedPreferences.edit().putString("idDireccion", addressId.get(position - 1)).apply();
                                    CarritoFragment.showPriceBtn.setVisibility(View.VISIBLE);
                                    CarritoFragment.deliveryCharges = Integer.parseInt(hashMap.get("cargoEnvio"));
                                    CarritoFragment.totalPrice.setText(CarritoFragment.totalPriceOfItem + Integer.parseInt(hashMap.get("cargoEnvio")) + "");
                                    CarritoFragment.address.setText(pinCodes.get(position - 1));
                                    progressDialog.cancel();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            });
        }else{
            holder.mainLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return numbers.size() + 1;
    }

    public static class viewHolder extends RecyclerView.ViewHolder {

        private final TextView address;
        private final TextView pinCode;
        private final MaterialCardView mainLayout;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address_recycler_address);
            pinCode = itemView.findViewById(R.id.address_recycler_pincode);
            mainLayout = itemView.findViewById(R.id.main_layout_adress_recycler);
        }
    }
}
