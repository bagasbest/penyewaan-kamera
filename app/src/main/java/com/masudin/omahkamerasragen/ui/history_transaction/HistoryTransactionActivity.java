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
        user = FirebaseAuth.getInstance().getCurrentUser();

        // filter belum bayar atau sudah bayar
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.status, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        binding.statusEt.setAdapter(adapter);
        binding.statusEt.setOnItemClickListener((adapterView, view, i, l) -> {
            initRecyclerView();
            status = binding.statusEt.getText().toString();
            initViewModel(role, status);
        });



        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initRecyclerView() {
        binding.rvTransaction.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryTransactionAdapter();
        binding.rvTransaction.setAdapter(adapter);
    }

    private void initViewModel(String role, String status) {
        // tampilkan daftar artikel di halaman artikel terkait pertanian
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}