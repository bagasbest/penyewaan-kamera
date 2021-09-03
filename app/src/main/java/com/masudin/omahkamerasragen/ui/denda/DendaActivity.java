package com.masudin.omahkamerasragen.ui.denda;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.masudin.omahkamerasragen.databinding.ActivityDendaBinding;
import com.masudin.omahkamerasragen.ui.history_transaction.HistoryTransactionAdapter;
import com.masudin.omahkamerasragen.ui.history_transaction.HistoryTransactionViewModel;

public class DendaActivity extends AppCompatActivity {

    private ActivityDendaBinding binding;
    private DendaAdapter adapter;
    private FirebaseUser user;

    @Override
    protected void onResume() {
        super.onResume();
        initRecyclerView();
        initViewModel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDendaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        user = FirebaseAuth.getInstance().getCurrentUser();

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initRecyclerView() {
        binding.rvDenda.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DendaAdapter();
        binding.rvDenda.setAdapter(adapter);
    }

    private void initViewModel() {
        // tampilkan daftar artikel di halaman artikel terkait pertanian
        HistoryTransactionViewModel viewModel = new ViewModelProvider(this).get(HistoryTransactionViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.setCustomerTransaction(user.getUid(), "Sudah Bayar");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}