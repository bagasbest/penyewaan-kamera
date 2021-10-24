package com.masudin.omahkamerasragen.ui.product;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.masudin.omahkamerasragen.R;
import com.masudin.omahkamerasragen.databinding.ActivityProductEditBinding;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ProductEditActivity extends AppCompatActivity {

    /// inisiasi variabel supaya tidak terjadi error pada aplikasi
    public static final String EXTRA_EDIT = "edit";
    private ActivityProductEditBinding binding;
    private String dp;
    private ProductModel model;
    private static final int REQUEST_FROM_GALLERY = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ///data dari kelas model terkait data kamera di panggil di activity ini, kemudian data tersebut di tampilkan pada halaman ini
        model = getIntent().getParcelableExtra(EXTRA_EDIT);
        Glide.with(this)
                .load(model.getDp())
                .into(binding.ArticleDp);

        binding.nameEt.setText(model.getName());
        binding.merkEt.setText(model.getMerk());
        binding.price.setText(model.getPrice());
        binding.price2.setText(model.getPrice2());
        binding.price3.setText(model.getPrice3());
        binding.description.setText(model.getDescription());


        // kembali ke halaman sebelumnya
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // KLIK Perbarui gambar
        binding.imageHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(ProductEditActivity.this)
                        .galleryOnly()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start(REQUEST_FROM_GALLERY);
            }
        });


        // KLIK UNGGAH ARTIKEL
        binding.uploadArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCameraUtilities();
            }
        });
    }


    /// fungsi yang berjalan ketika pengguna menekan klik pada unggah aksesoris
    private void updateCameraUtilities() {

        String name = binding.nameEt.getText().toString().trim();
        String merk = binding.merkEt.getText().toString().trim();
        String desc = binding.description.getText().toString();
        String price = binding.price.getText().toString().trim();
        String price2 = binding.price2.getText().toString().trim();
        String price3 = binding.price3.getText().toString().trim();

        //// validasi inputan
        if(name.isEmpty()) {
            Toast.makeText(ProductEditActivity.this, "Nama Produk tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(merk.isEmpty()) {
            Toast.makeText(ProductEditActivity.this, "Merk Produk tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(desc.isEmpty()) {
            Toast.makeText(ProductEditActivity.this, "Deskripsi Produk tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(price.isEmpty()) {
            Toast.makeText(ProductEditActivity.this, "Harga Produk (6 Jam) tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(price2.isEmpty()) {
            Toast.makeText(ProductEditActivity.this, "Harga Produk (12 Jam) tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(price3.isEmpty()) {
            Toast.makeText(ProductEditActivity.this, "Harga Produk (24 Jam) tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        // SIMPAN DATA PERALATAN KAMERA KE DATABASE
        Map<String, Object> product = new HashMap<>();
        product.put("name", name);
        product.put("description", desc);
        product.put("merk", merk);
        product.put("price", price);
        product.put("price2", price2);
        product.put("price3", price3);
        if(dp != null) {
            product.put("dp", dp);
        }
        FirebaseFirestore
                .getInstance()
                .collection("peralatan")
                .document(model.getUid())
                .update(product)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            binding.progressBar.setVisibility(View.GONE);
                            showSuccessDialog();
                        }
                        else {
                            binding.progressBar.setVisibility(View.GONE);
                            showFailureDialog();
                        }
                    }
                });

    }

    /// tampilkan dialog box jika gagal add data
    private void showFailureDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Gagal Mengunggah Peralatan Kamera")
                .setMessage("Terdapat kesalahan ketika mengunggah peralatan kamera, silahkan periksa koneksi internet anda, dan coba lagi nanti")
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    onBackPressed();
                })
                .show();
    }


    /// tampilkan dialog box jika sukses
    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Berhasil Mengunggah Peralatan Kamera")
                .setMessage("Peralatan Kamera akan segera terbit, anda dapat mengedit atau menghapus peralatan kamera jika terdapat kesalahan")
                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    onBackPressed();
                    onBackPressed();
                })
                .show();
    }

    /// fungsi untuk memvalidasi kode berdasarkan inisiasi variabel di atas tadi
    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_FROM_GALLERY) {
                uploadArticleDp(data.getData());
            }
        }
    }

    /// fungsi untuk mengupload foto kedalam cloud storage
    private void uploadArticleDp(Uri data) {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        ProgressDialog mProgressDialog = new ProgressDialog(this);

        mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        String imageFileName = "peralatan_kamera/data_" + System.currentTimeMillis() + ".png";

        mStorageRef.child(imageFileName).putFile(data)
                .addOnSuccessListener(taskSnapshot ->
                        mStorageRef.child(imageFileName).getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    mProgressDialog.dismiss();
                                    dp = uri.toString();
                                    binding.imageHint.setVisibility(View.GONE);
                                    Glide
                                            .with(this)
                                            .load(dp)
                                            .into(binding.ArticleDp);
                                })
                                .addOnFailureListener(e -> {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(ProductEditActivity.this, "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show();
                                    Log.d("imageDp: ", e.toString());
                                }))
                .addOnFailureListener(e -> {
                    mProgressDialog.dismiss();
                    Toast.makeText(ProductEditActivity.this, "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show();
                    Log.d("imageDp: ", e.toString());
                });
    }

    /// HAPUSKAN ACTIVITY KETIKA SUDAH TIDAK DIGUNAKAN, AGAR MENGURANGI RISIKO MEMORY LEAKS
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}