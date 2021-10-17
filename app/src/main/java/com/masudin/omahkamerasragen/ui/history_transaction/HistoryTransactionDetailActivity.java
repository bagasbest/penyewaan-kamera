package com.masudin.omahkamerasragen.ui.history_transaction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.masudin.omahkamerasragen.R;
import com.masudin.omahkamerasragen.databinding.ActivityHistoryTransactionDetailBinding;
import com.masudin.omahkamerasragen.ui.cart.CartAdapter;
import com.masudin.omahkamerasragen.ui.cart.CartModel;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class HistoryTransactionDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TRANSACTION = "transaction";
    private ActivityHistoryTransactionDetailBinding binding;
    private HistoryTransactionModel model;
    private FirebaseUser user;
    private int counterNotification = 0;

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
        binding.pickHour.setText("Jam Ambil: Pukul " + model.getData().get(0).getPickHour());

        /// waktu pengembalian
        long durationEndInMillis = model.getData().get(0).getDurationEnd();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date(durationEndInMillis);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String returnFormat = dateFormat.format(date);
        binding.dateFinish.setText("Waktu Pengembalian: " + model.getData().get(0).getDateFinish() + ", maksimal Pukul " + returnFormat);


        binding.finalPrice.setText("Biaya Sewa: IDR " + formatter.format(Double.parseDouble(model.getFinalPrice())));
//        if(duration.equals("6 Jam")) {
//            long durationEndInMillis = model.getData().get(0).getDurationEnd();
//            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
//            Date date = new Date(durationEndInMillis);
//            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
//            String returnFormat = dateFormat.format(date);
//            binding.dateFinish.setText("Waktu Pengembalian: " + model.getData().get(0).getDateFinish() + ", maksimal Pukul " + returnFormat);
//        } else if(duration.equals("12 Jam")) {
//            long durationEndInMillis = model.getData().get(0).getDurationEnd();
//            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
//            Date date = new Date(durationEndInMillis);
//            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
//            String returnFormat = dateFormat.format(date);
//            binding.dateFinish.setText("Waktu Pengembalian: " + model.getData().get(0).getDateFinish() + ", maksimal Pukul " + returnFormat);
//        }
//        else {
//            long durationEndInMillis = model.getData().get(0).getDurationEnd();
//            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
//            Date date = new Date(durationEndInMillis);
//            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
//            String returnFormat = dateFormat.format(date);
//            binding.dateFinish.setText("Waktu Pengembalian: " + model.getData().get(0).getDateFinish() + ", maksimal Pukul " + returnFormat);
//            ///binding.dateFinish.setText("Waktu Pengembalian: " + model.getData().get(0).getDateFinish() + ", maksimal Pukul 23:59");
//        }

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
                if (model.getStatus().equals("Belum Bayar")) {
                    showConfirmVerifyDialog();
                } else if (model.getStatus().equals("Sudah Bayar")) {
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


        /// beri validasi jika terdapat barang & waktu yang sama, namun status = Belum Bayar
        showDialogNotification();

    }

    private void showDialogNotification() {
        for (int i = 0; i < model.getName().size(); i++) {
            int finalI = i;
            FirebaseFirestore
                    .getInstance()
                    .collection("notification")
                    .whereEqualTo("name", model.getName().get(i))
                    .whereNotEqualTo("cartId", model.getData().get(i).getCartId())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot size = task.getResult();

                                if (size.size() > 0) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                                        try {
                                            Date dateStart = formatter.parse(model.getDateStart());
                                            Date dateFinish = formatter.parse(model.getDateFinish());

                                            Date transactionStart = formatter.parse("" + document.get("dateStart"));
                                            Date transactionFinish = formatter.parse("" + document.get("dateFinish"));

                                            if (((dateStart.getTime() < transactionStart.getTime() && dateStart.getTime() < transactionFinish.getTime()) && (dateFinish.getTime() < transactionStart.getTime() && dateFinish.getTime() < transactionFinish.getTime()))
                                                    || ((dateStart.getTime() > transactionStart.getTime() && dateStart.getTime() > transactionFinish.getTime()) && (dateFinish.getTime() > transactionStart.getTime() && dateFinish.getTime() > transactionFinish.getTime()))) {

                                                Log.e(String.valueOf(finalI), "PASS");

                                            } else {
                                                counterNotification++;
                                                Log.e(String.valueOf(finalI), "get");
                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    Log.e(String.valueOf(finalI), "Transaksi kosong");
                                }
                            }
                        }
                    });
        }

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (counterNotification > 0) {
                /// tampilkan dialog notifikasi
                new AlertDialog.Builder(HistoryTransactionDetailActivity.this)
                        .setTitle("Harap Lakukan Pembayaran")
                        .setMessage("Harap segera melakukan pembayaran terhadap transaksi ini, karena barang pada transaksi ini juga terdapat di transaksi lain.\n\nHanya pelanggan yang pertama kali membayar, yang berhak menyewa Kamera/Aksesoris pada transaksi ini, setelah itu transaksi lain akan dibatalkan oleh admin")
                        .setIcon(R.drawable.ic_baseline_warning_24)
                        .setPositiveButton("OKE", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        })
                        .show();
            }
        }, 1000);
    }

    private void setTotalSewa() {
        for (int i = 0; i < model.getData().size(); i++) {
            String category = model.getData().get(i).getCategory();
            String productId = model.getData().get(i).getProductId();

            if (category.equals("Kamera")) {
                FirebaseFirestore
                        .getInstance()
                        .collection("camera")
                        .document(productId)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                int totalSewa = documentSnapshot.getLong("totalSewa").intValue() + 1;

                                FirebaseFirestore
                                        .getInstance()
                                        .collection("camera")
                                        .document(productId)
                                        .update("totalSewa", totalSewa);

                            }
                        });
            } else {
                FirebaseFirestore
                        .getInstance()
                        .collection("peralatan")
                        .document(productId)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                int totalSewa = documentSnapshot.getLong("totalSewa").intValue() + 1;

                                FirebaseFirestore
                                        .getInstance()
                                        .collection("peralatan")
                                        .document(productId)
                                        .update("totalSewa", totalSewa);

                            }
                        });
            }
        }

        Toast.makeText(HistoryTransactionDetailActivity.this, "Berhasil menyelesaikan transaksi ini", Toast.LENGTH_SHORT).show();
        onBackPressed();

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
                        if (task.isSuccessful()) {
                            /// total sewa bertambah
                            setTotalSewa();
                            deleteBooking();
                        } else {
                            Toast.makeText(HistoryTransactionDetailActivity.this, "Gagal menyelesaikan transaksi ini", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void deleteBooking() {
        for (int i = 0; i < model.getData().size(); i++) {
            FirebaseFirestore
                    .getInstance()
                    .collection("booking")
                    .document(model.getData().get(i).getCartId())
                    .delete();
        }
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
                        if (task.isSuccessful()) {

                            /// create booking product
                            for (int i = 0; i < model.getData().size(); i++) {
                                Map<String, Object> booking = new HashMap<>();
                                booking.put("transactionId", model.getTransactionId());
                                booking.put("dateStart", model.getDateStart());
                                booking.put("dateFinish", model.getDateFinish());
                                booking.put("productName", model.getData().get(i).getName());
                                booking.put("category", model.getData().get(i).getCategory());

                                FirebaseFirestore
                                        .getInstance()
                                        .collection("booking")
                                        .document(model.getData().get(i).getCartId())
                                        .set(booking);
                            }
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
                    deleteNotification();
                })
                .setNegativeButton("TIDAK", (dialog, i) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void deleteNotification() {

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
                        if (task.isSuccessful()) {
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
                        if (("" + documentSnapshot.get("role")).equals("admin")) {
                            if (!model.getStatus().equals("Selesai")) {
                                binding.verify.setVisibility(View.VISIBLE);
                            }
                            if (model.getStatus().equals("Belum Bayar") || model.getStatus().equals("Selesai")) {
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