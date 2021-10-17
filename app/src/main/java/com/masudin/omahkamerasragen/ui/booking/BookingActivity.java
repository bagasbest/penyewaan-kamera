package com.masudin.omahkamerasragen.ui.booking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.masudin.omahkamerasragen.R;
import com.masudin.omahkamerasragen.databinding.ActivityBookingBinding;
import com.masudin.omahkamerasragen.ui.camera.CameraAdapter;
import com.masudin.omahkamerasragen.ui.camera.CameraViewModel;

public class BookingActivity extends AppCompatActivity {

    private ActivityBookingBinding binding;
    private BookingAdapter adapter;
    private String category = "Kamera";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initRecylerView();
        initViewModel(category);

        // filter kategori
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.kategori, android.R.layout.simple_list_item_1);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        binding.bookingEt.setAdapter(adapter);
        binding.bookingEt.setOnItemClickListener((adapterView, view, i, l) -> {
            category = binding.bookingEt.getText().toString();
            initRecylerView();
            initViewModel(category);
        });


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

    private void initViewModel(String category) {
        // tampilkan daftar artikel di halaman artikel terkait pertanian
        BookingViewModel viewModel = new ViewModelProvider(this).get(BookingViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);
        if(category.equals("Kamera")) {
            viewModel.setListBookingCamera();
        } else {
            viewModel.setListBookingAksesoris();
        }
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