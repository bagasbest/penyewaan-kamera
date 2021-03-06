package com.masudin.omahkamerasragen.ui.camera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.masudin.omahkamerasragen.LoginActivity;
import com.masudin.omahkamerasragen.MainActivity;
import com.masudin.omahkamerasragen.R;
import com.masudin.omahkamerasragen.databinding.ActivityCameraDetailBinding;
import com.masudin.omahkamerasragen.ui.booking.BookingActivity;
import com.masudin.omahkamerasragen.ui.cart.CartActivity;
import com.masudin.omahkamerasragen.ui.cart.CartModel;
import com.masudin.omahkamerasragen.ui.history_transaction.HistoryTransactionActivity;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CameraDetailActivity extends AppCompatActivity {

    /// inisiasi variabel, supaya tidak terjadi error ketika halaman detail kamera dijalankan
    public static final String EXTRA_CAMERA = "camera";
    private ActivityCameraDetailBinding binding;
    private CameraModel model;
    private int counter = 0;
    private String pickHour;
    private String options;
    private String userUid;
    private String dateStart = "";
    private String dateFinish = "";
    private String getPickHour = "";
    private String getCustomerName = "";
    private String duration = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCameraDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /// ambil UID dari pengguna saat ini
        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ///data dari kelas model terkait data kamera di panggil di activity ini, kemudian data tersebut di tampilkan pada halaman ini
        model = getIntent().getParcelableExtra(EXTRA_CAMERA);
        NumberFormat formatter = new DecimalFormat("#,###");

        Glide.with(this)
                .load(model.getDp())
                .into(binding.dp);

        binding.name.setText(model.getName());
        binding.merk.setText("Merk: " + model.getMerk());
        binding.description.setText(model.getDescription());
        binding.facility.setText(model.getFacility());
        binding.price.setText("IDR " + formatter.format(Double.parseDouble(model.getPrice())) + " untuk 6 Jam Penyewaan");
        binding.price2.setText("IDR  " + formatter.format(Double.parseDouble(model.getPrice2())) + " untuk 12 Jam Penyewaan");
        binding.price3.setText("IDR " + formatter.format(Double.parseDouble(model.getPrice3())) + " untuk 24 Jam Penyewaan");
        binding.totalSewa.setText("Telah disawa " + model.getTotalSewa() + " kali");


        // cek apakah role == user / role == admin
        checkRole();

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // hapus barang
        binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialog();
            }
        });

        // edit barang
        binding.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CameraDetailActivity.this, CameraEditActivity.class);
                intent.putExtra(CameraEditActivity.EXTRA_EDIT, model);
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
                sewaKameraPerDay();
            }
        });

        // sewa 6 jam
        binding.priceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!model.getPrice().equals("0")) {
                    sewaKameraPerHour(6);
                } else {
                    Toast.makeText(CameraDetailActivity.this, "Penyewaan 6 Jam tidak tersedia", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // sewa 12 jam
        binding.price2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!model.getPrice2().equals("0")) {
                    sewaKameraPerHour(12);
                } else {
                    Toast.makeText(CameraDetailActivity.this, "Penyewaan 12 Jam tidak tersedia", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /// klik jadwal booking
        binding.bookingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CameraDetailActivity.this, BookingActivity.class));
            }
        });


        /// klik sewa sekarang
        binding.sewaSekarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.sewaSekarang.setVisibility(View.GONE);
                binding.textSewaSekarang.setVisibility(View.GONE);
                binding.masukkanKeranjang.setVisibility(View.GONE);
                binding.imageView5.setVisibility(View.GONE);
                binding.imageView6.setVisibility(View.GONE);
                options = "now";
            }
        });

        /// klik masukkan keranjang
        binding.masukkanKeranjang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.sewaSekarang.setVisibility(View.GONE);
                binding.textSewaSekarang.setVisibility(View.GONE);
                binding.masukkanKeranjang.setVisibility(View.GONE);
                binding.imageView5.setVisibility(View.GONE);
                binding.imageView6.setVisibility(View.GONE);
                options = "cart";
            }
        });

    }

    /// fungsi yang berjalan ketika pengguna memilih penywaan perhari
    private void sewaKameraPerDay() {
        // pilih tanggal peminjaman, pengguna harus memilih tanggal penyewaan dan tanggal pengembalian
        Calendar now = Calendar.getInstance();
        MaterialDatePicker datePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setCalendarConstraints(new CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now()).build())
                .setSelection(Pair.create(now.getTimeInMillis(), now.getTimeInMillis())).build();
        datePicker.show(getSupportFragmentManager(), datePicker.toString());
        datePicker.addOnPositiveButtonClickListener(selection -> {

            // setelah memilih, sistem akan melakukan konversi waktu penyewaan dan waktu pengembalian ke bentuk tanggal, contoh, 19-02-2021
            Pair prendiRange = (Pair) datePicker.getSelection();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String formatFirst = sdf.format(new Date(Long.parseLong(prendiRange.first.toString())));
            String formatSecond = sdf.format(new Date(Long.parseLong(prendiRange.second.toString())));

            /// untuk penyewaan harian, minimal penyewaan adalah 1 hari
            if (formatFirst.equals(formatSecond)) {
                Toast.makeText(CameraDetailActivity.this, "Penyewaan Barang Minimal 1 Hari", Toast.LENGTH_SHORT).show();
                return;
            }

            /// cek apakah waktu peminjaman & pengembalian sudah sama dengan produk yang ada di keranjang, jika ada
            /// jika waktu penyewaan & pengembalian berbeda, maka akan muncul dialog box bahwa waktu peminjaman tiap barang harus sama
            /// ini hanya berlaku untuk peminjaman lebih dari 1 barang
            FirebaseFirestore
                    .getInstance()
                    .collection("cart")
                    .whereEqualTo("customerUid", userUid)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot document) {

                            /// cek apakah ada barang di keranjang, kemudian dapatkan data penyewaan, data pengembalian, dan waktu sewa jika ada
                            if (document.size() > 0) {
                                dateStart = "" + document.getDocuments().get(0).get("dateStart");
                                dateFinish = "" + document.getDocuments().get(0).get("dateFinish");
                                getPickHour = "" + document.getDocuments().get(0).get("pickHour");
                                duration = "" + document.getDocuments().get(0).get("duration");


                                /// saya disini mau mendapatkan durasi dari barang yang ada di keranjang, dengan tujuan barang yang ada di keranjang memiliki durasi yang sama: contoh 12 jam semua atau 1 hari semua.
                                /// logikanya: jika durasi penyewaan barang di keranjang tidak sesuai, maka pengguna saat ini tidak bisa menambah barang baru ke keranjang
                                long diff = Long.parseLong(prendiRange.second.toString()) - Long.parseLong(prendiRange.first.toString());
                                long diffDays = diff / (24 * 60 * 60 * 1000);
                                if (!duration.equals(diffDays + " Hari") && options.equals("cart")) {
                                    new AlertDialog.Builder(CameraDetailActivity.this)
                                            .setTitle("Durasi Harus Sesuai")
                                            .setMessage("Maaf, Durasi penyewaan Kamera ini harus sama dengan produk yang ada di dalam keranjang anda. Durasi yang ada di keranjang anda adalah " + duration)
                                            .setIcon(R.drawable.ic_baseline_warning_24)
                                            .setPositiveButton("OKE", (dialogInterface, i) -> {
                                                dialogInterface.dismiss();
                                            })
                                            .show();
                                    return;
                                }
                            }


                            /// cek apakah waktu peminjaman & pengembalian sudah sama dengan produk yang ada di keranjang, jika ada
                            /// jika waktu peminjaman, pengembalian, sesuai maka bisa lanjut memilih waktu pengambilan barang
                            if (((dateStart.equals(formatFirst)) && (dateFinish.equals(formatSecond))) || document.size() == 0 || options.equals("now")) {

                                // show time picker, pilih waktu pengambilan barang
                                MaterialTimePicker timePicker = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).build();
                                timePicker.show(getSupportFragmentManager(), timePicker.toString());
                                timePicker.addOnPositiveButtonClickListener(time -> {


                                    /// tampilkan progress dialog
                                    ProgressDialog mProgressDialog = new ProgressDialog(CameraDetailActivity.this);

                                    mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...");
                                    mProgressDialog.setCanceledOnTouchOutside(false);
                                    mProgressDialog.show();

                                    // ini merupakan kode untuk mengecek seluruh transaksi, yang berfungsi untuk mengecek apakah barang ini sedang di booking atau tidak
                                    FirebaseFirestore
                                            .getInstance()
                                            .collection("transaction")
                                            .whereEqualTo("status", "Sudah Bayar")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {

                                                        /// cek apakah data transaksi ada atau tidak
                                                        QuerySnapshot size = task.getResult();

                                                        /// konversi waktu pengambilan barang, contoh waktu konversi: 13:30
                                                        if (timePicker.getMinute() < 10) {
                                                            pickHour = timePicker.getHour() + ":0" + timePicker.getMinute();
                                                        } else {
                                                            pickHour = timePicker.getHour() + ":" + timePicker.getMinute();
                                                        }

                                                        /// waktu penyewaan berakhir pada pukul, contoh: 13:30
                                                        long durationEndInMillis = TimeUnit.SECONDS.toMillis(TimeUnit.HOURS.toSeconds(timePicker.getHour()) + TimeUnit.MINUTES.toSeconds(timePicker.getMinute()));

                                                        /// ambil jam saat ini, digunakan untuk memverifikasi penyewaan hari ini
                                                        DateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                                                        String timeNow = df.format(new Date());
                                                        String dateNow = sdf.format(new Date());
                                                        try {
                                                            Date getTimeNow = df.parse(timeNow);
                                                            Date getDateNow = sdf.parse(dateNow);
                                                            Date nowFirst = sdf.parse(formatFirst);

                                                            if ((getDateNow.getTime() == nowFirst.getTime()) && getTimeNow.getTime() > (durationEndInMillis - (1000 * 60 * 60 * 7))) {
                                                                mProgressDialog.dismiss();
                                                                new AlertDialog.Builder(CameraDetailActivity.this)
                                                                        .setTitle("Gagal")
                                                                        .setMessage("Maaf, jam pengambilan produk sudah lewat, silahkan inputkan jam pengambilan produk minimal 1 jam kedepan dari jam saat ini")
                                                                        .setIcon(R.drawable.ic_baseline_warning_24)
                                                                        .setPositiveButton("OKE", (dialogInterface, i) -> {
                                                                            mProgressDialog.dismiss();
                                                                            dialogInterface.dismiss();
                                                                        })
                                                                        .show();
                                                                return;
                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }


                                                        if (size.size() > 0) {
                                                            if (pickHour.equals(getPickHour) || document.size() == 0 || options.equals("now")) {

                                                                /// pengecekan pada transaksi, apakah barang yang dipilih sedang di sewa oleh orang lain atau tidak, jika disewa, maka pengguna saat ini tidak bisa menyewa
                                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                                    try {
                                                                        ArrayList<String> listName = (ArrayList<String>) document.get("name");
                                                                        Date dateStart = sdf.parse("" + document.get("dateStart"));
                                                                        Date dateFinish = sdf.parse("" + document.get("dateFinish"));
                                                                        Date nowFirst = sdf.parse(formatFirst);
                                                                        Date nowSecond = sdf.parse(formatSecond);

                                                                        long x = dateStart.getTime();
                                                                        long y = dateFinish.getTime();
                                                                        long dateStartNow = nowFirst.getTime();
                                                                        long dateFinishNow = nowSecond.getTime();


                                                                        // cek apakah tanggal sudah di booking atau belum oleh orang lain
                                                                        if (((dateStartNow < x && dateStartNow < y) && (dateFinishNow < x && dateFinishNow < y))
                                                                                || ((dateStartNow > x && dateStartNow > y) && (dateFinishNow > x && dateFinishNow > y))) {
                                                                            counter++;

                                                                            if (counter == size.size()) {
                                                                                counter = 0;
                                                                                mProgressDialog.dismiss();
                                                                                confirmSewaCameraPerDay(datePicker, pickHour, durationEndInMillis);
                                                                            }
                                                                        } else {
                                                                            for (int i = 0; i < listName.size(); i++) {
                                                                                if (model.getName().equals(listName.get(i))) {
                                                                                    counter = 0;
                                                                                    mProgressDialog.dismiss();
                                                                                    Toast.makeText(CameraDetailActivity.this, "Tanggal Sudah Di Booking", Toast.LENGTH_SHORT).show();
                                                                                    return;
                                                                                }
                                                                            }
                                                                            counter++;
                                                                            if (counter == size.size()) {
                                                                                counter = 0;
                                                                                mProgressDialog.dismiss();
                                                                                confirmSewaCameraPerDay(datePicker, pickHour, durationEndInMillis);
                                                                            }
                                                                        }

                                                                    } catch (ParseException e) {
                                                                        /// gagal mendapatkan data dari database
                                                                        mProgressDialog.dismiss();
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            } else {
                                                                mProgressDialog.dismiss();
                                                                /// gagal menyewa pada jam x, dikarenan ada barang di keranjang dengan waktu yang berbeda
                                                                new AlertDialog.Builder(CameraDetailActivity.this)
                                                                        .setTitle("Gagal")
                                                                        .setMessage("Maaf, jam pengambilan produk harus sama pada produk yang ada pada keranjang anda!\n\nJam pengambilan: pukul " + getPickHour)
                                                                        .setIcon(R.drawable.ic_baseline_warning_24)
                                                                        .setPositiveButton("OKE", (dialogInterface, i) -> {
                                                                            dialogInterface.dismiss();
                                                                        })
                                                                        .show();
                                                            }
                                                        } else {
                                                            mProgressDialog.dismiss();
                                                            /// jika belum ada transaksi sama sekali, maka cek apakah waktu penyewaan ini sudah sesuai dengan barang pertama di keranjang atau belum
                                                            if (pickHour.equals(getPickHour) || document.size() == 0 || options.equals("now")) {
                                                                confirmSewaCameraPerDay(datePicker, pickHour, durationEndInMillis);
                                                            } else {
                                                                new AlertDialog.Builder(CameraDetailActivity.this)
                                                                        .setTitle("Gagal")
                                                                        .setMessage("Maaf, jam pengambilan produk harus sama pada produk yang ada pada keranjang anda!\n\nJam pengambilan: pukul " + getPickHour)
                                                                        .setIcon(R.drawable.ic_baseline_warning_24)
                                                                        .setPositiveButton("OKE", (dialogInterface, i) -> {
                                                                            dialogInterface.dismiss();
                                                                        })
                                                                        .show();
                                                            }
                                                        }

                                                    } else {
                                                        mProgressDialog.dismiss();
                                                        Toast.makeText(CameraDetailActivity.this, "Gagal Load Calendar", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                });
                            } else {
                                /// waktu peminjaman tidak sama
                                new AlertDialog.Builder(CameraDetailActivity.this)
                                        .setTitle("Gagal")
                                        .setMessage("Maaf, waktu peminjaman dan waktu pengembalian barang harus sama dengan waktu pada produk yang ada pada keranjang anda!\n\nWaktu peminjaman: " + dateStart + "\nWaktu pengembalian: " + dateFinish)
                                        .setIcon(R.drawable.ic_baseline_warning_24)
                                        .setPositiveButton("OKE", (dialogInterface, i) -> {
                                            dialogInterface.dismiss();
                                        })
                                        .show();
                            }

                        }
                    });
        });
    }

    /// fungsi lanjutan dari fungsi diatas, jika semua nya tervalidasi dan sukses, maka sistem akan menampilkan dialog box, apakah yakin ingin menyewa?
    private void confirmSewaCameraPerDay(MaterialDatePicker datePicker, String pickHour, Long durationEnd) {
        Pair prendiRange = (Pair) datePicker.getSelection();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String formatFirst = sdf.format(new Date(Long.parseLong(prendiRange.first.toString())));
        String formatSecond = sdf.format(new Date(Long.parseLong(prendiRange.second.toString())));
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Penyewaan Kamera")
                .setMessage("Apakah anda yakin ingin menyewa Kamera ini ?")
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setPositiveButton("YA", (dialogInterface, i) -> {
                    long diff = Long.parseLong(prendiRange.second.toString()) - Long.parseLong(prendiRange.first.toString());
                    long diffDays = diff / (24 * 60 * 60 * 1000);
                    saveProductToCart(formatFirst, formatSecond, 24, diffDays, durationEnd, pickHour);
                })
                .setNegativeButton("TIDAK", (dialog, i) -> {
                    dialog.dismiss();
                })
                .show();
    }


    /// fungsi yang berjalan ketika pengguna menekan tombol sewa per jam
    private void sewaKameraPerHour(int hour) {
        // pilih tanggal peminjaman,
        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker().setCalendarConstraints(new CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now()).build()).build();
        datePicker.show(getSupportFragmentManager(), datePicker.toString());
        datePicker.addOnPositiveButtonClickListener(selection -> {

            /// konversi tanggal peminjaman yang sudah dipilih: contoh 19-02-2021
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String getDateNow = sdf.format(new Date(Long.parseLong(selection.toString())));

            /// cek apakah waktu peminjaman & pengembalian sudah sama dengan produk yang ada di keranjang, jika ada
            /// jika menyewa lebih dari 1 barang, waktu penyewaan, dan pengembalian harus sama
            FirebaseFirestore
                    .getInstance()
                    .collection("cart")
                    .whereEqualTo("customerUid", userUid)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot document) {

                            /// cek apakah ada barang di keranjang, kemudian dapatkan data penyewaan, data pengembalian, dan waktu sewa jika ada
                            if (document.size() > 0) {
                                dateStart = "" + document.getDocuments().get(0).get("dateStart");
                                dateFinish = "" + document.getDocuments().get(0).get("dateFinish");
                                getPickHour = "" + document.getDocuments().get(0).get("pickHour");
                                duration = "" + document.getDocuments().get(0).get("duration");

                                /// saya disini mau mendapatkan durasi dari barang yang ada di keranjang, dengan tujuan barang yang ada di keranjang memiliki durasi yang sama: contoh 12 jam semua atau 1 hari semua.
                                /// logikanya: jika durasi penyewaan barang di keranjang tidak sesuai, maka pengguna saat ini tidak bisa menambah barang baru ke keranjang
                                if (!duration.equals(hour + " Jam") && options.equals("cart")) {
                                    new AlertDialog.Builder(CameraDetailActivity.this)
                                            .setTitle("Durasi Harus Sesuai")
                                            .setMessage("Maaf, Durasi penyewaan Kamera ini harus sama dengan produk yang ada di dalam keranjang anda. Durasi yang ada di keranjang anda adalah " + duration)
                                            .setIcon(R.drawable.ic_baseline_warning_24)
                                            .setPositiveButton("OKE", (dialogInterface, i) -> {
                                                dialogInterface.dismiss();
                                            })
                                            .show();
                                    return;
                                }

                            }


                            // show time picker
                            MaterialTimePicker timePicker = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).build();
                            timePicker.show(getSupportFragmentManager(), timePicker.toString());
                            timePicker.addOnPositiveButtonClickListener(time -> {

                                /// waktu penyewaan berakhir pada pukul, contoh: 13:30
                                long durationStartInMillis = TimeUnit.SECONDS.toMillis(TimeUnit.HOURS.toSeconds(timePicker.getHour()) + TimeUnit.MINUTES.toSeconds(timePicker.getMinute()));
                                long durationEndInMillis = durationStartInMillis + (1000L * 60 * 60 * hour);
                                long dateEnd = (Long.parseLong(selection.toString()) - (1000L * 60 * 60 * 7)) + durationEndInMillis;

                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                                String dateEndInString = sdf.format(new Date(dateEnd));

                                /// cek apakah waktu peminjaman & pengembalian sudah sama dengan produk yang ada di keranjang, jika ada
                                /// jika waktu peminjaman, pengembalian, sesuai maka bisa lanjut memilih waktu pengambilan barang
                                if (getDateNow.equals(dateStart) && dateEndInString.equals(dateFinish) || document.size() == 0 || options.equals("now")) {

                                    /// tampilkan progress dialog
                                    ProgressDialog mProgressDialog = new ProgressDialog(CameraDetailActivity.this);

                                    mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...");
                                    mProgressDialog.setCanceledOnTouchOutside(false);
                                    mProgressDialog.show();

                                    // ini merupakan kode untuk mengecek seluruh transaksi, yang berfungsi untuk mengecek apakah barang ini sedang di booking atau tidak
                                    FirebaseFirestore
                                            .getInstance()
                                            .collection("transaction")
                                            .whereEqualTo("status", "Sudah Bayar")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {

                                                        /// cek apakah data transaksi ada atau tidak
                                                        QuerySnapshot size = task.getResult();


                                                        /// konversi waktu pengambilan barang, contoh waktu konversi: 13:30
                                                        if (timePicker.getMinute() < 10) {
                                                            pickHour = timePicker.getHour() + ":0" + timePicker.getMinute();
                                                        } else {
                                                            pickHour = timePicker.getHour() + ":" + timePicker.getMinute();
                                                        }

                                                        /// ambil jam saat ini, digunakan untuk memverifikasi penyewaan hari ini,
                                                        /// misal jam penyewaan, 09:00 namun sekarang jam 10:00, maka tidak bisa menyewa
                                                        DateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                                                        String timeNow = df.format(new Date());
                                                        String dateNoww = sdf.format(new Date());
                                                        try {
                                                            Date jamSekarang = df.parse(timeNow);
                                                            Date tanggalSekarang = sdf.parse(dateNoww);
                                                            Date tanggalPengambilan = sdf.parse(getDateNow);
                                                            long jamPengambilan = TimeUnit.SECONDS.toMillis(TimeUnit.HOURS.toSeconds(timePicker.getHour()) + TimeUnit.MINUTES.toSeconds(timePicker.getMinute()));

                                                            if ((tanggalSekarang.getTime() == tanggalPengambilan.getTime()) && jamSekarang.getTime() > (jamPengambilan - (1000 * 60 * 60 * 7))) {
                                                                mProgressDialog.dismiss();
                                                                new AlertDialog.Builder(CameraDetailActivity.this)
                                                                        .setTitle("Gagal")
                                                                        .setMessage("Maaf, jam pengambilan produk sudah lewat, silahkan inputkan jam pengambilan produk minimal 1 jam kedepan dari jam saat ini")
                                                                        .setIcon(R.drawable.ic_baseline_warning_24)
                                                                        .setPositiveButton("OKE", (dialogInterface, i) -> {
                                                                            dialogInterface.dismiss();
                                                                        })
                                                                        .show();
                                                                return;
                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }


                                                        if (size.size() > 0) {
                                                            /// pengecekan pada transaksi, apakah barang yang dipilih sedang di sewa oleh orang lain atau tidak, jika disewa, maka pengguna saat ini tidak bisa menyewa
                                                            if (getPickHour.equals(pickHour) || document.size() == 0 || options.equals("now")) {

                                                                /// pengecekan pada transaksi, apakah barang yang dipilih sedang di sewa oleh orang lain atau tidak, jika disewa, maka pengguna saat ini tidak bisa menyewa
                                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                                                                    String format = sdf.format(new Date(Long.parseLong(selection.toString())));

                                                                    try {
                                                                        ArrayList<String> listName = (ArrayList<String>) document.get("name");
                                                                        Date dateStart = sdf.parse("" + document.get("dateStart"));
                                                                        Date dateFinish = sdf.parse("" + document.get("dateFinish"));
                                                                        Date now = sdf.parse(format);

                                                                        long x = dateStart.getTime();
                                                                        long y = dateFinish.getTime();
                                                                        long dateNow = now.getTime();

                                                                        // cek apakah tanggal sudah di booking atau belum oleh orang lain
                                                                        if ((dateNow < x && dateNow < y) || (dateNow > x && dateNow > y)) {
                                                                            counter++;
                                                                            if (counter == size.size()) {
                                                                                counter = 0;
                                                                                mProgressDialog.dismiss();
                                                                                confirmSewaCamera(selection, dateEndInString, hour, durationEndInMillis, pickHour);
                                                                            }
                                                                        } else {
                                                                            for (int i = 0; i < listName.size(); i++) {
                                                                                if (model.getName().equals(listName.get(i))) {
                                                                                    counter = 0;
                                                                                    mProgressDialog.dismiss();
                                                                                    Toast.makeText(CameraDetailActivity.this, "Tanggal Sudah Di Booking", Toast.LENGTH_SHORT).show();
                                                                                    return;
                                                                                }
                                                                            }
                                                                            counter++;
                                                                            if (counter == size.size()) {
                                                                                counter = 0;
                                                                                mProgressDialog.dismiss();
                                                                                confirmSewaCamera(selection, dateEndInString, hour, durationEndInMillis, pickHour);
                                                                            }
                                                                        }

                                                                    } catch (ParseException e) {
                                                                        mProgressDialog.dismiss();
                                                                        /// gagal mengecek transaksi
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            } else {
                                                                mProgressDialog.dismiss();
                                                                ////Gagal menyewa karena waktu tidak sesuai
                                                                new AlertDialog.Builder(CameraDetailActivity.this)
                                                                        .setTitle("Gagal")
                                                                        .setMessage("Maaf, jam pengambilan produk harus sama pada produk yang ada pada keranjang anda!\n\nJam pengambilan: " + getPickHour)
                                                                        .setCancelable(false)
                                                                        .setIcon(R.drawable.ic_baseline_warning_24)
                                                                        .setPositiveButton("OKE", (dialogInterface, i) -> {
                                                                            dialogInterface.dismiss();
                                                                        })
                                                                        .show();
                                                            }
                                                        } else {
                                                            mProgressDialog.dismiss();
                                                            /// jika transaksi kosong, maka cek keranjang, apakah waktu peminjaman sesuai atau tidak
                                                            if (pickHour.equals(getPickHour) || document.size() == 0 || options.equals("now")) {
                                                                confirmSewaCamera(selection, dateEndInString, hour, durationEndInMillis, pickHour);
                                                            } else {
                                                                new AlertDialog.Builder(CameraDetailActivity.this)
                                                                        .setTitle("Gagal")
                                                                        .setMessage("Maaf, jam pengambilan produk harus sama pada produk yang ada pada keranjang anda!\n\nJam pengambilan: pukul " + getPickHour)
                                                                        .setIcon(R.drawable.ic_baseline_warning_24)
                                                                        .setPositiveButton("OKE", (dialogInterface, i) -> {
                                                                            dialogInterface.dismiss();
                                                                        })
                                                                        .show();
                                                            }
                                                        }
                                                    } else {
                                                        mProgressDialog.dismiss();
                                                        Toast.makeText(CameraDetailActivity.this, "Gagal Load Calendar", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                } else {
                                    /// waktu penyewaan tidak sesuai
                                    new AlertDialog.Builder(CameraDetailActivity.this)
                                            .setTitle("Gagal")
                                            .setMessage("Maaf, waktu peminjaman dan waktu pengembalian barang harus sama dengan waktu pada produk yang ada pada keranjang anda!\n\nWaktu peminjaman: " + dateStart + "\nWaktu pengembalian: " + dateFinish)
                                            .setIcon(R.drawable.ic_baseline_warning_24)
                                            .setPositiveButton("OKE", (dialogInterface, i) -> {
                                                dialogInterface.dismiss();
                                            })
                                            .show();
                                }

                            });

                        }
                    });
        });
    }

    /// fungsi untuk menampilkan dialog box, apakah ingin menyewa kamera ini ?
    private void confirmSewaCamera(Object selection, String dateEnd, int hour, long durationEndInMillis, String pickHour) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String format = sdf.format(new Date(Long.parseLong(selection.toString())));
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Penyewaan Kamera")
                .setMessage("Apakah anda yakin ingin menyewa Kamera ini ?")
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    saveProductToCart(format, dateEnd, hour, 0, durationEndInMillis, pickHour);
                })
                .setNegativeButton("TIDAK", (dialog, i) -> {
                    dialog.dismiss();
                })
                .show();
    }

    /// fungsi untuk membuat barang di simpan di keranjang   atau transaksi langsung
    private void saveProductToCart(String first, String second, int hour, long difference, long durationEndInMillis, String pickHour) {
        ProgressDialog mProgressDialog = new ProgressDialog(this);

        mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        /// masukkan produk kedalam keranjang
        if (options.equals("cart")) {
            String uid = String.valueOf(System.currentTimeMillis());

            // SIMPAN DATA KAMERA KE DATABASE
            Map<String, Object> addToCart = new HashMap<>();
            addToCart.put("cartId", uid);
            addToCart.put("productId", model.getUid());
            addToCart.put("name", model.getName());
            addToCart.put("merk", model.getMerk());
            addToCart.put("dp", model.getDp());
            if (hour == 6) {
                addToCart.put("duration", hour + " Jam");
                addToCart.put("price", model.getPrice());
                addToCart.put("totalPrice", model.getPrice());
            } else if (hour == 12) {
                addToCart.put("duration", hour + " Jam");
                addToCart.put("price", model.getPrice2());
                addToCart.put("totalPrice", model.getPrice2());
            } else {
                addToCart.put("duration", difference + " Hari");
                addToCart.put("price", model.getPrice3());
                addToCart.put("totalPrice", Long.parseLong(model.getPrice3()) * difference);
            }
            addToCart.put("dateStart", first);
            addToCart.put("dateFinish", second);
            addToCart.put("durationEnd", durationEndInMillis);
            addToCart.put("category", "Kamera");
            addToCart.put("customerUid", userUid);
            addToCart.put("customerName", getCustomerName);
            addToCart.put("pickHour", pickHour);


            //// simpan data kedalam database
            FirebaseFirestore
                    .getInstance()
                    .collection("cart")
                    .document(uid)
                    .set(addToCart)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mProgressDialog.dismiss();
                                showSuccessDialog("Berhasil Memasukkan Produk Kedalam Keranjang", "barang akan ditambahkan pada keranjang");
                            } else {
                                mProgressDialog.dismiss();
                                showFailureDialog("Gagal Memasukkan Produk Kedalam Keranjang", "Terdapat kesalahan ketika memasukkan produk kedalam keranjang, silahkan periksa koneksi internet anda, dan coba lagi nanti");
                            }
                        }
                    });
        } else {
            /// sewa sekarang  / transaksi secara langsung
            String trId = String.valueOf(System.currentTimeMillis());


            //// Masukkan semuadata kedalam field - field, kemudian simpan ke firebase
            ArrayList<String> name = new ArrayList<>();
            name.add(model.getName());

            ArrayList<CartModel> cart = new ArrayList<>();
            CartModel cartModel = new CartModel();
            cartModel.setCartId(trId);
            cartModel.setCategory("Kamera");
            cartModel.setCustomerName(getCustomerName);
            cartModel.setCustomerUid(userUid);
            cartModel.setDp(model.getDp());
            if (hour == 6 || hour == 12) {
                cartModel.setDuration(hour + " Jam");
                if (hour == 6) {
                    cartModel.setPrice(model.getPrice());
                    cartModel.setTotalPrice(model.getPrice());
                } else {
                    cartModel.setPrice(model.getPrice2());
                    cartModel.setTotalPrice(model.getPrice2());
                }
            } else {
                cartModel.setDuration(difference + " Hari");
                cartModel.setPrice(model.getPrice3());
                cartModel.setTotalPrice(model.getPrice3());
            }
            cartModel.setDurationEnd(durationEndInMillis);
            cartModel.setDateFinish(second);
            cartModel.setDateStart(first);
            cartModel.setMerk(model.getMerk());
            cartModel.setName(model.getName());
            cartModel.setPickHour(pickHour);
            cartModel.setProductId(model.getUid());
            // add to list
            cart.add(cartModel);


            Map<String, Object> transaction = new HashMap<>();
            transaction.put("customerId", userUid);
            if (hour == 6) {
                transaction.put("finalPrice", model.getPrice());
            } else if (hour == 12) {
                transaction.put("finalPrice", model.getPrice2());
            } else {
                transaction.put("finalPrice", model.getPrice3());
            }
            transaction.put("dateFinish", second);
            transaction.put("dateStart", first);
            transaction.put("status", "Belum Bayar");
            transaction.put("transactionId", trId);
            transaction.put("name", name);
            transaction.put("data", cart);


            //// simpan data kedalam firebase
            FirebaseFirestore
                    .getInstance()
                    .collection("transaction")
                    .document(trId)
                    .set(transaction)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mProgressDialog.dismiss();
                                showSuccessDialog("Berhasil Menyewa Langsung", "Anda dapat melihat produk pada navigasi transaksi");
                            } else {
                                mProgressDialog.dismiss();
                                showFailureDialog("Gagal Menyewa Langsung", "Terdapat kesalahan ketika ingin menyewa langsung, silahkan periksa koneksi internet anda, dan coba lagi nanti");
                            }
                        }
                    });

            /// untuk keperluan set notifikasi di halaman detail transaksi, apakah ada produk yang sama waktu penyewaannya atau tidak
            Map<String, Object> notification = new HashMap<>();

            /// oleh karena itu dibutuhkan penyimpanan dan pengecekan waktu penyewaan, dan waktu pengembalian
            notification.put("cartId", trId);
            notification.put("dateStart", first);
            notification.put("dateFinish", second);
            notification.put("name", model.getName());
            FirebaseFirestore
                    .getInstance()
                    .collection("notification")
                    .document(trId)
                    .set(notification);

        }
    }

    /// fungsi untuk menampilkan dialog box ketika gagal menambahkan barang ke keranjang / gagal membuat transaksi
    private void showFailureDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    binding.masukkanKeranjang.setVisibility(View.VISIBLE);
                    binding.sewaSekarang.setVisibility(View.VISIBLE);
                    binding.textSewaSekarang.setVisibility(View.VISIBLE);
                    binding.imageView5.setVisibility(View.VISIBLE);
                    binding.imageView6.setVisibility(View.VISIBLE);
                    dialogInterface.dismiss();
                    onBackPressed();
                })
                .show();
    }

    /// fungsi untuk menampilkan dialog box ketika sukses menambahkan barang ke keranjang / membuat transaksi
    private void showSuccessDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    binding.masukkanKeranjang.setVisibility(View.VISIBLE);
                    binding.sewaSekarang.setVisibility(View.VISIBLE);
                    binding.textSewaSekarang.setVisibility(View.VISIBLE);
                    binding.imageView5.setVisibility(View.VISIBLE);
                    binding.imageView6.setVisibility(View.VISIBLE);
                    if (options.equals("cart")) {
                        startActivity(new Intent(CameraDetailActivity.this, CartActivity.class));
                    } else {
                        startActivity(new Intent(CameraDetailActivity.this, HistoryTransactionActivity.class));
                    }
                })
                .show();
    }

    /// konfirmasi menbghapus data kamera
    private void showConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi menghapus kamera")
                .setMessage("Apakah anda yakin ingin menghapus kamera ini ?")
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setPositiveButton("YAKIN", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    deleteCamera();
                })
                .setNegativeButton("TIDAK", (dialog, i) -> {
                    dialog.dismiss();
                })
                .show();
    }

    /// fungsi untuk menghapus data kamera
    private void deleteCamera() {
        ProgressDialog mProgressDialog = new ProgressDialog(this);

        mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        FirebaseFirestore
                .getInstance()
                .collection("camera")
                .document(model.getUid())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mProgressDialog.dismiss();
                            Toast.makeText(CameraDetailActivity.this, "Berhasil menghapus kamera", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(CameraDetailActivity.this, "Gagal menghapus kamera", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    //// pengecekan role dibutuhkan, jika admin, maka admin dapat menghapus & mengedit data kamera
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

                            getCustomerName = "" + documentSnapshot.get("name");

                            if (("" + documentSnapshot.get("role")).equals("admin")) {
                                binding.edit.setVisibility(View.VISIBLE);
                                binding.delete.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }
    }

    /// HAPUSKAN ACTIVITY KETIKA SUDAH TIDAK DIGUNAKAN, AGAR MENGURANGI RISIKO MEMORY LEAKS
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}