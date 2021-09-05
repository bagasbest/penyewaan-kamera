package com.masudin.omahkamerasragen.ui.booking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.masudin.omahkamerasragen.R;
import com.masudin.omahkamerasragen.databinding.ActivityBookingBinding;
import com.masudin.omahkamerasragen.ui.camera.CameraAdapter;
import com.masudin.omahkamerasragen.ui.camera.CameraViewModel;

public class BookingActivity extends AppCompatActivity {

    private ActivityBookingBinding binding;
    private BookingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initRecylerView();
        initViewModel();

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void initRecylerView() {
        binding.rvBooking.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BookingAdapter();
        binding.rvBooking.setAdapter(adapter);
    }

    private void initViewModel() {
        // tampilkan daftar artikel di halaman artikel terkait pertanian
        BookingViewModel viewModel = new ViewModelProvider(this).get(BookingViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.setListBooking();
        viewModel.getBooking().observe(this, bookingModelArrayList -> {
            if (bookingModelArrayList.size() > 0) {
                binding.noData.setVisibility(View.GONE);
                adapter.setData(bookingModelArrayList);
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