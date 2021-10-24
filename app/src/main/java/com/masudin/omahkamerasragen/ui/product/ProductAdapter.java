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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.masudin.omahkamerasragen.R;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    /// INISIASI ARRAY LIST SEBAGAI PENAMPUNG LIST DATA AKSESORIS
    private final ArrayList<ProductModel> listProduct = new ArrayList<>();
    public void setData(ArrayList<ProductModel> items) {
        listProduct.clear();
        listProduct.addAll(items);
        notifyDataSetChanged();
    }


    /// CASTING LAYOUT KE item_peralatan SUPAYA LIST KAMERA DAPAT DI TAMPILKAN BERBENTUK URUTAN
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
        return listProduct.size();
    }



    //// FUNGSI UNTUK MEMASUKKAN DATA DARI ARRAY LIST DIATAS KEDALAM ATRIBUT, SEHINGGA TERLIHAT NAMA AKSESORIS, HARGA, DLL PADA LIST
    public class ViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout cv;
        private TextView name, price, status;
        private ImageView dp;
        private View bg;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            dp = itemView.findViewById(R.id.dp);
            bg = itemView.findViewById(R.id.view2);
            status = itemView.findViewById(R.id.status);
        }

        @SuppressLint("SetTextI18n")
        public void bind(ProductModel productModel) {

            NumberFormat formatter = new DecimalFormat("#,###");

            name.setText(productModel.getName());
            price.setText("IDR " + formatter.format(Double.parseDouble(productModel.getPrice()))  + " ~ IDR " + formatter.format(Double.parseDouble(productModel.getPrice3())));
            Glide.with(itemView.getContext())
                    .load(productModel.getDp())
                    .into(dp);
            status.setText(productModel.getStatus());

            /// JIKA STATUS READY
            if(productModel.getStatus().equals("ready")) {
                bg.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.rounded_bg2));

                cv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(itemView.getContext(), ProductDetailActivity.class);
                        intent.putExtra(ProductDetailActivity.EXTRA_PERALATAN, productModel);
                        itemView.getContext().startActivity(intent);
                    }
                });
            } else {
                /// JIKA TIDAK READY
                bg.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.rounded_bg));
            }
        }
    }
}
