package com.masudin.omahkamerasragen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.bumptech.glide.Glide;
import com.masudin.omahkamerasragen.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    /// inisisasi variabel supaya tidak error aplikasinya
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Glide.with(this)
                .load(R.drawable.logo)
                .into(binding.imageView);

        /// fungsi untuk melakukan delay sehingga splash screen muncul selama 4 detik
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            // ke halaman login setelah 4 detik splash screen
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }, 4000);

    }

    /// HAPUSKAN ACTIVITY KETIKA SUDAH TIDAK DIGUNAKAN, AGAR MENGURANGI RISIKO MEMORY LEAKS
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}