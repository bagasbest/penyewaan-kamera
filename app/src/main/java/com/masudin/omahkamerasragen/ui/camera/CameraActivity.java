package com.masudin.omahkamerasragen.ui.camera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.masudin.omahkamerasragen.databinding.ActivityCameraBinding;
import com.masudin.omahkamerasragen.ui.product.ProductAdapter;
import com.masudin.omahkamerasragen.ui.product.ProductViewModel;

public class CameraActivity extends AppCompatActivity {


    /// inisiasi variabel supaya tidak error ketika dijalankan
    private ActivityCameraBinding  binding;
    private FirebaseUser user;
    private CameraAdapter adapter;

    /// ON RESUME Activity sudah terlihat dan pengguna sudah dapat berinteraksi. Di sini adalah tempat terbaik untuk menjalankan animasi, membuka akses seperti camera, mengupdate UI, dll.
    @Override
    protected void onResume() {
        super.onResume();
        initRecylerView();
        initViewModel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        user = FirebaseAuth.getInstance().getCurrentUser();

        // cek role, hanya admin yang bisa CRUD produk kamera
        checkRole();


        // kembali ke halaman sebelumnya
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // tambah kamera
        binding.addCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CameraActivity.this, CameraAddActivity.class));
            }
        });
    }


    /// FUNGSI UNTUK MENAMPILKAN LIST DATA KAMERA
    private void initRecylerView() {
        binding.rvCamera.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CameraAdapter();
        binding.rvCamera.setAdapter(adapter);
    }


    /// FUNGSI UNTUK MENDAPATKAN LIST DATA KAMERA DARI FIREBASE
    private void initViewModel() {
        CameraViewModel viewModel = new ViewModelProvider(this).get(CameraViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.setCameraList();
        viewModel.getCameraList().observe(this, camera -> {
            if (camera.size() > 0) {
                binding.noData.setVisibility(View.GONE);
                adapter.setData(camera);
            } else {
                binding.noData.setVisibility(View.VISIBLE);
            }
            binding.progressBar.setVisibility(View.GONE);
        });
    }


    // cek role, hanya admin yang bisa CRUD produk kamera
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
                            binding.addCamera.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }


    /// HAPUSKAN ACTIVITY KETIKA SUDAH TIDAK DIGUNAKAN, AGAR MENGURANGI RISIKO MEMORY LEAKS
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}