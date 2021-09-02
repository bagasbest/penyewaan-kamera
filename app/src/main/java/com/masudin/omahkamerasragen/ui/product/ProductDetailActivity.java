package com.masudin.omahkamerasragen.ui.product;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.masudin.omahkamerasragen.R;
import com.masudin.omahkamerasragen.databinding.ActivityProductDetailBinding;
import org.jetbrains.annotations.NotNull;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
        binding.price.setText("Rp. " + model.getPrice() + " untuk 6 Jam Penyewaan");
        binding.price2.setText("Rp. " + model.getPrice2() + " untuk 12 Jam Penyewaan");
        binding.price3.setText("Rp. " + model.getPrice3() + " untuk 24 Jam Penyewaan");

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
                sewaPeralatanKameraPerDay();
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

    private void sewaPeralatanKameraPerDay() {
        // pilih tanggal peminjaman
        Calendar now = Calendar.getInstance();
        MaterialDatePicker datePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setSelection(Pair.create(now.getTimeInMillis(), now.getTimeInMillis())).build();

        datePicker.show(getSupportFragmentManager(), datePicker.toString());

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Pair prendiRange = (Pair) datePicker.getSelection();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String formatFirst = sdf.format(new Date(Long.parseLong(prendiRange.first.toString())));
            String formatSecond = sdf.format(new Date(Long.parseLong(prendiRange.second.toString())));
            new AlertDialog.Builder(this)
                    .setTitle("Konfirmasi Penyewaan Peralatan Kamera")
                    .setMessage("Apakah anda yakin ingin menyewa peralatan kamera ini ?\n\nJika ya, produk ini akan di tambahkan di keranjang")
                    .setIcon(R.drawable.ic_baseline_warning_24)
                    .setPositiveButton("OKE", (dialogInterface, i) -> {
                        long diff = Long.parseLong(prendiRange.second.toString()) - Long.parseLong(prendiRange.first.toString());
                        long diffDays = diff / (24 * 60 * 60 * 1000);
                        saveProductToCart(formatFirst, formatSecond, 24, diffDays);
                    })
                    .setNegativeButton("TIDAK", (dialog, i) -> {
                        dialog.dismiss();
                    })
                    .show();
        });
    }

    private void sewaPeralatanKameraPerHour(int hour) {
        // pilih tanggal peminjaman
        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker().build();
        datePicker.show(getSupportFragmentManager(), datePicker.toString());
        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String format = sdf.format(new Date(Long.parseLong(selection.toString())));
            new AlertDialog.Builder(this)
                    .setTitle("Konfirmasi Penyewaan Peralatan Kamera")
                    .setMessage("Apakah anda yakin ingin menyewa  peralatan kamera ini ?\n\nJika ya, produk ini akan di tambahkan di keranjang")
                    .setIcon(R.drawable.ic_baseline_warning_24)
                    .setPositiveButton("OKE", (dialogInterface, i) -> {
                        saveProductToCart(format, "", hour, 0);
                    })
                    .setNegativeButton("TIDAK", (dialog, i) -> {
                        dialog.dismiss();
                    })
                    .show();

        });

    }

    private void saveProductToCart(String first, String second, int hour, long difference) {
        ProgressDialog mProgressDialog = new ProgressDialog(this);

        mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String uid = String.valueOf(System.currentTimeMillis());

                        // SIMPAN DATA PERALATAN KAMERA KE DATABASE
                        Map<String, Object> addToCart = new HashMap<>();
                        addToCart.put("cartId", uid);
                        addToCart.put("name", model.getName());
                        addToCart.put("merk", model.getMerk());
                        addToCart.put("dp", model.getDp());
                        if(hour == 6) {
                            addToCart.put("duration", hour + " Jam");
                            addToCart.put("price", model.getPrice());
                            addToCart.put("dateStart", first);
                            addToCart.put("dateFinish", first);
                            addToCart.put("totalPrice", model.getPrice());
                        } else if(hour == 12) {
                            addToCart.put("duration", hour + " Jam");
                            addToCart.put("price", model.getPrice2());
                            addToCart.put("dateStart", first);
                            addToCart.put("dateFinish", first);
                            addToCart.put("totalPrice", model.getPrice2());
                        } else {
                            addToCart.put("duration", difference + " Hari");
                            addToCart.put("price", model.getPrice3());
                            addToCart.put("dateStart", first);
                            addToCart.put("dateFinish", second);
                            addToCart.put("totalPrice", Long.parseLong(model.getPrice3()) * difference);
                        }
                        addToCart.put("category", "Peralatan Kamera");
                        addToCart.put("customerUid", user.getUid());
                        addToCart.put("customerName", ""+documentSnapshot.get("name"));

                        FirebaseFirestore
                                .getInstance()
                                .collection("cart")
                                .document(uid)
                                .set(addToCart)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            mProgressDialog.dismiss();
                                            showSuccessDialog();
                                        }
                                        else {
                                            mProgressDialog.dismiss();
                                            showFailureDialog();
                                        }
                                    }
                                });
                    }
                });
    }

    private void showFailureDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Gagal Memasukkan Produk Kedalam Keranjang")
                .setMessage("Terdapat kesalahan ketika memasukkan produk kedalam keranjang, silahkan periksa koneksi internet anda, dan coba lagi nanti")
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
                .setMessage("Anda dapat melihat produk pada navigasi cart atau keranjang")
                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    onBackPressed();
                })
                .show();
    }

    private void showConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi menghapus peralatan kamera")
                .setMessage("Apakah anda yakin ingin menghapus peralatan kamera ini ?")
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setPositiveButton("YAKIN", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    deleteCameraUtilities();
                })
                .setNegativeButton("TIDAK", (dialog, i) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void deleteCameraUtilities() {
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