package com.cookmart.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cookmart.R;
import com.facebook.shimmer.ShimmerFrameLayout;

public class homeAdapterShimmer extends RecyclerView.Adapter<homeAdapterShimmer.viewholer> {

    private final Context context;
    private final int size;
    private final int layoutFromScreen;

    //1 means homepage
    //2 means cart
    public homeAdapterShimmer(Context context, int size, int layoutFromScreen) {
        this.context = context;
        this.layoutFromScreen = layoutFromScreen;
        this.size = size;
    }


    @NonNull
    @Override
    public viewholer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutFromScreen == 1) {
            return new viewholer(LayoutInflater.from(context).inflate(R.layout.layout_homepage_shimmer, parent, false));
        } else if (layoutFromScreen == 2) {
            return new viewholer(LayoutInflater.from(context).inflate(R.layout.layout_carrito_shimmer, parent, false));
        } else {
            return new viewholer(LayoutInflater.from(context).inflate(R.layout.layout_despensa_shimmer, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull viewholer holder, int position) {
        holder.shimmerFrameLayout.startShimmer();
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public class viewholer extends RecyclerView.ViewHolder {
        ShimmerFrameLayout shimmerFrameLayout;

        public viewholer(@NonNull View itemView) {
            super(itemView);
            shimmerFrameLayout = itemView.findViewById(R.id.shimmer_layout_homepage);
        }
    }
}
