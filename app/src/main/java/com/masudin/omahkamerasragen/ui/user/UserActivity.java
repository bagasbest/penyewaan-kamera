package com.masudin.omahkamerasragen.ui.user;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.masudin.omahkamerasragen.databinding.ActivityUserBinding;

public class UserActivity extends AppCompatActivity {

    private ActivityUserBinding binding;
    private UserAdapter adapter;

    @Override
    protected void onResume() {
        super.onResume();
        initRecyclerView();
        initViewModel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    private void initRecyclerView() {
        binding.rvUser.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter();
        binding.rvUser.setAdapter(adapter);
    }

    private void initViewModel() {
        // tampilkan daftar artikel di halaman artikel terkait pertanian
        UserViewModel viewModel = new ViewModelProvider(this).get(UserViewModel.class);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.setListUser(uid);
        viewModel.getUser().observe(this, users -> {
            if (users.size() > 0) {
                binding.noData.setVisibility(View.GONE);
                adapter.setData(users);
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