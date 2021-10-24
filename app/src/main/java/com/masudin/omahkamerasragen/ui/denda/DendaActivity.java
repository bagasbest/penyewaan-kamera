package com.masudin.omahkamerasragen.ui.denda;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.os.Bundle;
import android.view.View;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.masudin.omahkamerasragen.databinding.ActivityDendaBinding;
import com.masudin.omahkamerasragen.ui.history_transaction.HistoryTransactionViewModel;

public class DendaActivity extends AppCompatActivity {

    /// inisiasi variabel, diperlukan supaya aplikasi tidak error saat dijalankan
    private ActivityDendaBinding binding;
    private DendaAdapter adapter;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDendaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        /// ambil data pengguna yang sedang login saat ini
        user = FirebaseAuth.getInstance().getCurrentUser();

        /// cek role pengguna, apakah admin/user, jika admin, maka akan dapat melihat semua daftar denda seluruh pengguna
        /// jika user, maka hanya dapat melihat denda akun sendiri
        checkRole();


        /// kembali ke halaman sebelumnya
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    /// cek role pengguna, apakah admin/user, jika admin, maka akan dapat melihat semua daftar denda seluruh pengguna
    /// jika user, maka hanya dapat melihat denda akun sendiri
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
                            initRecyclerView();
                            initViewModel("admin");
                        } else {
                            initRecyclerView();
                            initViewModel("user");
                        }
                    }
                });
    }

    /// FUNGSI UNTUK MENAMPILKAN LIST DATA denda
    private void initRecyclerView() {
        binding.rvDenda.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DendaAdapter();
        binding.rvDenda.setAdapter(adapter);
    }

    /// FUNGSI UNTUK MENDAPATKAN LIST DATA denda DARI FIREBASE
    private void initViewModel(String role) {
        // tampilkan daftar denda
        HistoryTransactionViewModel viewModel = new ViewModelProvider(this).get(HistoryTransactionViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);

        if(role.equals("admin")) {
            viewModel.setAllTransaction("Sudah Bayar");
        } else {
            viewModel.setCustomerTransaction(user.getUid(), "Sudah Bayar");
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