package com.cookmart.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cookmart.Constants;
import com.cookmart.HelperClasses.PedidosObject;
import com.cookmart.LoginScreen;
import com.cookmart.PaymentActivity;
import com.cookmart.R;
import com.cookmart.SplashScreen;
import com.cookmart.adapters.addressSelectAdapter;
import com.cookmart.adapters.carritoAdapter;
import com.cookmart.adapters.homeAdapterShimmer;
import com.cookmart.models.modelCarrito;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class CarritoFragment extends Fragment {

    public static TextView address;
    public static boolean isCodAvailable;
    public static ImageView showPriceBtn;
    public static double totalPriceOfItem, deliveryCharges;
    public static ArrayList<modelCarrito> arrayList;
    public static TextView totalPrice;
    public static BottomSheetDialog bottomSheetDialogAddress;
    private RecyclerView recyclerView, shimmerRecycler;
    private carritoAdapter cartAdapter;
    private PedidosObject object;
    private CardView buyNowBtn;
    private LinearLayout linearLayout;
    private ProgressBar progressBar;
    private BottomSheetDialog bottomSheetDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_carrito, container, false);
        try {
            recyclerView = view.findViewById(R.id.cart_recycler);
            shimmerRecycler = view.findViewById(R.id.cart_recycler_shimmer);
            linearLayout = view.findViewById(R.id.fragment_cart_status_layout);
            progressBar = view.findViewById(R.id.progressBar3);
            buyNowBtn = view.findViewById(R.id.cart_buy_now_btn);
            shimmerRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
            shimmerRecycler.setAdapter(new homeAdapterShimmer(getContext(), 8, 2));
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            arrayList = new ArrayList<>();
            cartAdapter = new carritoAdapter(getContext(), arrayList);
            recyclerView.setAdapter(cartAdapter);
            getProductsId();
            buyNowBtn.setOnClickListener(v -> {
                if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                    showBottomSheetDialog();
                } else {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                    View customLayout = LayoutInflater.from(getContext()).inflate(R.layout.layout_verify_email_adress, null);
                    bottomSheetDialog.setContentView(customLayout);
                    bottomSheetDialog.show();
                    CardView cardView1 = customLayout.findViewById(R.id.layout_verify_email_cardView);
                    cardView1.setOnClickListener(v1 -> {
                        bottomSheetDialog.dismiss();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getContext(), LoginScreen.class));
                        Toast.makeText(getContext(), "Correo electrónico de verificación enviado con éxito.", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } catch (Exception e) {

        }
        return view;
    }

    public void calculateTotalPrice() {
        totalPriceOfItem = 0;
        String temp;
        for (int i = 0; i < arrayList.size(); i++) {
            temp = arrayList.get(i).getPrecio().replace(Constants.SIGNO_MONEDA + "", "");
            totalPriceOfItem += Double.parseDouble(temp);
        }
    }

    private void showBottomSheetDialog() {
        calculateTotalPrice();
        bottomSheetDialog = new BottomSheetDialog(requireContext());
        View customLayout = LayoutInflater.from(getContext()).inflate(R.layout.layout_bottom, null);
        bottomSheetDialog.setContentView(customLayout);
        address = customLayout.findViewById(R.id.bottom_proceed_delivery_addess);
        totalPrice = customLayout.findViewById(R.id.layout_bottom_price);
        TextView paymentType = customLayout.findViewById(R.id.layout_bottom_payment_type);
        ImageView selectAddressBtn = customLayout.findViewById(R.id.bottom_proceed_delivery_adress);
        ImageView selectPaymentTypeBtn = customLayout.findViewById(R.id.bottom_proceed_payment_type);
        showPriceBtn = customLayout.findViewById(R.id.bottom_proceed_total_price);
        ImageView closeBtn = customLayout.findViewById(R.id.bottom_close_btn);
        showPriceBtn.setVisibility(View.GONE);
        CardView continueBtn = customLayout.findViewById(R.id.bottom_proceed_next_continue_btn);
        bottomSheetDialog.show();
        totalPrice.setText(String.valueOf(totalPriceOfItem));
        showPriceBtn.setOnClickListener(v -> {
            AlertDialog alertDialog;
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View vobj = LayoutInflater.from(getContext()).inflate(R.layout.layout_payment_bill, null);
            builder.setView(vobj);
            TextView totalcp = vobj.findViewById(R.id.customer_bill_cost_price);
            TextView deliveryinvoice = vobj.findViewById(R.id.customer_bill_delivery_charge);
            TextView totalinvoice = vobj.findViewById(R.id.customer_bill_total_price);
            totalcp.setText(Constants.SIGNO_MONEDA.concat(String.valueOf(totalPriceOfItem)));
            deliveryinvoice.setText(Constants.SIGNO_MONEDA.concat(String.valueOf(deliveryCharges)));
            totalinvoice.setText(Constants.SIGNO_MONEDA.concat(String.valueOf(totalPriceOfItem + deliveryCharges)));
            alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        });
        closeBtn.setOnClickListener(v -> bottomSheetDialog.cancel());
        selectAddressBtn.setOnClickListener(v -> openAddressDialog());
        selectPaymentTypeBtn.setOnClickListener(v -> {
            if (address.getText().toString().equals("selecciona una dirección")) {
                Toast.makeText(getContext(), "Seleccione primero su dirección.", Toast.LENGTH_SHORT).show();
            } else {
                AlertDialog alertDialog;
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View vobj = LayoutInflater.from(getContext()).inflate(R.layout.layout_payment_method_slector, null);
                builder.setView(vobj);
                ConstraintLayout cashMethodBtn = vobj.findViewById(R.id.cash_payment_mode);
                ConstraintLayout onlinePaymentMode = vobj.findViewById(R.id.online_payment_mode);
                alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
                if (!isCodAvailable) {
                    cashMethodBtn.setOnClickListener(v12 -> Toast.makeText(getContext(), "Asegúrese de que el CODPOSTAL esté disponible para la dirección que seleccionó.", Toast.LENGTH_SHORT).show());
                    onlinePaymentMode.setOnClickListener(v1 -> {
                        alertDialog.cancel();
                        paymentType.setText(R.string.onlineTxt);
                    });
                    cashMethodBtn.setAlpha(0.5f);
                } else {
                    cashMethodBtn.setOnClickListener(v14 -> {
                        alertDialog.cancel();
                        paymentType.setText(R.string.efectivoActivo);
                    });
                    onlinePaymentMode.setOnClickListener(v13 -> {
                        alertDialog.cancel();
                        paymentType.setText(R.string.onlineTxt);
                    });
                }
            }

        });
        continueBtn.setOnClickListener(v -> {
            if (address.getText().toString().equals("selecciona una dirección") && showPriceBtn.getVisibility() == View.GONE) {
                Toast.makeText(getContext(), "Por favor complete todos los detalles", Toast.LENGTH_SHORT).show();
            } else {
                object = new PedidosObject();
                object.setPrecioTotal(String.valueOf(totalPriceOfItem + deliveryCharges));
                object.setIdDireccion(SplashScreen.sharedPreferences.getString("idDireccion", null));
                object.setEstado("preparando");
                object.setNumeroTlf(SplashScreen.sharedPreferences.getString("numeroTlf", null));
                HashMap<String, HashMap<String, String>> hashMap = new HashMap<>();
                HashMap<String, String> child = new HashMap<>();
                for (int i = 0; i < arrayList.size(); i++) {
                    child.put("imgMenor", arrayList.get(i).getImagen());
                    child.put("titulo", arrayList.get(i).getTitulo());
                    child.put("unidad", arrayList.get(i).getUnidadMedida());
                    child.put("cantidad", arrayList.get(i).getCantidad());
                    child.put("precio", String.valueOf(arrayList.get(i).getPrecioCantidad()));
                    hashMap.put(arrayList.get(i).getIdProducto(), child);
                    child = new HashMap<>();
                }
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy | HH:mm", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());
                object.setFecha(currentDateandTime);
                object.setProductos(hashMap);
                if (paymentType.getText().toString().equals(getString(R.string.efectivoActivo))) {
                    object.setTipoPago("efectivo");
                    asignOrderToCustomer(false);
                } else {
                    object.setTipoPago("online");
                    asignOrderToCustomer(true);
                }


            }
        });
    }

    addressSelectAdapter addressSelectAdapter;

    private void openAddressDialog() {
        progressBar.setVisibility(View.VISIBLE);
        bottomSheetDialogAddress = new BottomSheetDialog(requireContext());
        View customLayout = LayoutInflater.from(getContext()).inflate(R.layout.layout_bottom_select_address, null);
        bottomSheetDialogAddress.setContentView(customLayout);
        bottomSheetDialogAddress.show();
        RecyclerView recyclerViewAddress = customLayout.findViewById(R.id.recycler_view_select_address);
        ArrayList<String> address = new ArrayList<>();
        ArrayList<String> phoneNumber = new ArrayList<>();
        ArrayList<String> pinCode = new ArrayList<>();
        ArrayList<String> addressId = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("direccionesEnvio").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recyclerViewAddress.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                if (snapshot.exists()) {
                    HashMap<String, HashMap<String, String>> hashMap = (HashMap<String, HashMap<String, String>>) snapshot.getValue();
                    HashMap<String, String> child = new HashMap<>();
                    for (int i = 0; i < Objects.requireNonNull(hashMap).size(); i++) {
                        child = hashMap.get(String.valueOf(hashMap.keySet().toArray()[i]));
                        address.add(Objects.requireNonNull(child).get("nombre") + " - " + child.get("ciudad"));
                        pinCode.add(child.get("codpostal"));
                        phoneNumber.add(child.get("numeroTlf"));
                        addressId.add(String.valueOf(hashMap.keySet().toArray()[i]));
                    }
                    if (address.size() == 0) {
                        Toast.makeText(getContext(), "OOPS! No funciona esa dirección", Toast.LENGTH_SHORT).show();
                    }else{
                        addressSelectAdapter = new addressSelectAdapter(phoneNumber, addressId, address, pinCode, getContext());
                        recyclerViewAddress.setAdapter(addressSelectAdapter);

                    }
                } else {
                    addressSelectAdapter = new addressSelectAdapter(phoneNumber, addressId, address, pinCode, getContext());
                    phoneNumber.add("c");
                    addressId.add("c");
                    pinCode.add("c");
                    address.add("c");
                    addressSelectAdapter.notifyDataSetChanged();
                    recyclerViewAddress.setAdapter(addressSelectAdapter);

                }
                progressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getProductsId() {
        try {
            Set<String> stringSet = SplashScreen.sharedPreferences.getStringSet("productos_carrito", null);
            if (stringSet != null) {
                if (stringSet.size() != 0) {
                    for (int i = 0; i < stringSet.size(); i++) {
                        int finalI = i;
                        FirebaseDatabase.getInstance().getReference("productos")
                                .child(stringSet.toArray()[i] + "").
                                addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            HashMap<String, String> hashMap = (HashMap<String, String>) snapshot.getValue();
                                            arrayList.add(new modelCarrito(Double.parseDouble(Objects.requireNonNull(hashMap.get("precio").replace(Constants.SIGNO_MONEDA, ""))), stringSet.toArray()[finalI] + "", "Price 1 " + hashMap.get("unidad"), hashMap.get("titulo"), "Price 1 " + hashMap.get("unidad"), hashMap.get("imgMenor"), hashMap.get("precio"), "1"));
                                        }
                                        shimmerRecycler.setVisibility(View.INVISIBLE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        cartAdapter.notifyDataSetChanged();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    SplashScreen.sharedPreferences.edit().putStringSet("productos_carrito", stringSet).apply();
                } else {
                    linearLayout.setVisibility(View.VISIBLE);
                    shimmerRecycler.setVisibility(View.INVISIBLE);
                    buyNowBtn.setVisibility(View.GONE);
                }
            } else {
                linearLayout.setVisibility(View.VISIBLE);
                buyNowBtn.setVisibility(View.GONE);
                shimmerRecycler.setVisibility(View.INVISIBLE);
            }
        } catch (Exception exception) {
            Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void asignOrderToCustomer(boolean tobeSent) {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase.getInstance().getReference("storeInfo").child("lastIndex")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int lastIndexNo;
                        if (snapshot.exists()) {
                            lastIndexNo = Integer.parseInt((String) snapshot.getValue());
                        } else {
                            lastIndexNo = 0;
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                        object.setIndex(String.valueOf(lastIndexNo + 1));
                        if (tobeSent) {
                            Intent intent = new Intent(getActivity(), PaymentActivity.class);
                            intent.putExtra("PedidosObject", object);
                            startActivity(intent);
                        } else {
                            progressBar.setVisibility(View.VISIBLE);
                            FirebaseDatabase.getInstance().getReference("pedidos").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .push().setValue(object).addOnSuccessListener(aVoid -> {
                                progressBar.setVisibility(View.VISIBLE);
                                FirebaseDatabase.getInstance().getReference("storeInfo").child("lastIndex")
                                        .setValue((lastIndexNo + 1) + "").addOnSuccessListener(aVoid1 -> {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    bottomSheetDialog.cancel();
                                    requireActivity().getSupportFragmentManager()
                                            .beginTransaction()
                                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                            .replace(R.id.frame_layout, new PedidoRealizado())
                                            .commit();
                                    sendOredrNotification();
                                });

                            }).addOnFailureListener(e -> {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendOredrNotification() {
        RequestQueue mRequestQue = Volley.newRequestQueue(getContext());
        JSONObject json = new JSONObject();
        try {
            json.put("to", "/topics/" + "admins");
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("titulo", "Nuevo Pedido");
            notificationObj.put("body", "Tiene un pedido de un cliente de" + object.getPrecioTotal() + " a las " + object.getFecha() + "\nEl pago se ha realizado a través de" + object.getTipoPago());
            //replace notification with data when went send data
            json.put("notification", notificationObj);

            String URL = getString(R.string.fcm_post_url);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    json,
                    response -> Log.d("S", "  "),
                    error -> Log.d("F", "  ")
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=" + Constants.FCM_SERVER_KEY);
                    return header;
                }
            };


            mRequestQue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}