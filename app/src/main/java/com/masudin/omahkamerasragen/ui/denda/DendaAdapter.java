package com.masudin.omahkamerasragen.ui.denda;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.masudin.omahkamerasragen.R;
import com.masudin.omahkamerasragen.ui.history_transaction.HistoryTransactionModel;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DendaAdapter extends RecyclerView.Adapter<DendaAdapter.ViewHolder> {

    /// INISIASI ARRAY LIST SEBAGAI PENAMPUNG LIST DATA DENDA
    private final ArrayList<HistoryTransactionModel> listDenda = new ArrayList<>();
    public void setData(ArrayList<HistoryTransactionModel> items) {
        listDenda.clear();
        listDenda.addAll(items);
        notifyDataSetChanged();
    }


    /// CASTING LAYOUT KE item_denda SUPAYA LIST DENDA DAPAT DI TAMPILKAN BERBENTUK LIST
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_denda, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(listDenda.get(position));
    }

    @Override
    public int getItemCount() {
        return listDenda.size();
    }


    //// FUNGSI UNTUK MEMASUKKAN DATA DARI ARRAY LIST DIATAS KEDALAM ATRIBUT, SEHINGGA TERLIHAT total denda, keterlambatan, waktu peminjaman, DLL PADA LIST
    public static class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout cl;
        TextView denda, transactionId, dateFinish, dateBackProduct;
        long extraCash = 0;
        long finalDateFinishInMillis = 0;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cl = itemView.findViewById(R.id.cv);
            denda = itemView.findViewById(R.id.denda);
            transactionId = itemView.findViewById(R.id.transactionId);
            dateBackProduct = itemView.findViewById(R.id.dateBackProduct);
            dateFinish = itemView.findViewById(R.id.dateFinish);

        }

        @SuppressLint("SetTextI18n")
        public void bind(HistoryTransactionModel historyTransactionModel) {

            /// number format digunakan untuk money currency, misal IDR. 100.000
            NumberFormat formatter = new DecimalFormat("#,###");


            transactionId.setText(historyTransactionModel.getTransactionId());
            dateFinish.setText(historyTransactionModel.getDateFinish());
            /// jika belum melewati batas pengembalian, maka waktu keterlambatan masih "0 Hari, 0 Jam".
            dateBackProduct.setText("0 Hari, 0 Jam");


            /// Simple date format digunakan untuk mengkonversi mil detik pada database, menjadi bentuk tanggal yang mudah dibaca
            /// contoh 123456789 menjadi -> 19-02-2021
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String duration = historyTransactionModel.data.get(0).getDuration();


            try {
                Date date = sdf.parse(historyTransactionModel.getData().get(0).getDateFinish());
                String nowDate = sdf1.format(new Date());
                Date nowDates = sdf1.parse(nowDate);

                long dateFinishInMillis = date.getTime();
                long dateNowInMillis = nowDates.getTime();

                /// kemudian cek, apakah pengguna menyewa 6, 12 jam, atau harian
                /// jika menyewa 6 atau 12 jam, maka waktu berakhirnya penyewaan adalah waktu ambil + 6 jam atau 12 jam kedepan
                /// contoh: saya mengambil jam 6 pagi, untuk sewa 6 jam kamera, maka waktu pengembalian pukuk 12 siang, dan denda berlaku jam 1 siang
                if(duration.equals("6 Jam")) {
                    finalDateFinishInMillis = dateFinishInMillis + historyTransactionModel.getData().get(0).getDurationEnd();
                } else if (duration.equals("12 Jam")) {
                    finalDateFinishInMillis = dateFinishInMillis +  historyTransactionModel.getData().get(0).getDurationEnd();
                } else  {
                    finalDateFinishInMillis = dateFinishInMillis;
                }

                Log.e("NOW", String.valueOf(sdf1.format(dateNowInMillis)));
                Log.e("FINISH", String.valueOf(sdf1.format(finalDateFinishInMillis)));


                /// ini kondisi untuk pengecekan, apakah waktu saat ini sudah melewati waktu pengembalian atau belum
                /// jika sudah melewati waktu pengembalian maka akan berlaku denda, perjam * 5000
                if(dateNowInMillis > finalDateFinishInMillis) {
                    long diff = dateNowInMillis - finalDateFinishInMillis;
                    long extendCash = diff / (1000*60*60);
                    /// extendCash adalah variabel yang menentukan sudah berapa jam denda terlewat
                    /// contoh: 5 jam telewat, maka 5 * 5000 = 25000 dendanya
                    extraCash = extendCash * 5000;
                    Log.e("extendCash", ""+diff + " / (1000*60*60) = " + extendCash);

                    /// setelah itu, di konversi menggunakan number format, sehingga menjadi IDR 25.000
                    denda.setText("IDR " + formatter.format(Double.parseDouble(String.valueOf(extraCash))));

                    /// set keterlambatan, contoh: keterlambatan 1 Hari, 20 Jam
                    long getDays = diff / (1000*60*60*24);
                    long getHours = (diff / (1000*60*60))%24;
                    dateBackProduct.setText(""+getDays + " Hari, "+ getHours + " Jam");

                }

            } catch (ParseException e){
                e.printStackTrace();
            }


            /// ketika klik item denda, maka akan diarahkan ke halaman detail denda
            cl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), DendaDetailActivity.class);
                    intent.putExtra(DendaDetailActivity.EXTRA_DENDA, historyTransactionModel);
                    intent.putExtra(DendaDetailActivity.DENDA, extraCash);
                    intent.putExtra(DendaDetailActivity.TELAT, dateBackProduct.getText().toString());
                    itemView.getContext().startActivity(intent);
                }
            });

        }
    }
}
