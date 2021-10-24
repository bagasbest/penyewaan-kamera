package com.masudin.omahkamerasragen.ui.user;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.os.Bundle;
import android.view.View;
import com.masudin.omahkamerasragen.databinding.ActivityUserBinding;

public class UserActivity extends AppCompatActivity {

    /// inisiasi variabel, diperlukan supaya aplikasi tidak error saat dijalankan
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

        /// kembali ke halaman sebelumnya
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    /// FUNGSI UNTUK MENAMPILKAN LIST DATA user
    private void initRecyclerView() {
        binding.rvUser.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter();
        binding.rvUser.setAdapter(adapter);
    }

    /// FUNGSI UNTUK MENDAPATKAN LIST DATA user DARI FIREBASE
    private void initViewModel() {
        // tampilkan daftar user
        UserViewModel viewModel = new ViewModelProvider(this).get(UserViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.setListUser();
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

    /// HAPUSKAN ACTIVITY KETIKA SUDAH TIDAK DIGUNAKAN, AGAR MENGURANGI RISIKO MEMORY LEAKS
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}