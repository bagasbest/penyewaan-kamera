package com.masudin.omahkamerasragen.ui.history_transaction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.masudin.omahkamerasragen.R;
import com.masudin.omahkamerasragen.databinding.ActivityHistoryTransactionDetailBinding;
import com.masudin.omahkamerasragen.ui.cart.CartAdapter;
import com.masudin.omahkamerasragen.ui.cart.CartModel;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;

public class HistoryTransactionDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TRANSACTION = "transaction";
    private ActivityHistoryTransactionDetailBinding binding;
    private HistoryTransactionModel model;
    private FirebaseUser user;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryTransactionDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        user = FirebaseAuth.getInstance().getCurrentUser();

        model = getIntent().getParcelableExtra(EXTRA_TRANSACTION);
        NumberFormat formatter = new DecimalFormat("#,###");


        String duration = model.getData().get(0).getDuration();

        binding.transactionId.setText("Koda Transaksi: " + model.getTransactionId());
        binding.name.setText("Nama Penyewa: " + model.getData().get(0).getCustomerName());
        binding.dateStart.setText("Waktu Penyewaan: " + model.getData().get(0).getDateStart());
        if(duration.equals("6 Jam")) {
            binding.dateFinish.setText("Waktu Pengembalian: " + model.getData().get(0).getDateFinish() + ", maksimal Pukul 13.59");
        } else if(duration.equals("12 Jam")) {
            binding.dateFinish.setText("Waktu Pengembalian: " + model.getData().get(0).getDateFinish() + ", maksimal Pukul 19.59");
        } else {
            binding.dateFinish.setText("Waktu Pengembalian: " + model.getData().get(0).getDateFinish() + ", maksimal Pukul 07.59");
        }
        binding.finalPrice.setText("Biaya Sewa: IDR " + formatter.format(Double.parseDouble(model.getFinalPrice())));

        initRecyclerView();

        checkRole();

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // show info
        binding.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
            }
        });

        // lihat peta lokasi pembayaran
        binding.button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.app.goo.gl/1NjEZLyem51M4sx28"));
                startActivity(browserIntent);
            }
        });

        //verifikasi transaksi
        binding.verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(model.getStatus().equals("Belum Bayar")) {
                    showConfirmVerifyDialog();
                } else if(model.getStatus().equals("Sudah Bayar")) {
                    showConfirmFinishDialog();
                }
            }
        });


        // hapus transaksi
        binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDeleteDialog();
            }
        });

    }

    private void showConfirmVerifyDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi ACC Transaksi")
                .setMessage("Apakah anda yakin transaksi ini sudah dibayar ?")
                .setCancelable(false)
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setPositiveButton("YA", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    verifyTransaction();
                })
                .setNegativeButton("TIDAK", (dialog, i) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void showConfirmFinishDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Selesaikan Transaksi")
                .setMessage("Apakah anda yakin ingin menyelesaikan transaksi ini, dan barang sudah di kembalikan, serta denda sudah dibayarkan ?")
                .setCancelable(false)
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setPositiveButton("YA", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    finishTransaction();
                })
                .setNegativeButton("TIDAK", (dialog, i) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void finishTransaction() {
        FirebaseFirestore
                .getInstance()
                .collection("transaction")
                .document(model.getTransactionId())
                .update("status", "Selesai")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(HistoryTransactionDetailActivity.this, "Berhasil menyelesaikan transaksi ini", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        } else {
                            Toast.makeText(HistoryTransactionDetailActivity.this, "Gagal menyelesaikan transaksi ini", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void verifyTransaction() {
        FirebaseFirestore
                .getInstance()
                .collection("transaction")
                .document(model.getTransactionId())
                .update("status", "Sudah Bayar")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(HistoryTransactionDetailActivity.this, "Berhasil melakukan acc transaksi ini", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        } else {
                            Toast.makeText(HistoryTransactionDetailActivity.this, "Gagal melakukan acc pada transaksi ini", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showConfirmDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Menghapus Transaksi")
                .setMessage("Apakah anda yakin ingin menghapus transaksi ini ?")
                .setCancelable(false)
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setPositiveButton("YA", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    deleteTransaction();
                })
                .setNegativeButton("TIDAK", (dialog, i) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void deleteTransaction() {
        FirebaseFirestore
                .getInstance()
                .collection("transaction")
                .document(model.getTransactionId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(HistoryTransactionDetailActivity.this, "Berhasil menghapus riwayat transaksi ini", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        } else {
                            Toast.makeText(HistoryTransactionDetailActivity.this, "Gagal menghapus riwayat transaksi ini", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkRole() {
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(("" + documentSnapshot.get("role")).equals("admin")) {
                            if(!model.getStatus().equals("Selesai")) {
                                binding.verify.setVisibility(View.VISIBLE);
                            }
                            if(model.getStatus().equals("Belum Bayar") || model.getStatus().equals("Selesai")) {
                                binding.delete.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
    }

    private void showAlertDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Info Pembayaran")
                .setMessage("Syarat untuk melakukan pembayaran transaksi yaitu pelanggan yang tercepat membayar ke tempat perentalan kamera (Omah Kamera Sragen).\n\nJika sudah bayar akan di acc oleh admin, kemudian tanggal peminjaman akan di non-aktifkan agar tidak terjadi persewaan dengan tanggal yang sama.\n\nJika transaksi sudah di acc oleh admin, maka status akan merubah menjadi Sudah Bayar\n\nLokasi: Jl. Cemara, Mageru, Plumbungan, Kec. Karangmalang, Kabupaten Sragen, Jawa Tengah 57214")
                .setCancelable(false)
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setPositiveButton("Oke", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    private void initRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CartAdapter adapter = new CartAdapter("transaction");
        binding.recyclerView.setAdapter(adapter);
        adapter.setData((ArrayList<CartModel>) model.data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}