package com.cookmart.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cookmart.R;

import java.util.ArrayList;

public class notificationAdapter extends RecyclerView.Adapter<notificationAdapter.viewHolder> {

    ArrayList<String> fecha, titulo;
    Context context;

    public notificationAdapter(ArrayList<String> fecha, ArrayList<String> titulo, Context context) {
        this.fecha = fecha;
        this.titulo = titulo;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(context).inflate(R.layout.layout_notification, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.date.setText(fecha.get(position));
        holder.heading.setText(titulo.get(position));
    }

    @Override
    public int getItemCount() {
        return fecha.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        private final TextView date;
        private final TextView heading;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            heading = itemView.findViewById(R.id.notification_title);
            date = itemView.findViewById(R.id.notification_date);
        }
    }
}
