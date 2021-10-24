package com.masudin.omahkamerasragen.ui.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.masudin.omahkamerasragen.R;
import com.masudin.omahkamerasragen.databinding.ActivityUserDetailBinding;

import org.jetbrains.annotations.NotNull;

public class UserDetailActivity extends AppCompatActivity {

    /// inisiasi variable supaya aplikasi tidak error ketika dijalankan
    public static final String EXTRA_USER = "user";
    private ActivityUserDetailBinding binding;
    private UserModel model;
    private FirebaseUser user;
    private String adminPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        user = FirebaseAuth.getInstance().getCurrentUser();

        /// model berfungsi untuk menampung data berdasarkan field-field contohnya nama, image dll,
        /// kemudian data dari model di ambil, dan di presentasikan di halaman detail user
        model = getIntent().getParcelableExtra(EXTRA_USER);

        /// jika admin, maka bisa hampus user
        if(!model.getEmail().equals(user.getEmail())) {
            binding.delete.setVisibility(View.VISIBLE);
        }


        /// jika tidak ada foto, tampilkan
        if(!model.getDp().isEmpty()) {
            Glide.with(UserDetailActivity.this)
                    .load(model.getDp())
                    .into(binding.roundedImageView);
        } else {
            Glide.with(UserDetailActivity.this)
                    .load(R.drawable.ic_baseline_face_24)
                    .into(binding.roundedImageView);
        }


        /// tampilkan informasi user
        binding.addressEt.setText(model.getAddress());
        binding.emailEt.setText(model.getEmail());
        binding.nameEt.setText(model.getName());
        binding.phoneEt.setText(model.getPhone());
        binding.nikEt.setText(model.getNik());
        binding.uidEt.setText(model.getUid());
        binding.usernameEt.setText(model.getUsername());
        if (model.getGender().equals("Laki-laki")) {
            binding.male.setChecked(true);
        } else {
            binding.female.setChecked(true);
        }


        // disable all form
        binding.male.setEnabled(false);
        binding.female.setEnabled(false);
        binding.emailEt.setEnabled(false);
        binding.addressEt.setEnabled(false);
        binding.nameEt.setEnabled(false);
        binding.phoneEt.setEnabled(false);
        binding.nikEt.setEnabled(false);
        binding.uidEt.setEnabled(false);
        binding.usernameEt.setEnabled(false);


        /// kembali ke halaman sebelumnya
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        /// hapus user
        binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDeleteUser();
            }
        });
    }


    /// konfirmasi hapus user
    private void showConfirmDeleteUser() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Menghapus Pengguna")
                .setMessage("Apakah anda yakin ingin menghapus pengguna ini ?")
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setPositiveButton("YA", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    deleteUser();
                })
                .setNegativeButton("TIDAK", (dialog, i) -> {
                    dialog.dismiss();
                })
                .show();
    }


    /// fungsi delete user
    private void deleteUser() {

        ProgressDialog mProgressDialog = new ProgressDialog(this);

        mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        String adminEmail = user.getEmail();
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        adminPassword = ""+documentSnapshot.get("password");
                    }
                });
        // SIGN OUT DARI AKUN ADMIN KEMUDIAN LOGIN MENGGUNAKAN AKUN PENGGUNA TERSEBUT UNTUK MENGHAPUS AUTENTIKASI PENGGUNA
        FirebaseAuth.getInstance().signOut();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(model.getEmail(), model.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {

                            // HAPUS AUTENTIKASI PENGGUNA TERSEBUT SEHINGGA PENGGUNA TERSEBUT TIDAK BISA LOGIN
                            FirebaseAuth
                                    .getInstance()
                                    .getCurrentUser()
                                    .delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                            if(task.isSuccessful()) {

                                                // LOGIN KEMBALI KE AKUN ADMIN DAN HAPUS DATA PENGGUNA TERSEBUT DARI DATABASE
                                                FirebaseAuth
                                                        .getInstance()
                                                        .signInWithEmailAndPassword(adminEmail, adminPassword)
                                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                            @Override
                                                            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                                                                if(task.isSuccessful()) {
                                                                    // HAPUS DATA PENGGUNA TERSEBUT DARI DATABASE
                                                                    deleteUserData(mProgressDialog);
                                                                } else {
                                                                    mProgressDialog.dismiss();
                                                                    Toast.makeText(UserDetailActivity.this, "Gagal menghapus akun", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            } else {
                                                mProgressDialog.dismiss();
                                                Toast.makeText(UserDetailActivity.this, "Gagal menghapus akun", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        else {
                            mProgressDialog.dismiss();
                            Toast.makeText(UserDetailActivity.this, "Gagal menghapus akun", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void deleteUserData(ProgressDialog mProgressDialog) {
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(model.getUid())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            mProgressDialog.dismiss();
                            Toast.makeText(UserDetailActivity.this, "Berhasil menghapus akun", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(UserDetailActivity.this, "Gagal menghapus akun", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    /// HAPUSKAN ACTIVITY KETIKA SUDAH TIDAK DIGUNAKAN, AGAR MENGURANGI RISIKO MEMORY LEAKS
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}