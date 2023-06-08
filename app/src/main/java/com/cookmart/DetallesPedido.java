package com.cookmart;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cookmart.HelperClasses.PedidosObject;
import com.cookmart.adapters.adapterEditarDetalles;
import com.cookmart.models.modelEditDetalles;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class DetallesPedido extends AppCompatActivity {
    private PedidosObject object;
    private RecyclerView recyclerView;
    private TextView orderDeatilsTxt;
    private TextView addressCustomer;
    private TextView phoneNumber;
    private TextView orderedProductsTxt;
    private ConstraintLayout orderDetails;
    private ConstraintLayout orderDetailsBtn, orderProductsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_pedido);
        object = (PedidosObject) getIntent().getSerializableExtra("PedidosObject");

        try {
            recyclerView = findViewById(R.id.detalles_pedido_recycler);
            recyclerView.setVisibility(View.VISIBLE);
            orderDetailsBtn = findViewById(R.id.detallesPedidoBtn);
            TextView paymentMode = findViewById(R.id.detalles_pedido_tipoPago);
            TextView orderId = findViewById(R.id.detalles_pedido_idPedido);
            orderedProductsTxt = findViewById(R.id.order_products_txt);
            orderDetails = findViewById(R.id.detalles_pedido_layout);
            addressCustomer = findViewById(R.id.detalles_pedido_direccion);
            phoneNumber = findViewById(R.id.detalles_pedido_telefono);
            TextView price = findViewById(R.id.order_details_price);
            TextView statusOfOrder = findViewById(R.id.detalles_pedido_estado);
            TextView date = findViewById(R.id.detalles_pedido_fecha);
            orderDeatilsTxt = findViewById(R.id.orderedDetailsTxt);
            char temp=Character.toUpperCase(object.getEstado().charAt(0));
            statusOfOrder.setText(object.getEstado().replaceFirst(object.getEstado().charAt(0)+"",temp+""));
            date.setText(object.getFecha());
            price.setText(Constants.SIGNO_MONEDA.concat(object.getPrecioTotal()));
            paymentMode.setText(object.getTipoPago());
            orderId.setText("Id Pedido: #" + object.getIndex());
            orderProductsBtn = findViewById(R.id.productos_pedido_btn);
            ArrayList<modelEditDetalles> arrayList = new ArrayList<>();
            adapterEditarDetalles adapter = new adapterEditarDetalles(DetallesPedido.this, arrayList);
            recyclerView.setLayoutManager(new LinearLayoutManager(DetallesPedido.this));
            recyclerView.setAdapter(adapter);
            orderProductsBtn.setOnClickListener(v -> {
                recyclerView.setVisibility(View.VISIBLE);
                orderDetails.setVisibility(View.INVISIBLE);
                orderedProductsTxt.setTextColor(ContextCompat.getColor(DetallesPedido.this, R.color.white));
                orderDeatilsTxt.setTextColor(ContextCompat.getColor(DetallesPedido.this, R.color.apptheme));
                orderDetailsBtn.setBackgroundColor(ContextCompat.getColor(DetallesPedido.this, R.color.white));
                orderProductsBtn.setBackgroundColor(ContextCompat.getColor(DetallesPedido.this, R.color.apptheme));
            });
            orderDetailsBtn.setOnClickListener(v -> {
                recyclerView.setVisibility(View.GONE);
                orderedProductsTxt.setTextColor(ContextCompat.getColor(DetallesPedido.this, R.color.apptheme));
                orderDetails.setVisibility(View.VISIBLE);
                orderDeatilsTxt.setTextColor(ContextCompat.getColor(DetallesPedido.this, R.color.white));
                orderDetailsBtn.setBackgroundColor(ContextCompat.getColor(DetallesPedido.this, R.color.apptheme));
                orderProductsBtn.setBackgroundColor(ContextCompat.getColor(DetallesPedido.this, R.color.white));
            });
            HashMap<String, HashMap<String, String>> hashMap = object.getProductos();
            try {
                for (int i = 0; i < hashMap.size(); i++) {
                    HashMap<String, String> child = hashMap.get(hashMap.keySet().toArray()[i]);
                    arrayList.add(new modelEditDetalles(child.get("imgMenor"), child.get("titulo"), child.get("unidad"), child.get("cantidad"), child.get("precio")));
                }
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            getAddress();


        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void getAddress() {
        FirebaseDatabase.getInstance().getReference("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("direccionesEnvio").child(object.getIdDireccion()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HashMap<String, String> hashMap = (HashMap<String, String>) snapshot.getValue();
                    String temp = hashMap.get("direccion")
                            .concat(", " + hashMap.get("ciudad")).concat(", " + Objects.requireNonNull(hashMap.get("codpostal")));
                    addressCustomer.setText(temp);
                    phoneNumber.setText(hashMap.get("numeroTlf"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}