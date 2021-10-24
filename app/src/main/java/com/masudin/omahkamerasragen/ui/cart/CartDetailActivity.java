package com.masudin.omahkamerasragen.ui.cart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.masudin.omahkamerasragen.R;
import com.masudin.omahkamerasragen.databinding.ActivityCartDetailBinding;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.TimeZone;

public class CartDetailActivity extends AppCompatActivity {

    /// inisiasi variable supaya aplikasi tidak error ketika dijalankan
    public static final String EXTRA_CART = "cart";
    private ActivityCartDetailBinding  binding;
    private CartModel model;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /// model berfungsi untuk menampung data berdasarkan field-field contohnya nama, image, harga, dll,
        /// kemudian data dari model di ambil, dan di presentasikan di halaman detail keranjang
        model = getIntent().getParcelableExtra(EXTRA_CART);
        NumberFormat formatter = new DecimalFormat("#,###");

        Glide.with(this)
                .load(model.getDp())
                .into(binding.dp);

        /// kemudian data dari model di ambil, dan di presentasikan di halaman detail keranjang
        binding.name.setText(model.getName());
        binding.merk.setText("Merk: " + model.getMerk());
        binding.dateStart.setText(model.getDateStart());
        binding.dateFinish.setText(model.getDateFinish());
        binding.price.setText("Biaya sewa: IDR " + formatter.format(Double.parseDouble(model.getPrice())));
        binding.duration.setText("Durasi sewa: " + model.getDuration());
        binding.totalPrice.setText("Total Biaya: IDR " + formatter.format(Double.parseDouble(model.getTotalPrice())));

        /// kemudian data dari model di ambil, dan di presentasikan di halaman detail keranjang
        if(model.getDuration().equals("6 Jam")) {
            long durationEndInMillis = model.getDurationEnd() - (1000*60*60*6);
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            Date date = new Date(durationEndInMillis);
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            String returnFormat = dateFormat.format(date);
            binding.jamAmbil.setText(returnFormat);
        } else if(model.getDuration().equals("12 Jam")) {
            long durationEndInMillis = model.getDurationEnd() - (1000*60*60*12);
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            Date date = new Date(durationEndInMillis);
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            String returnFormat = dateFormat.format(date);
            binding.jamAmbil.setText(returnFormat);
        } else {
            binding.jamAmbil.setText(model.getPickHour());
        }

        // kembali ke halaman sebelumnya
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CartDetailActivity.this, CartActivity.class));
                finish();
            }
        });

        // cancel button pada keranjang detail
        binding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationBeforeDelete();
            }
        });
    }

    /// konfirmasi cancel barang di keranjang
    private void showConfirmationBeforeDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi menghapus cart")
                .setMessage("Apakah anda yakin ingin menghapus cart ini ?")
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setPositiveButton("YAKIN", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    deleteCart();
                })
                .setNegativeButton("TIDAK", (dialog, i) -> {
                    dialog.dismiss();
                })
                .show();
    }

    /// ketika konfirmasi di tekan "YAKIN", maka, item keranjang tersebut akan di hapus dari keranjang
    private void deleteCart() {
        FirebaseFirestore
                .getInstance()
                .collection("cart")
                .document(model.getCartId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(CartDetailActivity.this, "Berhasil menghapus cart", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(CartDetailActivity.this, CartActivity.class));
                            finish();
                        }else {
                            Toast.makeText(CartDetailActivity.this, "Gagal menghapus kamera", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    /// kembali ke halaman Cart Activity
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            startActivity(new Intent(CartDetailActivity.this, CartActivity.class));
            finish();
            return true;
        }
        return false;
    }

    /// HAPUSKAN ACTIVITY KETIKA SUDAH TIDAK DIGUNAKAN, AGAR MENGURANGI RISIKO MEMORY LEAKS
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}