package com.masudin.omahkamerasragen.ui.cart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.masudin.omahkamerasragen.R;
import com.masudin.omahkamerasragen.databinding.ActivityCartBinding;
import com.masudin.omahkamerasragen.ui.product.ProductAdapter;
import com.masudin.omahkamerasragen.ui.product.ProductViewModel;

public class CartActivity extends AppCompatActivity {

    private ActivityCartBinding binding;
    private FirebaseUser user;
    private CartAdapter adapter;

    @Override
    protected void onResume() {
        super.onResume();
        initRecyclerView();
        initViewModel("Kamera");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        user = FirebaseAuth.getInstance().getCurrentUser();

        // PILIH KATEGORI (KAMERA/PERALATAN)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        binding.categoryEt.setAdapter(adapter);
        binding.categoryEt.setOnItemClickListener((adapterView, view, i, l) -> {
            initRecyclerView();
            initViewModel(binding.categoryEt.getText().toString());
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initRecyclerView() {
        binding.rvCart.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter();
        binding.rvCart.setAdapter(adapter);
    }

    private void initViewModel(String category) {
        // tampilkan daftar artikel di halaman artikel terkait pertanian
        CartViewModel viewModel = new ViewModelProvider(this).get(CartViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.setListCart(category, user.getUid());
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