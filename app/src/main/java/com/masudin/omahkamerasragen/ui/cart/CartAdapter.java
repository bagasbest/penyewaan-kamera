package com.masudin.omahkamerasragen.ui.cart;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.masudin.omahkamerasragen.R;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private final ArrayList<CartModel> listCart = new ArrayList<>();

    String option;
    public CartAdapter(String option) {
        this.option = option;
    }

    public void setData(ArrayList<CartModel> items) {
        listCart.clear();
        listCart.addAll(items);
        notifyDataSetChanged();
    }


    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(listCart.get(position), option);
    }

    @Override
    public int getItemCount() {
        return listCart.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout cv;
        ImageView dp;
        TextView name, price, duration;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            dp = itemView.findViewById(R.id.dp);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            duration = itemView.findViewById(R.id.duration);
        }

        @SuppressLint("SetTextI18n")
        public void bind(CartModel model, String option) {
            NumberFormat formatter = new DecimalFormat("#,###");

            Glide.with(itemView.getContext())
                    .load(model.getDp())
                    .into(dp);

            name.setText(model.getName());
            price.setText("Biaya Sewa: IDR " + formatter.format(Double.parseDouble(model.getTotalPrice())));
            duration.setText("Durasi: " + model.getDuration());

            if(option.equals("cart")) {
                cv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(itemView.getContext(), CartDetailActivity.class);
                        intent.putExtra(CartDetailActivity.EXTRA_CART, model);
                        itemView.getContext().startActivity(intent);
                    }
                });
            }
        }
    }
}
