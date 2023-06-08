package com.cookmart.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.cookmart.Constants;
import com.cookmart.PaginaProducto;
import com.cookmart.R;
import com.cookmart.models.modelProducto;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class productoHomeAdapter extends RecyclerView.Adapter<productoHomeAdapter.viewholder> {
    private final Context context;
    private final int layout;
    private final ArrayList<modelProducto> arrayList;

    public productoHomeAdapter(int layout, Context context, ArrayList<modelProducto> arrayList) {
        this.context = context;
        this.layout = layout;
        this.arrayList = arrayList;
    }

    //1 is for homepage

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layout == 1) {
            return new viewholder(LayoutInflater.from(context).inflate(R.layout.layout_products_homepage, parent, false));
        } else {
            return new viewholder(LayoutInflater.from(context).inflate(R.layout.layout_searched_products, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        holder.title.setText(arrayList.get(position).getTitulo());
        Picasso.get().load(arrayList.get(position).getImagen()).into(holder.imageView);
        holder.price.setText(Constants.SIGNO_MONEDA.concat(arrayList.get(position).getPrecio()));
        holder.measuringUnit.setText("1 " + arrayList.get(position).getUnidadMedida());
        holder.addBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, PaginaProducto.class);
            intent.putExtra("precio", arrayList.get(position).getPrecio());
            intent.putExtra("unidad", arrayList.get(position).getUnidadMedida());
            intent.putExtra("titulo", arrayList.get(position).getTitulo());
            intent.putExtra("descripcion", arrayList.get(position).getDescripcion());
            intent.putExtra("imgMayor", arrayList.get(position).getImagenMayor());
            intent.putExtra("idProducto", arrayList.get(position).getIdProducto());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
        holder.materialCardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PaginaProducto.class);
            intent.putExtra("precio", arrayList.get(position).getPrecio());
            intent.putExtra("unidad", arrayList.get(position).getUnidadMedida());
            intent.putExtra("titulo", arrayList.get(position).getTitulo());
            intent.putExtra("descripcion", arrayList.get(position).getDescripcion());
            intent.putExtra("imgMayor", arrayList.get(position).getImagenMayor());
            intent.putExtra("idProducto", arrayList.get(position).getIdProducto());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class viewholder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final CardView addBtn;
        private final TextView title;
        private final TextView measuringUnit;
        private final TextView price;
        private final MaterialCardView materialCardView;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.product_homepage_image);
            title = itemView.findViewById(R.id.product_homepage_title);
            materialCardView = itemView.findViewById(R.id.products_homepage_main_layout);
            price = itemView.findViewById(R.id.product_homepage_price);
            addBtn = itemView.findViewById(R.id.product_homepage_add_btn);
            measuringUnit = itemView.findViewById(R.id.product_homepage_measuring_unit);
        }
    }
}
