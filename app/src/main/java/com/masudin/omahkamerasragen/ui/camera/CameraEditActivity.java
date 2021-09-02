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
import com.masudin.omahkamerasragen.databinding.ActivityCameraEditBinding;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;

public class CameraEditActivity extends AppCompatActivity {

    public static final String EXTRA_EDIT = "edit";
    private ActivityCameraEditBinding binding;
    private String dp;
    private CameraModel model;
    private static final int REQUEST_FROM_GALLERY = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCameraEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        model = getIntent().getParcelableExtra(EXTRA_EDIT);

        Glide.with(this)
                .load(model.getDp())
                .into(binding.ArticleDp);

        binding.nameEt.setText(model.getName());
        binding.merkEt.setText(model.getMerk());
        binding.price.setText(model.getPrice());
        binding.price2.setText(model.getPrice2());
        binding.price3.setText(model.getPrice3());
        binding.facility.setText(model.getFacility());
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
                ImagePicker.with(CameraEditActivity.this)
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
                updateCamera();
            }
        });
    }

    private void updateCamera() {

        String name = binding.nameEt.getText().toString().trim();
        String merk = binding.merkEt.getText().toString().trim();
        String desc = binding.description.getText().toString();
        String price = binding.price.getText().toString().trim();
        String price2 = binding.price2.getText().toString().trim();
        String price3 = binding.price3.getText().toString().trim();

        if(name.isEmpty()) {
            Toast.makeText(CameraEditActivity.this, "Nama Kamera tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(merk.isEmpty()) {
            Toast.makeText(CameraEditActivity.this, "Merk Kamera tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(desc.isEmpty()) {
            Toast.makeText(CameraEditActivity.this, "Deskripsi Kamera tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(price.isEmpty()) {
            Toast.makeText(CameraEditActivity.this, "Harga Kamera (6 Jam) tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(price2.isEmpty()) {
            Toast.makeText(CameraEditActivity.this, "Harga Kamera (12 Jam) tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(price3.isEmpty()) {
            Toast.makeText(CameraEditActivity.this, "Harga Kamera (24 Jam) tidak boleh kosong", Toast.LENGTH_SHORT).show();
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
                .collection("camera")
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

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Berhasil Mengunggah Peralatan Kamera")
                .setMessage("Peralatan Kamera akan segera terbit, anda dapat mengedit atau menghapus peralatan kamera jika terdapat kesalahan")
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
                                    Toast.makeText(CameraEditActivity.this, "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show();
                                    Log.d("imageDp: ", e.toString());
                                }))
                .addOnFailureListener(e -> {
                    mProgressDialog.dismiss();
                    Toast.makeText(CameraEditActivity.this, "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show();
                    Log.d("imageDp: ", e.toString());
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}