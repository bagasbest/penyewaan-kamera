package com.masudin.omahkamerasragen.ui.booking;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.masudin.omahkamerasragen.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    /// INISIASI ARRAY LIST SEBAGAI PENAMPUNG LIST DATA BOOKING
    private final ArrayList<BookingModel> listBooking = new ArrayList<>();
    public void setData(ArrayList<BookingModel> items) {
        listBooking.clear();
        listBooking.addAll(items);
        notifyDataSetChanged();
    }

    /// CASTING LAYOUT KE item_booking SUPAYA LIST BOOKING DAPAT DI TAMPILKAN BERBENTUK URUTAN
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(listBooking.get(position));
    }

    @Override
    public int getItemCount() {
        return listBooking.size();
    }

    //// FUNGSI UNTUK MEMASUKKAN DATA DARI ARRAY LIST DIATAS KEDALAM ATRIBUT, SEHINGGA TERLIHAT KODE TRANSAKSI DLL PADA LIST
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tId, dateStart, dateFinish, name;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tId = itemView.findViewById(R.id.transactionId);
            dateFinish = itemView.findViewById(R.id.dateFinish);
            dateStart = itemView.findViewById(R.id.dateStart);
            name = itemView.findViewById(R.id.productName);
        }

        @SuppressLint("SetTextI18n")
        public void bind(BookingModel model) {
            if (model.getCategory().equals("Kamera")) {
                tId.setText("CA-" + model.getTransactionId());
            } else {
                tId.setText("AK-" + model.getTransactionId());
            }
            dateStart.setText(model.getDateStart());
            dateFinish.setText(model.getDateFinish());
            name.setText(model.getProductName());
        }
    }
}
