package com.masudin.omahkamerasragen.ui.cart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.masudin.omahkamerasragen.R;
import com.masudin.omahkamerasragen.databinding.ActivityCartDetailBinding;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Currency;

public class CartDetailActivity extends AppCompatActivity {

    public static final String EXTRA_CART = "cart";
    private ActivityCartDetailBinding  binding;
    private CartModel model;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        model = getIntent().getParcelableExtra(EXTRA_CART);
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits(0);
        format.setCurrency(Currency.getInstance("IDR"));

        Glide.with(this)
                .load(model.getDp())
                .into(binding.dp);

        binding.name.setText(model.getName());
        binding.merk.setText("Merk: " + model.getMerk());
        binding.dateStart.setText(model.getDateStart());
        binding.dateFinish.setText(model.getDateFinish());
        binding.price.setText("Biaya sewa: " + format.format(Integer.parseInt(model.getPrice())));
        binding.duration.setText("Durasi sewa: " + model.getDuration());
        binding.totalPrice.setText("Rp. " + model.getTotalPrice());

        // kembali ke halaman sebelumnya
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // cancel button
        binding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationBeforeDelete();
            }
        });
    }

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
                            onBackPressed();
                        }else {
                            Toast.makeText(CartDetailActivity.this, "Gagal menghapus kamera", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}