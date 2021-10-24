package com.masudin.omahkamerasragen.ui.terlaris;

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
import com.masudin.omahkamerasragen.ui.camera.CameraDetailActivity;
import com.masudin.omahkamerasragen.ui.camera.CameraModel;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class TerlarisAdapter extends RecyclerView.Adapter<TerlarisAdapter.ViewHolder> {

    /// INISIASI ARRAY LIST SEBAGAI PENAMPUNG LIST DATA terlaris
    private final ArrayList<CameraModel> listCamera = new ArrayList<>();
    public void setData(ArrayList<CameraModel> items) {
        listCamera.clear();
        listCamera.addAll(items);
        notifyDataSetChanged();
    }


    /// CASTING LAYOUT KE item_terlaris SUPAYA LIST DENDA DAPAT DI TAMPILKAN BERBENTUK LIST
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_terlaris, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(listCamera.get(position));
    }

    @Override
    public int getItemCount() {
        return listCamera.size();
    }


    //// FUNGSI UNTUK MEMASUKKAN DATA DARI ARRAY LIST DIATAS KEDALAM ATRIBUT, SEHINGGA TERLIHAT nama, DLL PADA LIST
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout cv;
        private ImageView dp;
        private TextView name, facility, price;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            dp = itemView.findViewById(R.id.dp);
            name = itemView.findViewById(R.id.name);
            facility = itemView.findViewById(R.id.facility);
            price = itemView.findViewById(R.id.price);
        }

        public void bind(CameraModel model) {
            NumberFormat formatter = new DecimalFormat("#,###");

            Glide.with(itemView.getContext())
                    .load(model.getDp())
                    .into(dp);

            name.setText(model.getName());
            facility.setText(model.getFacility());
            price.setText("IDR " + formatter.format(Double.parseDouble(model.getPrice())) + " ~ IDR " + formatter.format(Double.parseDouble(model.getPrice3())));

            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), CameraDetailActivity.class);
                    intent.putExtra(CameraDetailActivity.EXTRA_CAMERA, model);
                    itemView.getContext().startActivity(intent);
                }
            });


        }
    }
}
