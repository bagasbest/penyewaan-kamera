package com.masudin.omahkamerasragen.ui.camera;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

public class CameraAdapter extends RecyclerView.Adapter<CameraAdapter.ViewHolder> {

    /// INISIASI ARRAY LIST SEBAGAI PENAMPUNG LIST DATA KAMERA
    private final ArrayList<CameraModel> listCamera = new ArrayList<>();
    public void setData(ArrayList<CameraModel> items) {
        listCamera.clear();
        listCamera.addAll(items);
        notifyDataSetChanged();
    }

    /// CASTING LAYOUT KE item_booking SUPAYA LIST KAMERA DAPAT DI TAMPILKAN BERBENTUK URUTAN
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_camera, parent, false);
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



    //// FUNGSI UNTUK MEMASUKKAN DATA DARI ARRAY LIST DIATAS KEDALAM ATRIBUT, SEHINGGA TERLIHAT NAMA KAMERA, HARGA, DLL PADA LIST
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout cv;
        private View bg;
        private ImageView dp;
        private TextView name, facility, price, status;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            dp = itemView.findViewById(R.id.dp);
            bg = itemView.findViewById(R.id.view2);
            name = itemView.findViewById(R.id.name);
            facility = itemView.findViewById(R.id.facility);
            price = itemView.findViewById(R.id.price);
            status = itemView.findViewById(R.id.status);
        }

        public void bind(CameraModel model) {

            NumberFormat formatter = new DecimalFormat("#,###");

            Glide.with(itemView.getContext())
                    .load(model.getDp())
                    .into(dp);

            name.setText(model.getName());
            facility.setText(model.getFacility());
            price.setText("IDR " + formatter.format(Double.parseDouble(model.getPrice())) + " ~ IDR " + formatter.format(Double.parseDouble(model.getPrice3())));
            status.setText(model.getStatus());


            /// JIKA STATUS READY
            if(model.getStatus().equals("ready")) {
                bg.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.rounded_bg2));

                /// KLIK DETAIL CAMERA
                cv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(itemView.getContext(), CameraDetailActivity.class);
                        intent.putExtra(CameraDetailActivity.EXTRA_CAMERA, model);
                        itemView.getContext().startActivity(intent);
                    }
                });

            } else {
                bg.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.rounded_bg));
            }



        }
    }
}
