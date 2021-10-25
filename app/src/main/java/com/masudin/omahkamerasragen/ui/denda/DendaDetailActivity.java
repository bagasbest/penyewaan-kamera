package com.masudin.omahkamerasragen.ui.denda;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import com.masudin.omahkamerasragen.databinding.ActivityDendaDetailBinding;
import com.masudin.omahkamerasragen.ui.cart.CartAdapter;
import com.masudin.omahkamerasragen.ui.cart.CartModel;
import com.masudin.omahkamerasragen.ui.history_transaction.HistoryTransactionModel;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class DendaDetailActivity extends AppCompatActivity {

    /// inisiasi variable supaya aplikasi tidak error ketika dijalankan
    public static final String EXTRA_DENDA = "denda";
    public static final String DENDA = "extraCash";
    public static final String TELAT = "telat";
    private ActivityDendaDetailBinding binding;
    private HistoryTransactionModel model;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDendaDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /// model berfungsi untuk menampung data berdasarkan field-field contohnya nama, image, harga, dll,
        /// kemudian data dari model di ambil, dan di presentasikan di halaman detail denda
        model = getIntent().getParcelableExtra(EXTRA_DENDA);
        /// number format sama seperti yang saya jelaskan di adapter denda
        NumberFormat formatter = new DecimalFormat("#,###");

        /// kemudian data dari model di ambil, dan di presentasikan di halaman detail denda
        if(model.getData().get(0).getCategory().equals("Kamera")) {
            binding.transactionId.setText("Kode Transaksi: CA-" + model.getTransactionId());
        } else {
            binding.transactionId.setText("Kode Transaksi: AK-" + model.getTransactionId());
        }
        binding.borrower.setText("Nama Penyewa: " + model.getData().get(0).getCustomerName());
        binding.finalPrice.setText("Biaya Denda: IDR " + formatter.format(Double.parseDouble(String.valueOf(getIntent().getLongExtra(DENDA,0)))));
        binding.startDate.setText("Tanggal Peminjaman: " + model.getDateStart() + ", pukul " + model.getData().get(0).getPickHour());
        binding.telat.setText("Waktu Keterlambatan: " + getIntent().getStringExtra(TELAT));

        /// waktu pengembalian produk yang disewa
        /// konversi mil detik menjadi waktu pengembalian yang mudah dibaca
        /// contoh: 12302301293 -> 19:30
        long durationEndInMillis = model.getData().get(0).getDurationEnd();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date(durationEndInMillis);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String returnFormat = dateFormat.format(date);
        binding.finishDate.setText("Tanggal Pengembalian: " + model.getDateFinish() + ", pukul " + returnFormat);


        /// kembali ke halaman denda activity
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        /// tampilkan data barang yang di sewa, secara list atau terurut vertikal
        initRecyclerView();


        /// klik lokasi pembayaran
        binding.button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.app.goo.gl/1NjEZLyem51M4sx28"));
                startActivity(browserIntent);
            }
        });


    }

    /// tampilkan data barang yang di sewa, secara list atau terurut vertikal
    private void initRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CartAdapter adapter = new CartAdapter("transaction");
        binding.recyclerView.setAdapter(adapter);
        adapter.setData((ArrayList<CartModel>) model.data);
    }


    /// HAPUSKAN ACTIVITY KETIKA SUDAH TIDAK DIGUNAKAN, AGAR MENGURANGI RISIKO MEMORY LEAKS
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}