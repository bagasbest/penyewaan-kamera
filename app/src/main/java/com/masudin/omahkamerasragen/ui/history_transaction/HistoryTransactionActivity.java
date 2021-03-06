package com.masudin.omahkamerasragen.ui.history_transaction;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.masudin.omahkamerasragen.R;
import com.masudin.omahkamerasragen.databinding.ActivityHistoryTransactionBinding;

public class HistoryTransactionActivity extends AppCompatActivity {

    /// inisiasi variabel, diperlukan supaya aplikasi tidak error saat dijalankan
    private ActivityHistoryTransactionBinding binding;
    private HistoryTransactionAdapter adapter;
    private FirebaseUser user;
    private String role;
    private String status = "Belum Bayar";
    @Override
    protected void onResume() {
        super.onResume();
       checkRole();
    }

    /// cek role apakah yang login saat ini user atau admin
    /// jika admin, maka dapat melihat semua transaksi
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
                            role = "admin";
                            initRecyclerView();
                            initViewModel("admin", status);
                        } else {
                            role = "user";
                            initRecyclerView();
                            initViewModel("user", status);
                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        /// get uid dari user yang sedang login
        user = FirebaseAuth.getInstance().getCurrentUser();

        // filter belum bayar atau sudah bayar, atau selesai
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.status, android.R.layout.simple_list_item_1);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        binding.statusEt.setAdapter(adapter);
        binding.statusEt.setOnItemClickListener((adapterView, view, i, l) -> {
            initRecyclerView();
            status = binding.statusEt.getText().toString();
            initViewModel(role, status);
        });


        /// kembali ke halaman sebelumnya
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    /// FUNGSI UNTUK MENAMPILKAN LIST DATA history transaksi
    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        binding.rvTransaction.setLayoutManager(layoutManager);
        adapter = new HistoryTransactionAdapter();
        binding.rvTransaction.setAdapter(adapter);
    }

    /// FUNGSI UNTUK MENDAPATKAN LIST DATA history tranasaksi DARI FIREBASE
    private void initViewModel(String role, String status) {
        // tampilkan daftar history tranasksi berdasarkan role nya, admin atau user biasa
        HistoryTransactionViewModel viewModel = new ViewModelProvider(this).get(HistoryTransactionViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);
        if(role.equals("admin")) {
            viewModel.setAllTransaction(status);
        } else {
            viewModel.setCustomerTransaction(user.getUid(), status);
        }
        viewModel.getTransaction().observe(this, transactionModels -> {
            if (transactionModels.size() > 0) {
                binding.noData.setVisibility(View.GONE);
                adapter.setData(transactionModels);
            } else {
                binding.noData.setVisibility(View.VISIBLE);
            }
            binding.progressBar.setVisibility(View.GONE);
        });
    }


    /// HAPUSKAN ACTIVITY KETIKA SUDAH TIDAK DIGUNAKAN, AGAR MENGURANGI RISIKO MEMORY LEAKS
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}