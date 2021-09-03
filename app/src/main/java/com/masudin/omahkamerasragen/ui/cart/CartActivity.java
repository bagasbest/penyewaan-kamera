package com.masudin.omahkamerasragen.ui.cart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.masudin.omahkamerasragen.R;
import com.masudin.omahkamerasragen.databinding.ActivityCartBinding;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

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

        if(cartModelArrayList.size() > 0) {
            binding.button3.setVisibility(View.VISIBLE);
        }

        // kembali ke halaman sebelumnya
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // checkout semua produk
        binding.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkoutAllProduct();
            }
        });
    }

    private void checkoutAllProduct() {
        for(int i=0; i<cartModelArrayList.size(); i++) {
            Log.e("Tag", cartModelArrayList.get(i).getTotalPrice());
            totalPrice += Integer.parseInt(cartModelArrayList.get(i).getTotalPrice());
        }


        String transactionId = String.valueOf(System.currentTimeMillis());

        Map<String, Object> transaction = new HashMap<>();
        transaction.put("transactionId", transactionId);
        transaction.put("customerId", cartModelArrayList.get(0).getCustomerUid());
        transaction.put("status", "Belum Bayar");
        transaction.put("finalPrice", String.valueOf(totalPrice));
        transaction.put("data", cartModelArrayList);

        FirebaseFirestore
                .getInstance()
                .collection("transaction")
                .document(transactionId)
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
    }

    private void initRecyclerView() {
        binding.rvCart.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter("cart");
        binding.rvCart.setAdapter(adapter);
    }

    private void initViewModel() {
        // tampilkan daftar artikel di halaman artikel terkait pertanian
        CartViewModel viewModel = new ViewModelProvider(this).get(CartViewModel.class);
        cartModelArrayList = new ArrayList<>();

        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.setListCart(user.getUid(), cartModelArrayList);
        viewModel.getListCart().observe(this, product -> {
            if (product.size() > 0) {
                binding.noData.setVisibility(View.GONE);
                adapter.setData(product);
            } else {
                binding.noData.setVisibility(View.VISIBLE);
            }
            binding.progressBar.setVisibility(View.GONE);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}