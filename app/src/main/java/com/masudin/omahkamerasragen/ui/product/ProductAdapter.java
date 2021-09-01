package com.masudin.omahkamerasragen.ui.product;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.masudin.omahkamerasragen.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private final ArrayList<ProductModel> listProduct = new ArrayList<>();
    public void setData(ArrayList<ProductModel> items) {
        listProduct.clear();
        listProduct.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_peralatan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(listProduct.get(position));
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cv;
        private TextView name, price;
        private ImageView dp;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            dp = itemView.findViewById(R.id.dp);
        }

        @SuppressLint("SetTextI18n")
        public void bind(ProductModel productModel) {

            name.setText(productModel.getName());
            price.setText("Rp. " + productModel.getPrice()  + " - Rp. " + productModel.getPrice3());
            Glide.with(itemView.getContext())
                    .load(productModel.getDp())
                    .into(dp);

            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), ProductDetailActivity.class);
                    intent.putExtra(ProductDetailActivity.EXTRA_PERALATAN, productModel);
                    itemView.getContext().startActivity(intent);
                }
            });

        }
    }
}
