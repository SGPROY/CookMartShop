package com.cookmart.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.cookmart.Constants;
import com.cookmart.HelperClasses.PedidosObject;
import com.cookmart.DetallesPedido;
import com.cookmart.R;

import java.util.ArrayList;

public class adapterPedidos extends RecyclerView.Adapter<adapterPedidos.viewHolder> {

    private final ArrayList<PedidosObject> arrayList;
    private final Context context;

    public adapterPedidos(ArrayList<PedidosObject> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(context).inflate(R.layout.layout_orders, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        if (arrayList.get(position).getTipoPago().equals("efectivo")) {
            holder.paymentType.setText(R.string.PAY_ON_DELIVERY);
        } else {
            holder.paymentType.setText(R.string.PAID_ONLINE);
        }
        String statusStr = arrayList.get(position).getEstado().replaceFirst(arrayList.get(position).getEstado().charAt(0) + "", (arrayList.get(position).getEstado().charAt(0) + "").toUpperCase());
        holder.orderStatus.setText(statusStr);
        holder.orderTimeStamp.setText(arrayList.get(position).getFecha());
        holder.orderId.setText(String.format("Id Pedido: #%s", arrayList.get(position).getIndex()));
        holder.price.setText((arrayList.get(position).getPrecioTotal()).concat(Constants.SIGNO_MONEDA));
        holder.constraintLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetallesPedido.class);
            intent.putExtra("PedidosObject", arrayList.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        private final TextView paymentType;
        private final TextView orderId;
        private final ConstraintLayout constraintLayout;
        private final TextView price;
        private final TextView orderTimeStamp;
        private final TextView orderStatus;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            price = itemView.findViewById(R.id.order_price);
            paymentType = itemView.findViewById(R.id.order_payment_type);
            orderId = itemView.findViewById(R.id.orders_order_id);
            orderTimeStamp = itemView.findViewById(R.id.order_timestamp);
            orderStatus = itemView.findViewById(R.id.orders_status);
            constraintLayout = itemView.findViewById(R.id.orders_main_container);
        }
    }
}
