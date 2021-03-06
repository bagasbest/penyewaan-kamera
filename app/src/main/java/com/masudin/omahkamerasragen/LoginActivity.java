package com.masudin.omahkamerasragen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.masudin.omahkamerasragen.databinding.ActivityLoginBinding;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {

    /// inisisasi variabel supaya tidak error aplikasinya
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /// untuk memunculkan logo di login activity
        Glide.with(this)
                .load(R.drawable.logo)
                .into(binding.imageView7);

        // cek apakah user udah pernah login sebelumnya
        checkUserLoginOrNotBefore();

        // klik login
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        // klik registrasi
        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    /// fungsi untuk auto login, cek apakah user pernah login sebelumnya
    private void checkUserLoginOrNotBefore() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, HomepageActivity.class));
            finish();
        }
    }

    /// ketika klik register
    private void register() {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    /// ketika klik login, validasi inputan email & password
    private void login() {
        String email = binding.emailEt.getText().toString().trim();
        String password = binding.passwordEt.getText().toString().trim();

        if(email.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Kata Sandi tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        // login process
        binding.progressBar2.setVisibility(View.VISIBLE);
        FirebaseAuth
                .getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            binding.progressBar2.setVisibility(View.GONE);
                              startActivity(new Intent(LoginActivity.this, HomepageActivity.class));
                              finish();
                        } else {
                            binding.progressBar2.setVisibility(View.GONE);
                            showFailureDialog();
                        }
                    }
                });
    }

    /// jika gagal login, munculkan alert dialog gagal
    private void showFailureDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Gagal login")
                .setMessage("Terdapat kesalahan ketika login, silahkan periksa koneksi internet anda, dan coba lagi nanti")
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    /// HAPUSKAN ACTIVITY KETIKA SUDAH TIDAK DIGUNAKAN, AGAR MENGURANGI RISIKO MEMORY LEAKS
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}