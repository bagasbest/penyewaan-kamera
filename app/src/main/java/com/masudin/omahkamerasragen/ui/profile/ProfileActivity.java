package com.masudin.omahkamerasragen.ui.profile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.masudin.omahkamerasragen.LoginActivity;
import com.masudin.omahkamerasragen.R;
import com.masudin.omahkamerasragen.databinding.ActivityProfileBinding;
import com.masudin.omahkamerasragen.utils.Background;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private FirebaseUser user;

    // variabel
    private static final int REQUEST_FROM_GALLERY_TO_SELF_PHOTO = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        user = FirebaseAuth.getInstance().getCurrentUser();

        // KLIK PERBARUI PROFIL
        updateProfile();

        // KLIK PERBARUI USER DP
        updateUserDp();

        // kembali ke halaman sebelumnya
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        populateUI();
    }

    private void updateUserDp() {
        binding.updateUserDp.setOnClickListener(view -> {
            ImagePicker
                    .with(this)
                    .galleryOnly()
                    .compress(1024)
                    .start(REQUEST_FROM_GALLERY_TO_SELF_PHOTO);
        });
    }


    private void updateProfile() {
        binding.updateProfileBtn.setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setTitle("Konfirmasi Perbarui Profil")
                    .setMessage("Apakah kamu yakin ingin memperbarui profil, berdasarkan data yang telah kamu inputkan ?")
                    .setIcon(R.drawable.ic_baseline_warning_24)
                    .setPositiveButton("YA", (dialogInterface, i) -> {
                        // SIMPAN PERUBAHAN PROFIL PENGGUNA KE DATABASE
                        saveProfileChangesToDatabase();
                    })
                    .setNegativeButton("TIDAK", null)
                    .show();
        });
    }


    private void saveProfileChangesToDatabase() {
        String name = binding.nameEt.getText().toString().trim();
        String phone = binding.phoneEt.getText().toString().trim();
        String height = binding.usernameEt.getText().toString().trim();
        String weight = binding.addressEt.getText().toString().trim();

        // VALIDASI KOLOM PROFIL, JANGAN SAMPAI ADA YANG KOSONG
        if (name.isEmpty()) {
            binding.nameEt.setError("Nama Lengkap tidak boleh kosong");
            return;
        } else if (phone.isEmpty()) {
            binding.phoneEt.setError("Nomor Telepon tidak boleh kosong");
            return;
        } else if (height.isEmpty()) {
            binding.usernameEt.setError("Username tidak boleh kosong");
            return;
        } else if (weight.isEmpty()) {
            binding.addressEt.setError("Alamat tidak boleh kosong");
            return;
        }

        Map<String, Object> updateProfile = new HashMap<>();
        updateProfile.put("name", name);
        updateProfile.put("phone", phone);
        updateProfile.put("address", height);
        updateProfile.put("username", weight);

        // SIMPAN PERUBAHAN PROFIL TERBARU KE DATABASE
        binding.progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(user.getUid())
                .update(updateProfile)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Berhasil memperbarui profil", Toast.LENGTH_SHORT).show();
                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Gagal memperbarui profil", Toast.LENGTH_SHORT).show();
                        Log.e("Error update profil", task.toString());
                    }
                });

    }

    private void populateUI() {
            // TERAPKAN BACKGROUND SESUAI WAKTU
            Background.setBackgroundImage(this, binding.imageView2);


            // AMBIL DATA PENGGUNA DARI DATABASE, UNTUK DITAMPILKAN SEBAGAI PROFIL
            FirebaseFirestore
                    .getInstance()
                    .collection("users")
                    .document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String name = "" + documentSnapshot.get("name").toString();
                        String email = "" + documentSnapshot.get("email").toString();
                        String phone = "" + documentSnapshot.get("phone").toString();
                        String gender = "" + documentSnapshot.get("gender").toString();
                        String address = "" + documentSnapshot.get("address").toString();
                        String username = "" + documentSnapshot.get("username").toString();
                        String userDp = "" + documentSnapshot.get("dp").toString();

                        //TERAPKAN PADA UI PROFIL
                        binding.nameEt.setText(name);
                        binding.emailEt.setText(email);
                        binding.phoneEt.setText(phone);
                        binding.addressEt.setText(address);
                        binding.usernameEt.setText(username);

                        Glide.with(this)
                                .load(userDp)
                                .error(R.drawable.ic_baseline_face_24)
                                .into(binding.userDp);

                        Log.e("TAG", userDp);

                        if (gender.equals("Laki-laki")) {
                            binding.male.setChecked(true);
                        } else {
                            binding.female.setChecked(true);
                        }
                        binding.male.setEnabled(false);
                        binding.female.setEnabled(false);
                        binding.emailEt.setEnabled(false);

                    })
                    .addOnFailureListener(e -> {
                        Log.e("Error get profil", e.toString());
                        Toast.makeText(this, "Gagal mengambil data pengguna", Toast.LENGTH_SHORT).show();
                    });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        binding.progressBar.setVisibility(View.VISIBLE);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_FROM_GALLERY_TO_SELF_PHOTO) {
                ProfileDatabase.uploadImageToDatabase(data.getData(), this, user.getUid());
                Glide.with(this)
                        .load(data.getData())
                        .into(binding.userDp);
                binding.progressBar.setVisibility(View.GONE);
            }
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}