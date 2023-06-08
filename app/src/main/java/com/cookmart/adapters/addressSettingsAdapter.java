package com.cookmart.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cookmart.AddEditDireccion;
import com.cookmart.R;
import com.cookmart.models.modelDireccion;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class addressSettingsAdapter extends RecyclerView.Adapter<addressSettingsAdapter.viewHolder> {

    private Context context;
    private ArrayList<modelDireccion> arrayList;

    public addressSettingsAdapter(Context context, ArrayList<modelDireccion> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(context).inflate(R.layout.layout_ajustes_direccion, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.name.setText(arrayList.get(position).getNombre());
        holder.address.setText(arrayList.get(position).getDireccion() +  ", "
                + arrayList.get(position).getCodPostal());
        holder.materialCardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddEditDireccion.class);
            intent.putExtra("direccion", arrayList.get(position).getDireccion());
            intent.putExtra("codpostal", arrayList.get(position).getCodPostal());
            intent.putExtra("nombre", arrayList.get(position).getNombre());
            intent.putExtra("ciudad", arrayList.get(position).getCiudad());
            intent.putExtra("numeroTlf", arrayList.get(position).getNumero());
            intent.putExtra("IdDireccion", arrayList.get(position).getIdDireccion());
            context.startActivity(intent);
        });
        holder.number.setText(arrayList.get(position).getNumero());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView address;
        private final TextView number;
        private final MaterialCardView materialCardView;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.ajustes_direccion_nombre);
            address = itemView.findViewById(R.id.ajustes_direccion_direccion);
            number = itemView.findViewById(R.id.ajustes_direccion_numero);
            materialCardView = itemView.findViewById(R.id.ajustes_direccion_edit_btn);
        }
    }
}
