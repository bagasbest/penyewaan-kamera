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


    private ActivityCameraBinding  binding;
    private FirebaseUser user;
    private CameraAdapter adapter;

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

    private void initRecylerView() {
        binding.rvCamera.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CameraAdapter();
        binding.rvCamera.setAdapter(adapter);
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}