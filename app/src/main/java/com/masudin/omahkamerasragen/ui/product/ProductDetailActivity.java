package com.masudin.omahkamerasragen.ui.product;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.masudin.omahkamerasragen.R;
import com.masudin.omahkamerasragen.databinding.ActivityProductDetailBinding;

import org.jetbrains.annotations.NotNull;

public class ProductDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PERALATAN = "peralatan";
    private ActivityProductDetailBinding binding;
    private ProductModel model;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        model = getIntent().getParcelableExtra(EXTRA_PERALATAN);
        Glide.with(this)
                .load(model.getDp())
                .into(binding.dp);

        binding.name.setText(model.getName());
        binding.merk.setText(model.getMerk());
        binding.description.setText(model.getDescription());
        binding.price.setText("Rp. " + model.getPrice());
        binding.price2.setText("Rp. " + model.getPrice2());
        binding.price3.setText("Rp. " + model.getPrice3());

        // cek apakah role == user / role == admin
        checkRole();

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // hapus barang
        binding.description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialog();
            }
        });

        // edit barang
        binding.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetailActivity.this, ProductEditActivity.class);
                intent.putExtra(ProductEditActivity.EXTRA_EDIT, model);
                startActivity(intent);
            }
        });

        // sewa perjam
        binding.sewaBerdasarkanJam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.sewaBerdasarkanJam.setVisibility(View.INVISIBLE);
                binding.jam.setVisibility(View.VISIBLE);
            }
        });

        // sewa per hari
        binding.sewaBerdasarkanHari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.sewaBerdasarkanJam.setVisibility(View.VISIBLE);
                binding.jam.setVisibility(View.GONE);

            }
        });

        // sewa 6 jam
        binding.priceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sewaPeralatanKameraPerHour(6);
            }
        });

        // sewa 12 jam
        binding.price2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sewaPeralatanKameraPerHour(12);
            }
        });

    }

    private void sewaPeralatanKameraPerHour(int hour) {
        // pilih tanggal peminjaman
        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Pilih Tanggal Penyewaan").setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            Log.e("TAG", datePicker.getHeaderText());
        });
    }

    private void showConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi menghapus peralatan kamera")
                .setMessage("Apakah anda yakin ingin menghapus peralatan kamera ini ?")
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setPositiveButton("YAKIN", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    deleteArticle();
                })
                .setNegativeButton("TIDAK", (dialog, i) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void deleteArticle() {
        ProgressDialog mProgressDialog = new ProgressDialog(this);

        mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        FirebaseFirestore
                .getInstance()
                .collection("peralatan")
                .document(model.getUid())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful())  {
                            mProgressDialog.dismiss();
                            Toast.makeText(ProductDetailActivity.this, "Berhasil menghapus peralatan kamera", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                        else {
                            mProgressDialog.dismiss();
                            Toast.makeText(ProductDetailActivity.this, "Gagal menghapus peralatan kamera", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkRole() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore
                    .getInstance()
                    .collection("users")
                    .document(user.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (("" + documentSnapshot.get("role")).equals("admin")) {
                                binding.edit.setVisibility(View.VISIBLE);
                                binding.delete.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}