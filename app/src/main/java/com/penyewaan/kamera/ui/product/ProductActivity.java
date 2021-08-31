package com.penyewaan.kamera.ui.product;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.penyewaan.kamera.R;
import com.penyewaan.kamera.databinding.ActivityProductBinding;

public class ProductActivity extends AppCompatActivity {

    private ActivityProductBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}