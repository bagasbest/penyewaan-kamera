package com.penyewaan.kamera.ui.denda;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.penyewaan.kamera.R;
import com.penyewaan.kamera.databinding.ActivityDendaBinding;

public class DendaActivity extends AppCompatActivity {

    private ActivityDendaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDendaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}