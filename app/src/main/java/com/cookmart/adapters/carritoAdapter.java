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
import com.cookmart.SplashScreen;
import com.cookmart.fragments.CarritoFragment;
import com.cookmart.models.modelCarrito;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Set;

public class carritoAdapter extends RecyclerView.Adapter<carritoAdapter.viewholder> {
    Context context;
    ArrayList<modelCarrito> arrayList;

    public carritoAdapter(Context context, ArrayList<modelCarrito> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewholder(LayoutInflater.from(context).inflate(R.layout.layout_carrito, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        try {
            holder.priceRate.setText(arrayList.get(position).getTasaPrecio());
            holder.price.setText(Constants.SIGNO_MONEDA.concat(arrayList.get(position).getPrecio()));
            holder.quantity.setText(SplashScreen.sharedPreferences.getString(arrayList.get(position).getIdProducto(),"1"));
            holder.price.setText("".concat(String.valueOf(arrayList.get(position).getPrecioCantidad() * Integer.parseInt(holder.quantity.getText().toString()))));
            arrayList.get(position).setCantidad(holder.quantity.getText().toString());
            arrayList.get(position).setPrecio(holder.price.getText().toString());
            CarritoFragment.arrayList = arrayList;

            holder.minus.setOnClickListener(v -> {
                if (!holder.quantity.getText().toString().equals("1")) {
                    int num = Integer.parseInt(holder.quantity.getText().toString());
                    num--;
                    arrayList.get(position).setCantidad(String.valueOf(num));
                    holder.price.setText(Constants.SIGNO_MONEDA.concat(String.valueOf(arrayList.get(position).getPrecioCantidad() * num)));
                    holder.quantity.setText(String.valueOf(num));
                    SplashScreen.sharedPreferences.edit().putString(arrayList.get(position).getIdProducto(),num+"").apply();
                    arrayList.get(position).setPrecio(holder.price.getText().toString());
                    CarritoFragment.arrayList = arrayList;
//                    arrayList.get(position).setPrice(arrayList.get(position).getQuantityPrice()*num+"");

                }
            });
            holder.plus.setOnClickListener(v -> {
                int num = Integer.parseInt(holder.quantity.getText().toString());
                num++;
                arrayList.get(position).setCantidad(String.valueOf(num));
                holder.price.setText(Constants.SIGNO_MONEDA.concat(String.valueOf(arrayList.get(position).getPrecioCantidad() * num)));
                holder.quantity.setText(String.valueOf(num));
                SplashScreen.sharedPreferences.edit().putString(arrayList.get(position).getIdProducto(),num+"").apply();
                arrayList.get(position).setPrecio(holder.price.getText().toString());
                CarritoFragment.arrayList = arrayList;

//                arrayList.get(position).setPrice(arrayList.get(position).getQuantityPrice()*num+"");
            });

            holder.title.setText(arrayList.get(position).getTitulo());
            Picasso.get().load(arrayList.get(position).getImagen()).into(holder.itemImage);
            holder.price.setText(Constants.SIGNO_MONEDA.concat(arrayList.get(position).getPrecio()));
            holder.closeBtn.setOnClickListener(v -> {
                Set<String> cartStringSet = SplashScreen.sharedPreferences.getStringSet("productos_carrito", null);
                cartStringSet.remove(arrayList.get(position).getIdProducto());
                SplashScreen.sharedPreferences.edit().remove(arrayList.get(position).getIdProducto()).apply();
                arrayList.remove(position);
                notifyDataSetChanged();
            });
        } catch (Exception exception) {
            Toast.makeText(context, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class viewholder extends RecyclerView.ViewHolder {
        private final ImageView itemImage;
        private final ImageView closeBtn;
        private final TextView title;
        private final TextView priceRate;
        private final TextView price;
        private final MaterialCardView minus;
        private final MaterialCardView plus;
        private final TextView quantity;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.cart_image);
            priceRate = itemView.findViewById(R.id.cart_price_rate);
            minus = itemView.findViewById(R.id.carro_min);
            closeBtn = itemView.findViewById(R.id.cart_close_button);
            quantity = itemView.findViewById(R.id.cantidad_carro);
            title = itemView.findViewById(R.id.cart_title);
            price = itemView.findViewById(R.id.cart_item_price);
            plus = itemView.findViewById(R.id.carro_add);
        }
    }
}
