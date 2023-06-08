package com.cookmart.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cookmart.Constants;
import com.cookmart.R;
import com.cookmart.models.modelEditDetalles;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class adapterEditarDetalles extends RecyclerView.Adapter<adapterEditarDetalles.viewHolder> {

    private final Context context;
    private final ArrayList<modelEditDetalles> arrayList;

    public adapterEditarDetalles(Context context, ArrayList<modelEditDetalles> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(context).inflate(R.layout.layout_detalles_producto, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        try {
            holder.titulo.setText(arrayList.get(position).getTitulo());
            holder.measuringUnit.setText(arrayList.get(position).getUnidadMedida().replace("1", arrayList.get(position).getCantidad()));
            holder.price.setText(Constants.SIGNO_MONEDA.concat(String.valueOf(Double.parseDouble(arrayList.get(position).getPrecio()) * Double.parseDouble(arrayList.get(position).getCantidad()))));
            Picasso.get().load(arrayList.get(position).getImagen()).into(holder.image);
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class viewHolder extends RecyclerView.ViewHolder {
        private final ImageView image;
        private final TextView titulo;
        private final TextView measuringUnit;
        private final TextView price;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.layout_editDetails_image);
            titulo = itemView.findViewById(R.id.layout_editDetails_title);
            measuringUnit = itemView.findViewById(R.id.layout_editDetails_measuringUnit);
            price = itemView.findViewById(R.id.layout_editDetails_price);
        }
    }
}
