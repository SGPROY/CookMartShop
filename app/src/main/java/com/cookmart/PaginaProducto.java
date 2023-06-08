package com.cookmart;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.Set;

// Esta clase maneja la página de detalles del producto
public class PaginaProducto extends AppCompatActivity {

    private TextView description;
    private boolean isMenuOpened, isInDespensa, isInCart;
    private CardView addBtn, minusBtn;
    private TextView quantity;
    private CardView addToCartBtn,despensabtn;
    private String productId;
    private TextView addToCartStatus, addToDespensaStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto);
        try {
            inicializarComponentes();

            addBtn.setOnClickListener(v -> incrementarCantidad());

            minusBtn.setOnClickListener(v -> decrementarCantidad());

            despensabtn.setOnClickListener(v -> alternarDespensa());

            addToCartBtn.setOnClickListener(v -> alternarCarrito());

            obtenerEstado();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void inicializarComponentes() {
        addBtn = findViewById(R.id.carro_add);
        quantity = findViewById(R.id.cantidad_carro);
        minusBtn = findViewById(R.id.carro_min);
        description = findViewById(R.id.pagina_producto_descripcion);
        addToCartStatus = findViewById(R.id.add_to_cart_stataus);
        addToDespensaStatus = findViewById(R.id.add_to_despensa_stataus);
        TextView title = findViewById(R.id.pagina_producto_titulo);
        ImageView bigImage = findViewById(R.id.product_page_big_Image);
        TextView measuringUnit = findViewById(R.id.pagina_producto_precioInfo);
        TextView price = findViewById(R.id.pagina_producto_precio);
        productId = getIntent().getStringExtra("idProducto");

        // Establece el valor inicial de la cantidad
        if (!SplashScreen.sharedPreferences.getString(productId, "default").equals("default")) {
            quantity.setText(SplashScreen.sharedPreferences.getString(productId, "default"));
        } else {
            quantity.setText("1");
        }

        // Establece el valor inicial de la cantidad
        if (!SplashScreen.sharedPreferences.getString("despensa_" + productId, "default").equals("default")) {
            quantity.setText(SplashScreen.sharedPreferences.getString("despensa_" + productId, "default"));
        } else {
            quantity.setText("1");
        }


        measuringUnit.setText("Precio por 1 " + getIntent().getStringExtra("unidad"));
        price.setText( getIntent().getStringExtra("precio") + "€");
        Picasso.get().load(getIntent().getStringExtra("imgMayor")).into(bigImage);
        title.setText(getIntent().getStringExtra("titulo"));
        addToCartBtn = findViewById(R.id.pagina_producto_add_carro);
        //likebtn = findViewById(R.id.pagina_producto_megusta);
        //Este es el nuevo btn llamado Despensa.
        despensabtn = findViewById(R.id.pagina_producto_add_despensa);
        description.setText(getIntent().getStringExtra("descripcion"));
    }

    // Incrementa la cantidad del producto
    private void incrementarCantidad() {
            int newQuantity = Integer.parseInt(quantity.getText().toString()) + 1;
            quantity.setText(newQuantity + "");
    }

    // Decrementa la cantidad del producto
    private void decrementarCantidad() {
        if (!quantity.getText().toString().equals("1")) {
            // Verifica si estás en la despensa o en el carrito para ajustar la clave correspondiente
                int newQuantity = Integer.parseInt(quantity.getText().toString()) - 1;
                quantity.setText(newQuantity + "");
        }
    }


    // Alterna la presencia del producto en la despensa
    private void alternarDespensa() {
        Set<String> despensaStringSet = SplashScreen.sharedPreferences.getStringSet("productos_despensa", null);
        if (despensaStringSet == null) {
            despensaStringSet = new HashSet<>();
        }
        if (isInDespensa) {
            despensaStringSet.remove(productId + "");
            SplashScreen.sharedPreferences.edit().remove("despensa_" + productId).apply(); // Agrega esta línea para eliminar la cantidad en la despensa
            SplashScreen.sharedPreferences.edit().putStringSet("productos_despensa", despensaStringSet).apply();
            isInDespensa = false;
            despensabtn.setCardBackgroundColor(Color.parseColor("#FF018786"));
            addToDespensaStatus.setText("Añadir a la despensa");

            Toast.makeText(PaginaProducto.this, "Eliminado de la despensa", Toast.LENGTH_SHORT).show();
        } else {
            despensaStringSet.add(productId + "");
            SplashScreen.sharedPreferences.edit().putString("despensa_" + getIntent().getStringExtra("idProducto"), Integer.parseInt(quantity.getText().toString()) + "").apply();
            isInDespensa = true;
            SplashScreen.sharedPreferences.edit().putStringSet("productos_despensa", despensaStringSet).apply();
            despensabtn.setCardBackgroundColor(Color.parseColor("#ffff4444"));
            addToDespensaStatus.setText("Eliminar de la despensa");
            Toast.makeText(PaginaProducto.this, "Añadido a la despensa", Toast.LENGTH_SHORT).show();
        }
    }



    // Alterna la presencia del producto en el carrito
    private void alternarCarrito() {
        Set<String> cartStringSet = SplashScreen.sharedPreferences.getStringSet("productos_carrito", null);
        if (cartStringSet == null) {
            cartStringSet = new HashSet<>();
        }
        if (isInCart) {
            cartStringSet.remove(productId + "");
            SplashScreen.sharedPreferences.edit().putStringSet("productos_carrito", cartStringSet).apply();
            isInCart = false;
            addToCartBtn.setCardBackgroundColor(Color.parseColor("#F05A25"));
            addToCartStatus.setText("Añadir al carrito");
            Toast.makeText(PaginaProducto.this, "Eliminado del carrito", Toast.LENGTH_SHORT).show();

        } else {
            cartStringSet.add(productId + "");
            SplashScreen.sharedPreferences.edit().putString(getIntent().getStringExtra("idProducto"), Integer.parseInt(quantity.getText().toString()) + "").apply();
            isInCart = true;
            SplashScreen.sharedPreferences.edit().putStringSet("productos_carrito", cartStringSet).apply();
            addToCartBtn.setCardBackgroundColor(Color.parseColor("#ffff4444"));
            addToCartStatus.setText("Eliminar del carrito");
            Toast.makeText(PaginaProducto.this, "Añadido al carrito", Toast.LENGTH_SHORT).show();
        }
    }

    private void obtenerEstado() {
        verificarEstadoCarrito();
        verificarEstadoDespensa();
    }

    // Verifica si el producto está en la despensa
    private void verificarEstadoDespensa() {
        boolean isInListDespensa = false;
        Set<String> despensaStringSet = SplashScreen.sharedPreferences.getStringSet("productos_despensa", null);
        if (despensaStringSet != null) {
            isInListDespensa = despensaStringSet.contains(productId);
        }

        if (isInListDespensa) {
            isInDespensa = true;
            despensabtn.setCardBackgroundColor(Color.parseColor("#ffff4444"));
            addToDespensaStatus.setText("Eliminar de la despensa");

        } else {
            despensabtn.setCardBackgroundColor(Color.parseColor("#FF018786"));
            addToDespensaStatus.setText("Añadir a la despensa");

        }
    }

    // Verifica si el producto está en el carrito
    private void verificarEstadoCarrito() {
        boolean isInListCart = false;
        Set<String> cartStringSet = SplashScreen.sharedPreferences.getStringSet("productos_carrito", null);
        if (cartStringSet != null) {
            isInListCart = cartStringSet.contains(productId);
        }

        if (isInListCart) {
            isInCart = true;
            addToCartBtn.setCardBackgroundColor(Color.parseColor("#ffff4444"));
            addToCartStatus.setText("Eliminar del carrito");
        } else {
            addToCartBtn.setCardBackgroundColor(Color.parseColor("@color/apptheme"));
            addToCartStatus.setText("Añadir al carrito");
        }
    }

    // Muestra u oculta el menú del producto
    public void mostrar_menu_producto(View view) {
        if (isMenuOpened) {
            isMenuOpened = false;
            description.setVisibility(View.GONE);
        } else {
            isMenuOpened = true;
            description.setVisibility(View.VISIBLE);
        }
    }
}


