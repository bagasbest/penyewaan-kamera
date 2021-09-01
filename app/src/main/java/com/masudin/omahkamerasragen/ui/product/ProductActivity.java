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

    private ActivityProductBinding binding;
    private FirebaseUser user;
    private ProductAdapter adapter;

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

    private void initRecylerView() {
        binding.rvProduct.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter();
        binding.rvProduct.setAdapter(adapter);
    }

    private void initViewModel() {
        // tampilkan daftar artikel di halaman artikel terkait pertanian
        ProductViewModel viewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.setArticleList();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}