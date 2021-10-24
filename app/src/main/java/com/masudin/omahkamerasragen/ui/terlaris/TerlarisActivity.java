package com.masudin.omahkamerasragen.ui.terlaris;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.masudin.omahkamerasragen.R;
import com.masudin.omahkamerasragen.databinding.ActivityTerlarisBinding;
import com.masudin.omahkamerasragen.ui.camera.CameraAdapter;
import com.masudin.omahkamerasragen.ui.camera.CameraViewModel;
import com.masudin.omahkamerasragen.ui.product.ProductAdapter;
import com.masudin.omahkamerasragen.ui.product.ProductViewModel;

public class TerlarisActivity extends AppCompatActivity {

    /// inisiasi variabel, diperlukan supaya aplikasi tidak error saat dijalankan
    private ActivityTerlarisBinding binding;
    private CameraAdapter cameraAdapter;
    private ProductAdapter productAdapter;
    private String category = "";
    @Override
    protected void onResume() {
        super.onResume();
        if(category.equals("Aksesoris")) {
            initRecyclerViewProduct();
            initViewModelProduct();
        } else {
            initRecyclerViewCamera();
            initViewModelCamera();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTerlarisBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /// untuk memfilter kategori kamera, atau aksesoris
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.kategori, android.R.layout.simple_list_item_1);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        binding.categoryEt.setAdapter(adapter);
        binding.categoryEt.setOnItemClickListener((adapterView, view, i, l) -> {
            category = binding.categoryEt.getText().toString();

            if(category.equals("Kamera")) {
                initRecyclerViewCamera();
                initViewModelCamera();
            } else {
                initRecyclerViewProduct();
                initViewModelProduct();
            }

        });

        /// kembali
        binding.backButton.setOnClickListener(view -> onBackPressed());

    }

    /// FUNGSI UNTUK MENAMPILKAN LIST DATA terlaris
    private void initRecyclerViewCamera() {
        binding.rvTerlaris.setLayoutManager(new LinearLayoutManager(this));
        cameraAdapter = new CameraAdapter();
        binding.rvTerlaris.setAdapter(cameraAdapter);
    }


    /// FUNGSI UNTUK MENDAPATKAN LIST DATA terlaris DARI FIREBASE
    private void initViewModelCamera() {
        CameraViewModel viewModel = new ViewModelProvider(this).get(CameraViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.setCameraTerlaris();
        viewModel.getCameraList().observe(this, camera -> {
            if (camera.size() > 0) {
                binding.noData.setVisibility(View.GONE);
                cameraAdapter.setData(camera);
            } else {
                binding.noData.setVisibility(View.VISIBLE);
            }
            binding.progressBar.setVisibility(View.GONE);
        });
    }


    private void initRecyclerViewProduct() {
        binding.rvTerlaris.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter();
        binding.rvTerlaris.setAdapter(productAdapter);
    }

    private void initViewModelProduct() {
        ProductViewModel viewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.setProductTerlaris();
        viewModel.getProduct().observe(this, product -> {
            if (product.size() > 0) {
                binding.noData.setVisibility(View.GONE);
                productAdapter.setData(product);
            } else {
                binding.noData.setVisibility(View.VISIBLE);
            }
            binding.progressBar.setVisibility(View.GONE);
        });
    }


    /// HAPUSKAN ACTIVITY KETIKA SUDAH TIDAK DIGUNAKAN, AGAR MENGURANGI RISIKO MEMORY LEAKS
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}