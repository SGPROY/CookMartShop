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

import com.cookmart.R;
import com.cookmart.SplashScreen;
import com.cookmart.models.modelCarrito;
import com.cookmart.models.modelDespensa;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class despensaAdapter extends RecyclerView.Adapter<despensaAdapter.viewholder> {
    Context context;
    ArrayList<modelDespensa> arrayList;
    ArrayList<modelCarrito> cartList;

    public despensaAdapter(Context context, ArrayList<modelDespensa> arrayList, ArrayList<modelCarrito> cartList) {
        this.context = context;
        this.arrayList = arrayList;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewholder(LayoutInflater.from(context).inflate(R.layout.layout_despensa, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        try {
            holder.title.setText(arrayList.get(position).getTitulo());
            Picasso.get().load(arrayList.get(position).getImagen()).into(holder.itemImage);
            holder.numItems.setText(SplashScreen.sharedPreferences.getString("despensa_" + arrayList.get(position).getIdProducto(), "1"));

            modelDespensa item = arrayList.get(position);

            holder.reduce.setOnClickListener(v -> {
                int quantity = Integer.parseInt(SplashScreen.sharedPreferences.getString("despensa_" + item.getIdProducto(), "1"));

                if (quantity > 1) {
                    quantity--;

                    SplashScreen.sharedPreferences.edit()
                            .putString("despensa_" + item.getIdProducto(), String.valueOf(quantity))
                            .apply();

                    holder.numItems.setText(String.valueOf(quantity));

                    // Incrementar la cantidad en el carrito
                    boolean itemExistsInCart = false;
                    for (modelCarrito cartItem : cartList) {
                        if (cartItem.getIdProducto().equals(item.getIdProducto())) {
                            int cartItemQuantity = Integer.parseInt(SplashScreen.sharedPreferences.getString(cartItem.getIdProducto(), "1"));
                            cartItemQuantity++;

                            cartItem.setCantidad(String.valueOf(cartItemQuantity));
                            SplashScreen.sharedPreferences.edit()
                                    .putString(cartItem.getIdProducto(), String.valueOf(cartItemQuantity))
                                    .apply();

                            itemExistsInCart = true;
                            break;
                        }
                    }

                    if (!itemExistsInCart) {
                        modelCarrito newCartItem = new modelCarrito(
                                item.getPrecioCantidad(),
                                item.getIdProducto(),
                                item.getUnidadMedida(),
                                item.getTitulo(),
                                item.getTasaPrecio(),
                                item.getImagen(),
                                item.getPrecio().toString(),
                                "1"
                        );
                        cartList.add(newCartItem);
                        SplashScreen.sharedPreferences.edit()
                                .putString(newCartItem.getIdProducto(), "1")
                                .apply();
                        // Añadimos el id del producto a "productos_carrito" en SharedPreferences
                        Set<String> productIds = SplashScreen.sharedPreferences.getStringSet("productos_carrito", new HashSet<>());
                        productIds.add(item.getIdProducto());
                        SplashScreen.sharedPreferences.edit().putStringSet("productos_carrito", productIds).apply();


                      }

                } else if (quantity == 1) {
                    // Si la cantidad es 1, la reducimos a 0 y eliminamos los valores de SharedPreferences

                    // Eliminamos despensa_{productId}
                    SplashScreen.sharedPreferences.edit().remove("despensa_" + item.getIdProducto()).apply();

                    // Eliminamos el producto de productos_despensa
                    Set<String> productIds = SplashScreen.sharedPreferences.getStringSet("productos_despensa", new HashSet<>());
                    productIds.remove(item.getIdProducto());
                    SplashScreen.sharedPreferences.edit().putStringSet("productos_despensa", productIds).apply();

                    // Incrementamos la cantidad en el carrito si ya existe el producto, sino lo añadimos
                    boolean itemExistsInCart = false;
                    // Incrementar la cantidad en el carrito
                    for (modelCarrito cartItem : cartList) {
                        if (cartItem.getIdProducto().equals(item.getIdProducto())) {
                            int cartItemQuantity = Integer.parseInt(SplashScreen.sharedPreferences.getString(cartItem.getIdProducto(), "1"));
                            cartItemQuantity++;

                            cartItem.setCantidad(String.valueOf(cartItemQuantity));
                            SplashScreen.sharedPreferences.edit()
                                    .putString(cartItem.getIdProducto(), String.valueOf(cartItemQuantity))
                                    .apply();

                            itemExistsInCart = true;
                            break;
                        }
                    }

                    if (!itemExistsInCart) {
                        modelCarrito newCartItem = new modelCarrito(
                                item.getPrecioCantidad(),
                                item.getIdProducto(),
                                item.getUnidadMedida(),
                                item.getTitulo(),
                                item.getTasaPrecio(),
                                item.getImagen(),
                                item.getPrecio().toString(),
                                "1"
                        );
                        cartList.add(newCartItem);
                        SplashScreen.sharedPreferences.edit()
                                .putString(newCartItem.getIdProducto(), "1")
                                .apply();
                        // Añadimos el id del producto a "productos_carrito" en SharedPreferences
                        Set<String> productIds1 = SplashScreen.sharedPreferences.getStringSet("productos_carrito", new HashSet<>());
                        productIds1.add(item.getIdProducto());
                        SplashScreen.sharedPreferences.edit().putStringSet("productos_carrito", productIds).apply();
                    }

                    Toast.makeText(context, "Producto eliminado de la despensa y añadido al carrito.", Toast.LENGTH_SHORT).show();

                    // Removemos el elemento de la lista y notificamos al adapter
                    arrayList.remove(position);
                    notifyDataSetChanged();

                } else {
                    Toast.makeText(context, "No hay más productos para reducir en la despensa.", Toast.LENGTH_SHORT).show();
                }

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
        private final MaterialCardView reduce;
        private final TextView title;
        private final TextView numItems;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.layout_despensa_image);
            title = itemView.findViewById(R.id.cart_despensa_title);
            numItems = itemView.findViewById(R.id.layout_despensa_numero);
            reduce = itemView.findViewById(R.id.reduce_item_button);
        }
    }
}
