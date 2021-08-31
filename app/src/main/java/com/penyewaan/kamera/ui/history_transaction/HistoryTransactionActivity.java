package com.penyewaan.kamera.ui.history_transaction;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.penyewaan.kamera.R;
import com.penyewaan.kamera.databinding.ActivityHistoryTransactionBinding;

public class HistoryTransactionActivity extends AppCompatActivity {

    private ActivityHistoryTransactionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}