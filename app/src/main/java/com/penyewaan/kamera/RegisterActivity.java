package com.penyewaan.kamera;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.penyewaan.kamera.databinding.ActivityRegisterBinding;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // klik tombol registrasi
        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrateUser();
            }
        });

        binding.login.setOnClickListener(view -> onBackPressed());

    }

    private void registrateUser() {
        String name = binding.nameEt.getText().toString().trim();
        String username = binding.usernameEt.getText().toString().trim();
        String nik = binding.nikEt.getText().toString().trim();
        String phone = binding.phoneEt.getText().toString().trim();
        String address = binding.addressEt.getText().toString().trim();
        String email = binding.emailEt.getText().toString().trim();
        String password = binding.passwordEt.getText().toString().trim();

        // PILIH JENIS KELAMIN
        int selectId = binding.radioGroup2.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectId);

        if(name.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Nama Lengkap tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(username.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Usia tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(nik.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "NIK tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(phone.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "No Telepon tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(address.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Alamat tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(email.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(password.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Kata Sandi tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (radioButton.getText().toString().isEmpty()) {
            Toast.makeText(this, "Jenis Kelamin tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        // simpan biodata kedalam database
        binding.progressBar3.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        Map<String, Object> register = new HashMap<>();
                        register.put("name", name);
                        register.put("username", username);
                        register.put("nik", nik);
                        register.put("phone", phone);
                        register.put("address", address);
                        register.put("email", email);
                        register.put("password", password);
                        register.put("gender", radioButton.getText().toString());
                        register.put("uid", uid);
                        register.put("role", "user");
                        register.put("dp", "");

                        FirebaseFirestore
                                .getInstance()
                                .collection("users")
                                .document(uid)
                                .set(register)
                                .addOnCompleteListener(task2 -> {
                                    if(task2.isSuccessful()) {
                                        binding.progressBar3.setVisibility(View.GONE);
                                        showSuccessDialog();
                                    }
                                    else {
                                        binding.progressBar3.setVisibility(View.GONE);
                                        showFailureDialog();
                                    }
                                });
                    } else {
                        binding.progressBar3.setVisibility(View.GONE);
                        showFailureDialog();
                    }
                });

    }

    private void showFailureDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Gagal melakukan registrasi")
                .setMessage("Silahkan mendaftar kembali dengan informasi yang benar")
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    onBackPressed();
                })
                .show();
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Berhasil melakukan registrasi")
                .setMessage("Silahkan login")
                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    onBackPressed();
                })
                .show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}