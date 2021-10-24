package com.masudin.omahkamerasragen.ui.product;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.masudin.omahkamerasragen.databinding.ActivityProductBinding;

public class ProductActivity extends AppCompatActivity {

    /// inisiasi variabel supaya tidak error ketika dijalankan
    private ActivityProductBinding binding;
    private FirebaseUser user;
    private ProductAdapter adapter;

    /// ON RESUME Activity sudah terlihat dan pengguna sudah dapat berinteraksi. Di sini adalah tempat terbaik untuk menjalankan animasi, membuka akses seperti aksesoris, mengupdate UI, dll.
    @Override
    protected void onResume() {
        super.onResume();
        initRecylerView();
        initViewModel();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        user = FirebaseAuth.getInstance().getCurrentUser();


        // cek role, hanya admin yang bisa CRUD produk kamera
        checkRole();


        // kembali ke halaman sebelumnya
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // tambah kamera
        binding.addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProductActivity.this, ProductAddActivity.class));
            }
        });
    }


    /// FUNGSI UNTUK MENAMPILKAN LIST DATA AKSESORIS
    private void initRecylerView() {
        binding.rvProduct.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter();
        binding.rvProduct.setAdapter(adapter);
    }


    /// FUNGSI UNTUK MENDAPATKAN LIST DATA AKSESORIS DARI FIREBASE
    private void initViewModel() {
        ProductViewModel viewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.setCameraUtilities();
        viewModel.getProduct().observe(this, product -> {
            if (product.size() > 0) {
                binding.noData.setVisibility(View.GONE);
                adapter.setData(product);
            } else {
                binding.noData.setVisibility(View.VISIBLE);
            }
            binding.progressBar.setVisibility(View.GONE);
        });
    }

    // cek role, hanya admin yang bisa CRUD produk AKSESORIS
    private void checkRole() {
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(("" + documentSnapshot.get("role")).equals("admin")) {
                            binding.addProduct.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }



    /// HAPUSKAN ACTIVITY KETIKA SUDAH TIDAK DIGUNAKAN, AGAR MENGURANGI RISIKO MEMORY LEAKS
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}