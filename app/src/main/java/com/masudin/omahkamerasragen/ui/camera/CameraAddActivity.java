package com.masudin.omahkamerasragen.ui.camera;

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
import com.masudin.omahkamerasragen.databinding.ActivityCameraAddBinding;
import com.masudin.omahkamerasragen.ui.product.ProductAddActivity;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CameraAddActivity extends AppCompatActivity {

    private ActivityCameraAddBinding binding;
    private String dp;
    private static final int REQUEST_FROM_GALLERY = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCameraAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // unggah peralatan kamera
        binding.uploadArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadCamera();
            }
        });

        // KLIK TAMBAH GAMBAR
        binding.imageHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(CameraAddActivity.this)
                        .galleryOnly()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start(REQUEST_FROM_GALLERY);
            }
        });

        binding.backButton.setOnClickListener(view -> onBackPressed());
    }

    private void uploadCamera() {

        String name = binding.nameEt.getText().toString().trim();
        String merk = binding.merkEt.getText().toString().trim();
        String desc = binding.description.getText().toString();
        String facility = binding.facility.getText().toString();
        String price = binding.price.getText().toString().trim();
        String price2 = binding.price2.getText().toString().trim();
        String price3 = binding.price3.getText().toString().trim();

        if(name.isEmpty()) {
            Toast.makeText(CameraAddActivity.this, "Nama Kamera tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(merk.isEmpty()) {
            Toast.makeText(CameraAddActivity.this, "Merk Kamera tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(desc.isEmpty()) {
            Toast.makeText(CameraAddActivity.this, "Deskripsi Kamera tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(facility.isEmpty()) {
            Toast.makeText(CameraAddActivity.this, "Fasilitas Kamera tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(price.isEmpty()) {
            Toast.makeText(CameraAddActivity.this, "Harga Kamera (6 Jam) tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(price2.isEmpty()) {
            Toast.makeText(CameraAddActivity.this, "Harga Kamera (12 Jam) tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(price3.isEmpty()) {
            Toast.makeText(CameraAddActivity.this, "Harga Kamera (24 Jam) tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(dp == null) {
            Toast.makeText(CameraAddActivity.this, "Gambar Kamera tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        String uid = String.valueOf(System.currentTimeMillis());

        // SIMPAN DATA PERALATAN KAMERA KE DATABASE
        Map<String, Object> product = new HashMap<>();
        product.put("name", name);
        product.put("description", desc);
        product.put("facility", facility);
        product.put("merk", merk);
        product.put("price", price);
        product.put("price2", price2);
        product.put("price3", price3);
        product.put("uid", uid);
        product.put("dp", dp);


        FirebaseFirestore
                .getInstance()
                .collection("camera")
                .document(uid)
                .set(product)
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

    private void showFailureDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Gagal Mengunggah Kamera")
                .setMessage("Terdapat kesalahan ketika mengunggah kamera, silahkan periksa koneksi internet anda, dan coba lagi nanti")
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    onBackPressed();
                })
                .show();
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Berhasil Mengunggah Kamera")
                .setMessage("Kamera akan segera terbit, anda dapat mengedit atau menghapus kamera jika terdapat kesalahan")
                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    onBackPressed();
                })
                .show();
    }


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

    private void uploadArticleDp(Uri data) {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        ProgressDialog mProgressDialog = new ProgressDialog(this);

        mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        String imageFileName = "kamera/data_" + System.currentTimeMillis() + ".png";

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
                                    Toast.makeText(CameraAddActivity.this, "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show();
                                    Log.d("imageDp: ", e.toString());
                                }))
                .addOnFailureListener(e -> {
                    mProgressDialog.dismiss();
                    Toast.makeText(CameraAddActivity.this, "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show();
                    Log.d("imageDp: ", e.toString());
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}