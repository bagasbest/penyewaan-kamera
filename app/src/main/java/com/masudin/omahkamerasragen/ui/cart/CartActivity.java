package com.masudin.omahkamerasragen.ui.cart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.masudin.omahkamerasragen.HomepageActivity;
import com.masudin.omahkamerasragen.R;
import com.masudin.omahkamerasragen.databinding.ActivityCartBinding;
import com.masudin.omahkamerasragen.ui.history_transaction.HistoryTransactionActivity;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    /// inisiasi variabel, diperlukan supaya aplikasi tidak error saat dijalankan
    private ActivityCartBinding binding;
    private FirebaseUser user;
    private CartAdapter adapter;
    private int totalPrice = 0;
    private ArrayList<CartModel> cartModelArrayList;
    @Override
    protected void onResume() {
        super.onResume();
        initRecyclerView();
        initViewModel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        user = FirebaseAuth.getInstance().getCurrentUser();

        // kembali ke halaman sebelumnya
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this, HomepageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        // checkout semua produk yang ada di cart / keranjang
        binding.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialog();
            }
        });
    }


    /// dialog box, konfirmasi apakah ingin mengcheckout semua barang di keranjang ?
    private void showConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Melakukan Checkout")
                .setMessage("Apakah anda yakin ingin melakukan checkout barang ?")
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setPositiveButton("YAKIN", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    if(cartModelArrayList.get(0).getCategory().equals("Kamera")) {
                        checkoutAllProduct("CA-");
                    } else {
                        checkoutAllProduct("AK-");
                    }
                })
                .setNegativeButton("TIDAK", (dialog, i) -> {
                    dialog.dismiss();
                })
                .show();
    }


    //// setelah di konfirmasi, maka lakukan checkout barang 1 per 1, jika lebih dari satu, oleh karena itu ada perulangan di bawah, untuk melakukan checkout jika barang lebih dari 1
    private void checkoutAllProduct(String code) {
        ArrayList<String> listName = new ArrayList<>();

        for(int i=0; i<cartModelArrayList.size(); i++) {
            listName.add(cartModelArrayList.get(i).getName());
            totalPrice += Integer.parseInt(cartModelArrayList.get(i).getTotalPrice());

            /// untuk keperluan set notifikasi, jadi barang yang waktu penyewaan nya sama, nanti di transaksi akan muncul dialog, bahwa waktu penyewaan sama, maka user harus segera membayar
            Map<String, Object> notification = new HashMap<>();
            notification.put("cartId", cartModelArrayList.get(i).getCartId());
            notification.put("dateStart", cartModelArrayList.get(i).getDateStart());
            notification.put("dateFinish", cartModelArrayList.get(i).getDateFinish());
            notification.put("name", cartModelArrayList.get(i).getName());
            FirebaseFirestore
                    .getInstance()
                    .collection("notification")
                    .document(cartModelArrayList.get(i).getCartId())
                    .set(notification);

        }


        //// dari tiap tiap barang yang ada di dalam keranjang, di buatkan collection baru yaitu transaction
        //// collection transaction menampung data transaksi, pada barang yang di checkout dari keranjang
        String transactionId = String.valueOf(System.currentTimeMillis());
        Map<String, Object> transaction = new HashMap<>();
        transaction.put("transactionId", code+transactionId);
        transaction.put("customerId", cartModelArrayList.get(0).getCustomerUid());
        transaction.put("dateStart", cartModelArrayList.get(0).getDateStart());
        transaction.put("dateFinish", cartModelArrayList.get(0).getDateFinish());
        transaction.put("status", "Belum Bayar");
        transaction.put("finalPrice", String.valueOf(totalPrice));
        transaction.put("data", cartModelArrayList);
        transaction.put("name", listName);
        FirebaseFirestore
                .getInstance()
                .collection("transaction")
                .document(code+transactionId)
                .set(transaction)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            showSuccessDialog();
                        } else {
                            showFailureDialog();
                        }
                    }
                });

    }


    //// memunculkan dialog jika gagal checkout
    private void showFailureDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Gagal Melakukan Checkout")
                .setMessage("Terdapat kesalahan ketika melakukan checkout, silahkan periksa koneksi internet anda, dan coba lagi nanti")
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }


    //// memunculkan dialog jika berhasil checkout
    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Berhasil Melakukan Checkout")
                .setMessage("Anda dapat melihat produk checkout pada History Transaksi, kemudian melunasi pembayaran secara langsung pada alamat Omah Kamera Sragen")
                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    deleteCart();
                })
                .show();
    }


    /// fungsi untuk menghapus keranjang, setelah berhasil checkout, supaya keranjang bersih, setelah itu navigasi ke halaman transaksi
    private void deleteCart() {
        for(int i=0; i<cartModelArrayList.size(); i++) {
            FirebaseFirestore
                    .getInstance()
                    .collection("cart")
                    .document(cartModelArrayList.get(i).getCartId())
                    .delete();
        }
        initRecyclerView();
        initViewModel();
        startActivity(new Intent(CartActivity.this, HistoryTransactionActivity.class));
    }

    /// FUNGSI UNTUK MENAMPILKAN LIST DATA keranjang
    private void initRecyclerView() {
        binding.rvCart.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter("cart");
        binding.rvCart.setAdapter(adapter);
    }


    /// FUNGSI UNTUK MENDAPATKAN LIST DATA Keranjang DARI FIREBASE
    private void initViewModel() {
        // tampilkan daftar barang yang ada di keranjang
        CartViewModel viewModel = new ViewModelProvider(this).get(CartViewModel.class);
        cartModelArrayList = new ArrayList<>();

        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.setListCart(user.getUid(), cartModelArrayList);
        viewModel.getListCart().observe(this, product -> {
            if (product.size() > 0) {
                binding.button3.setVisibility(View.VISIBLE);
                binding.noData.setVisibility(View.GONE);
                adapter.setData(product);
            } else {
                binding.button3.setVisibility(View.GONE);
                binding.noData.setVisibility(View.VISIBLE);
            }
            binding.progressBar.setVisibility(View.GONE);
        });
    }


    /// ketika klik kembali, maka akan menghapus data sebelumnya
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            Intent intent = new Intent(CartActivity.this, HomepageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
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