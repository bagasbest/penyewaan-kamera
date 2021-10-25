package com.masudin.omahkamerasragen.ui.history_transaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.masudin.omahkamerasragen.R;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;

public class HistoryTransactionAdapter extends RecyclerView.Adapter<HistoryTransactionAdapter.ViewHolder> {

    /// INISIASI ARRAY LIST SEBAGAI PENAMPUNG LIST DATA HISTORY TRANSAKSI
    private final ArrayList<HistoryTransactionModel> historyTransactionModelArrayList = new ArrayList<>();
    public void setData(ArrayList<HistoryTransactionModel> items) {
        historyTransactionModelArrayList.clear();
        historyTransactionModelArrayList.addAll(items);
        notifyDataSetChanged();
    }

    /// CASTING LAYOUT KE item_transaction SUPAYA LIST DENDA DAPAT DI TAMPILKAN BERBENTUK LIST
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(historyTransactionModelArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return historyTransactionModelArrayList.size();
    }


    //// FUNGSI UNTUK MEMASUKKAN DATA DARI ARRAY LIST DIATAS KEDALAM ATRIBUT, SEHINGGA TERLIHAT riwayat penyewaan, waktu penyewaan, waktu pengembalian DLL PADA LIST
    public static class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout cv;
        View view;
        TextView transactionId, dateStart, status, finalPrice;


        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            cv = itemView.findViewById(R.id.cv);
            transactionId = itemView.findViewById(R.id.transactionId);
            dateStart = itemView.findViewById(R.id.dateStart);
            status = itemView.findViewById(R.id.status);
            finalPrice = itemView.findViewById(R.id.finalPrice);
            view = itemView.findViewById(R.id.view8);
        }

        @SuppressLint("SetTextI18n")
        public void bind(HistoryTransactionModel model) {
            /// number format digunakan untuk money currency, misal IDR. 100.000
            NumberFormat formatter = new DecimalFormat("#,###");

            if(model.getData().get(0).getCategory().equals("Kamera")) {
                transactionId.setText("CA-"+model.getTransactionId());
            } else {
                transactionId.setText("AK-"+model.getTransactionId());
            }
            dateStart.setText(model.getDateStart());
            status.setText(model.getStatus());
            finalPrice.setText("IDR " + formatter.format(Double.parseDouble(model.getFinalPrice())));


            /// jika belum bayar maka background color nya berwarna merah
            if(model.getStatus().equals("Belum Bayar")) {
                view.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.rounded_bg));
            } else {
                /// jika sudah bayar / selesai, maka background color nya berwarna hijau
                view.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.rounded_bg2));
            }


            /// ketika klik salah satu item tranasaksi, maka ke halaman detail tranasaksi
            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), HistoryTransactionDetailActivity.class);
                    intent.putExtra(HistoryTransactionDetailActivity.EXTRA_TRANSACTION, model);
                    itemView.getContext().startActivity(intent);
                }
            });

        }
    }
}
